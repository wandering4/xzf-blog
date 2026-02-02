package com.xzf.blog.article.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xzf.blog.article.biz.constant.RedisConstant;
import com.xzf.blog.article.biz.convert.ArticleConvert;
import com.xzf.blog.article.biz.convert.UserConvert;
import com.xzf.blog.article.biz.domain.dataobject.*;
import com.xzf.blog.article.biz.domain.mapper.*;
import com.xzf.blog.article.biz.enums.ArticleIsTopEnum;
import com.xzf.blog.article.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.article.biz.rpc.UserFeignService;
import com.xzf.blog.article.dto.response.article.FindCategoryListRspVO;
import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.article.biz.service.ArticleService;
import com.xzf.blog.article.biz.util.MarkdownHelper;
import com.xzf.blog.article.biz.util.MarkdownStatsUtil;
import com.xzf.blog.article.constants.MQConstants;
import com.xzf.blog.article.dto.request.IdsRequest;
import com.xzf.blog.article.dto.mq.ArticleMessage;
import com.xzf.blog.article.dto.request.article.*;
import com.xzf.blog.article.dto.response.article.FindArticleDetailRspVO;
import com.xzf.blog.article.dto.response.tag.FindTagListRspVO;
import com.xzf.blog.article.dto.vo.UserInfoVO;
import com.xzf.blog.framework.commons.enums.ResponseCodeEnum;
import com.xzf.blog.framework.commons.enums.RoleEnums;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.framework.commons.util.JsonUtils;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagMapper articleTagRelMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private UserFeignService userFeignService;

    private static Integer DETAIL_CACHE_TIMEOUT_MINUTES = 60;
    private static Integer PAGE_CACHE_TIMEOUT_MINUTES = 60;


    /**
     * 发布文章
     *
     * @param publishArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 1. VO 转 ArticleDO, 并保存
        ArticleDO articleDO = ArticleDO.builder()
                .authorId(LoginUserContextHolder.getUserId())
                .title(publishArticleReqVO.getTitle())
                .cover(publishArticleReqVO.getCover())
                .summary(publishArticleReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.insert(articleDO);

        // 拿到插入记录的主键 ID
        Long articleId = articleDO.getId();

        // 2. VO 转 ArticleContentDO，并保存
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .build();
        articleContentMapper.insert(articleContentDO);

        // 3. 处理文章关联的分类
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(BizResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryDO articleCategoryRelDO = ArticleCategoryDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        // 5.发送文章发布事件
        ArticleMessage articleMessage = ArticleMessage.builder()
                .articleId(articleId)
                .title(articleDO.getTitle())
                .content(publishArticleReqVO.getContent())
                .build();
        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(articleMessage)).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_PUBLISH_ARTICLE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【文章服务：发布文章】MQ 发送成功，SendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【文章服务：发布文章】MQ 发送异常: ", throwable);
            }
        });
        threadPoolTaskExecutor.execute(this::deletePageListCache);

        return Response.success();
    }


    @Override
    @SneakyThrows
    public PageResponse<FindIndexArticlePageListRspVO> findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        // ========== 缓存逻辑 start ==========
        // 生成缓存 key（将请求序列化的指纹作为 key）
        String pageCacheKey = RedisConstant.buildArticlePageListKey(JsonUtils.toJsonString(findIndexArticlePageListReqVO));

        // 尝试从缓存获取
        String cacheStr = (String) redisTemplate.opsForValue().get(pageCacheKey);
        if (cacheStr != null && !cacheStr.isEmpty()) {
            log.info("==> 分页列表缓存命中, cacheKey: {}", pageCacheKey);
            PageResponse<FindIndexArticlePageListRspVO> cachedResponse = JsonUtils.parseObject(cacheStr, PageResponse.class);
            if (cachedResponse != null) {
                return cachedResponse;
            }
        }
        // ========== 缓存逻辑 end ==========

        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();
        LocalDate startDate = findIndexArticlePageListReqVO.getStartCreateTime();
        LocalDate endDate = findIndexArticlePageListReqVO.getEndCreateTime();
        String title = findIndexArticlePageListReqVO.getTitle();
        List<Long> tagIds = findIndexArticlePageListReqVO.getTagIds();
        List<Long> categoryIds = findIndexArticlePageListReqVO.getCategoryIds();
        Long userId = findIndexArticlePageListReqVO.getUserId();
        boolean hasTagCondition = tagIds != null && !tagIds.isEmpty();
        boolean hasCategoryCondition = categoryIds != null && !categoryIds.isEmpty();

        // 1.根据tag条件查询对应的tag
        Set<Long> articleIdSetFromTags = new HashSet<>();
        List<ArticleTagDO> articleTagRelDOS = new ArrayList<>();
        if (hasTagCondition) {
            // 根据tagId查询文章ID范围
            articleTagRelDOS = articleTagRelMapper.selectByTagIds(tagIds);
            articleIdSetFromTags = articleTagRelDOS.stream()
                    .map(ArticleTagDO::getArticleId)
                    .collect(Collectors.toSet());
        }

        // 3.根据category条件查询对应的category
        Set<Long> articleIdSetFromCategories = new HashSet<>();
        List<ArticleCategoryDO> articleCategoryRelDOS = new ArrayList<>();
        if (hasCategoryCondition) {
            // 根据categoryId查询文章ID范围
            articleCategoryRelDOS = articleCategoryRelMapper.selectByCategoryIds(categoryIds);
            articleIdSetFromCategories = articleCategoryRelDOS.stream()
                    .map(ArticleCategoryDO::getArticleId)
                    .collect(Collectors.toSet());
        }

        // 5.根据articleId的限制和其他条件分页查询文章主体记录
        // 合并tag和category的articleId限制条件
        Set<Long> filteredArticleIds = new HashSet<>();
        if (hasTagCondition && hasCategoryCondition) {
            // 取交集：文章必须同时满足tag和category条件
            filteredArticleIds = articleIdSetFromTags.stream()
                    .filter(articleIdSetFromCategories::contains)
                    .collect(Collectors.toSet());
        } else if (hasTagCondition) {
            filteredArticleIds = articleIdSetFromTags;
        } else if (hasCategoryCondition) {
            filteredArticleIds = articleIdSetFromCategories;
        }
        if ((hasTagCondition || hasCategoryCondition) && filteredArticleIds.isEmpty()) {
            // ========== 空结果也缓存，防止缓存穿透 ==========
            PageResponse<FindIndexArticlePageListRspVO> emptyResponse = PageResponse.success(null, null);
            asyncLoadPageCache(pageCacheKey, JsonUtils.toJsonString(emptyResponse), 1);
            return emptyResponse;
        }

        List<Long> articleIdList = filteredArticleIds.isEmpty() ? null : new ArrayList<>(filteredArticleIds);

        // 分页查询文章主体记录
        Page<ArticleDO> articleDOPage = articleMapper.selectPageListWithArticleIds(current, size, title, startDate, endDate, articleIdList, userId);

        // 返回的分页数据
        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        List<FindIndexArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            CompletableFuture<Map<Long, UserInfoVO>> userInfoFuture = CompletableFuture.supplyAsync(() -> {
                List<Long> authorIds = articleDOS.stream()
                        .map(ArticleDO::getAuthorId)
                        .toList();
                return getUserInfoVOMap(authorIds);
            }, threadPoolTaskExecutor);


            // 拿到所有文章的 ID 集合
            List<Long> articleIds = articleDOS.stream().map(ArticleDO::getId).collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 查询所有分类
            List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
            Map<Long, String> categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

            if (!hasCategoryCondition) {
                // 根据文章 ID 批量查询所有关联记录
                articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);
            }

            Map<Long, UserInfoVO> userInfoMap = userInfoFuture.get(5, TimeUnit.SECONDS);
            // 文章 DO 转 VO
            vos = articleDOS.stream()
                    .map(articleDO -> {
                        FindIndexArticlePageListRspVO articleVO = ArticleConvert.INSTANCE.convertDO2VO(articleDO);
                        articleVO.setAuthorInfo(userInfoMap.get(articleDO.getAuthorId()));
                        return articleVO;
                    })
                    .collect(Collectors.toList());

            for (FindIndexArticlePageListRspVO vo : vos) {
                Long currArticleId = vo.getId();
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryDO> optional = articleCategoryRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryDO articleCategoryRelDO = optional.get();
                    Long categoryId = articleCategoryRelDO.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(categoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(categoryId)
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            }

            // 第三步：设置文章标签
            // 查询所有标签
            List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
            // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
            Map<Long, String> mapIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));

            // 拿到所有文章的标签关联记录
            if (!hasTagCondition) {
                articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);
            }

            for (FindIndexArticlePageListRspVO vo : vos) {
                Long currArticleId = vo.getId();
                // 过滤出当前文章的标签关联记录
                List<ArticleTagDO> articleTagRelDOList = articleTagRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());

                List<FindTagListRspVO> findTagListRspVOS = Lists.newArrayList();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelDOList.forEach(articleTagRelDO -> {
                    Long tagId = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagId);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(tagId)
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            }
        }

        PageResponse<FindIndexArticlePageListRspVO> result = PageResponse.success(articleDOPage, vos);

        // ========== 缓存逻辑: 异步写入缓存并维护批次 key 集合 ==========
        final String finalPageCacheKey = pageCacheKey;
        threadPoolTaskExecutor.execute(() -> {
            try {
                // 1. 写入分页缓存
                redisTemplate.opsForValue().set(finalPageCacheKey, JsonUtils.toJsonString(result), PAGE_CACHE_TIMEOUT_MINUTES, java.util.concurrent.TimeUnit.MINUTES);

                // 2. 将缓存 key 注册到批次集合中（方便后续批量删除）
                // 注册到全量批次
                redisTemplate.opsForSet().add(RedisConstant.buildArticlePageBatchKeysKeyForAll(), finalPageCacheKey);

                log.info("==> 分页列表缓存写入成功, cacheKey: {}", finalPageCacheKey);
            } catch (Exception e) {
                log.error("==> 分页列表缓存写入失败, cacheKey: {}", finalPageCacheKey, e);
            }
        });

        return result;
    }

    private Map<Long, UserInfoVO> getUserInfoVOMap(List<Long> authorIds) {
        List<FindUserByIdResponse> resps = userFeignService.findByIds(authorIds);
        if (resps == null) return new HashMap<>();
        return resps.stream()
                .map(resp -> UserInfoVO.builder()
                        .id(resp.getId())
                        .userName(resp.getUserName())
                        .avatarUrl(resp.getAvatarUrl())
                        .build())
                .collect(Collectors.toMap(UserInfoVO::getId, userInfo -> userInfo, (a, b) -> a));
    }

    @Override
    public Response<List<FindIndexArticlePageListRspVO>> getByIds(IdsRequest req) {
        // 1. 获取请求的文章 ID 集合
        List<Long> articleIds = req.getIds();
        if (CollectionUtils.isEmpty(articleIds)) {
            return Response.success(Collections.emptyList());
        }

        // 2. 批量查询文章
        List<ArticleDO> articleDOS = articleMapper.selectBatchIds(articleIds);
        if (CollectionUtils.isEmpty(articleDOS)) {
            return Response.success(Collections.emptyList());
        }

        // 3. 文章 DO 转 VO
        List<FindIndexArticlePageListRspVO> vos = articleDOS.stream()
                .map(ArticleConvert.INSTANCE::convertDO2VO)
                .collect(Collectors.toList());

        // 4. 查询所有分类，转成 Map 方便后续根据分类 ID 获取名称
        List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
        Map<Long, String> categoryIdNameMap = categoryDOS.stream()
                .collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));

        // 5. 批量查询文章-分类关联记录
        List<ArticleCategoryDO> articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);

        // 6. 设置文章所属分类
        for (FindIndexArticlePageListRspVO vo : vos) {
            Long currArticleId = vo.getId();
            Optional<ArticleCategoryDO> optional = articleCategoryRelDOS.stream()
                    .filter(rel -> Objects.equals(rel.getArticleId(), currArticleId))
                    .findAny();

            if (optional.isPresent()) {
                ArticleCategoryDO articleCategoryRelDO = optional.get();
                Long categoryId = articleCategoryRelDO.getCategoryId();
                String categoryName = categoryIdNameMap.get(categoryId);

                FindCategoryListRspVO categoryVO = FindCategoryListRspVO.builder()
                        .id(categoryId)
                        .name(categoryName)
                        .build();
                vo.setCategory(categoryVO);
            }
        }

        // 7. 查询所有标签，转成 Map 方便后续根据标签 ID 获取名称
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());
        Map<Long, String> tagIdNameMap = tagDOS.stream()
                .collect(Collectors.toMap(TagDO::getId, TagDO::getName));

        // 8. 批量查询文章-标签关联记录
        List<ArticleTagDO> articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);

        // 9. 设置文章标签
        for (FindIndexArticlePageListRspVO vo : vos) {
            Long currArticleId = vo.getId();
            List<ArticleTagDO> articleTagRelDOList = articleTagRelDOS.stream()
                    .filter(rel -> Objects.equals(rel.getArticleId(), currArticleId))
                    .collect(Collectors.toList());

            List<FindTagListRspVO> tagVOS = articleTagRelDOList.stream()
                    .map(articleTagRelDO -> {
                        Long tagId = articleTagRelDO.getTagId();
                        String tagName = tagIdNameMap.get(tagId);
                        return FindTagListRspVO.builder()
                                .id(tagId)
                                .name(tagName)
                                .build();
                    })
                    .collect(Collectors.toList());
            vo.setTags(tagVOS);
        }

        return Response.success(vos);
    }

    @Override
    public PageResponse<FindIndexArticlePageListRspVO> findPersonalArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        findIndexArticlePageListReqVO.setUserId(LoginUserContextHolder.getUserId());
        return findArticlePageList(findIndexArticlePageListReqVO);
    }

    @Override
    @SneakyThrows
    public Response<FindArticleDetailRspVO> findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getId();

        String redisCacheKey = RedisConstant.buildArticleDetailKey(articleId);
        String cache = (String) redisTemplate.opsForValue().get(redisCacheKey);
        FindArticleDetailRspVO vo = null;

        if (cache != null && !cache.isEmpty()) {
            vo = JsonUtils.parseObject(cache, FindArticleDetailRspVO.class);
        } else {
            ArticleDO articleDO = articleMapper.selectById(articleId);
            // 判断文章是否存在
            if (Objects.isNull(articleDO)) {
                log.warn("==> 该文章不存在, articleId: {}", articleId);
                // 防止缓存穿透
                asyncLoadCache(redisCacheKey, "null", 5, TimeUnit.SECONDS);
                throw new BizException(BizResponseCodeEnum.ARTICLE_NOT_FOUND);
            }
            CompletableFuture<UserInfoVO> userInfoFuture = CompletableFuture.supplyAsync(() -> {
                Long authorId = articleDO.getAuthorId();
                Map<Long, UserInfoVO> userInfoVOMap = getUserInfoVOMap(Collections.singletonList(authorId));
                return userInfoVOMap.get(authorId);
            }, threadPoolTaskExecutor);

            // 查询正文
            ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);
            String content = articleContentDO.getContent();

            // 计算 md 正文字数
            Integer totalWords = MarkdownStatsUtil.calculateWordCount(content);

            // DO 转 VO
            vo = FindArticleDetailRspVO.builder()
                    .title(articleDO.getTitle())
                    .summary(articleDO.getSummary())
                    .content(MarkdownHelper.convertMarkdown2Html(content))
                    .readNum(articleDO.getViewCount())
                    .totalWords(totalWords)
                    .readTime(MarkdownStatsUtil.calculateReadingTime(totalWords))
                    .createTime(articleDO.getCreateTime())
                    .updateTime(articleDO.getUpdateTime())
                    .build();

            // 查询所属分类
            ArticleCategoryDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
            CategoryDO categoryDO = categoryMapper.selectById(articleCategoryRelDO.getCategoryId());
            vo.setCategoryId(categoryDO.getId());
            vo.setCategoryName(categoryDO.getName());

            // 查询标签
            List<ArticleTagDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
            List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagDO::getTagId).collect(Collectors.toList());
            List<TagDO> tagDOS = tagMapper.selectBatchIds(tagIds);

            // 标签 DO 转 VO
            List<FindTagListRspVO> tagVOS = tagDOS.stream()
                    .map(tagDO -> FindTagListRspVO.builder().id(tagDO.getId()).name(tagDO.getName()).build())
                    .collect(Collectors.toList());
            vo.setTags(tagVOS);

            UserInfoVO userInfo = userInfoFuture.get(5, TimeUnit.SECONDS);
            vo.setAuthorInfo(userInfo);

            // 异步加载redis缓存
            asyncLoadCache(redisCacheKey, JsonUtils.toJsonString(vo), DETAIL_CACHE_TIMEOUT_MINUTES + new Random().nextInt(DETAIL_CACHE_TIMEOUT_MINUTES), TimeUnit.MINUTES);
        }

        // 发布文章阅读事件
        Message<Long> message = MessageBuilder.withPayload(articleId).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_READ_ARTICLE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【文章服务：阅读文章】MQ 发送成功，SendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【文章服务：阅读文章】MQ 发送异常: ", throwable);
            }
        });

        return Response.success(vo);
    }

    private void asyncLoadCache(String redisCacheKey, String cache, Integer timeout, TimeUnit timeUnit) {
        threadPoolTaskExecutor.execute(() -> {
            redisTemplate.opsForValue().set(redisCacheKey, cache, timeout, timeUnit);
        });
    }

    /**
     * 异步加载分页缓存
     */
    private void asyncLoadPageCache(String redisCacheKey, String cache, Integer timeoutMinutes) {
        threadPoolTaskExecutor.execute(() -> {
            redisTemplate.opsForValue().set(redisCacheKey, cache, timeoutMinutes, TimeUnit.MINUTES);
        });
    }


    /**
     * 删除文章
     *
     * @param deleteArticleReqVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getId();

        // 鉴权
        checkRootOrSelf(articleId);

        // 1. 删除文章
        articleMapper.deleteById(articleId);

        // 2. 删除文章内容
        articleContentMapper.deleteByArticleId(articleId);

        // 3. 删除文章-分类关联记录
        articleCategoryRelMapper.deleteByArticleId(articleId);

        // 4. 删除文章-标签关联记录
        articleTagRelMapper.deleteByArticleId(articleId);

        // 5. 发布文章删除事件
        Message<Long> message = MessageBuilder.withPayload(articleId).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_DELETE_ARTICLE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【文章服务：删除文章】MQ 发送成功，SendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【文章服务：删除文章】MQ 发送异常: ", throwable);
            }
        });
        threadPoolTaskExecutor.execute(() -> {
            deletePageListCache();
            deleteDetailCache(articleId);
        });

        return Response.success();
    }

    private void checkRootOrSelf(Long articleId) {
        Long userId = LoginUserContextHolder.getUserId();
        String role = LoginUserContextHolder.getUserRole();
        ArticleDO articleDO = articleMapper.selectById(articleId);
        if (Objects.isNull(articleDO)) {
            throw new BizException(BizResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        if (!role.equals(RoleEnums.ROOT.getName()) && !articleDO.getAuthorId().equals(userId)) {
            throw new BizException(ResponseCodeEnum.NOT_HAVE_PERMISSION);
        }
    }

    /**
     * 更新文章
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response updateArticle(UpdateArticleReqVO req) {
        Long articleId = req.getId();
        checkRootOrSelf(articleId);

        // 1. VO 转 ArticleDO, 并更新
        ArticleDO articleDO = ArticleDO.builder()
                .id(articleId)
                .title(req.getTitle())
                .cover(req.getCover())
                .updateTime(LocalDateTime.now())
                .build();
        int count = articleMapper.updateById(articleDO);

        // 根据更新是否成功，来判断该文章是否存在
        if (count == 0) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(BizResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. VO 转 ArticleContentDO，并更新
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(req.getContent())
                .build();
        articleContentMapper.updateByArticleId(articleContentDO);


        // 3. 更新文章分类
        Long categoryId = req.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(BizResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先删除该文章关联的分类记录，再插入新的关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryDO articleCategoryRelDO = ArticleCategoryDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        // 先删除该文章对应的标签
        articleTagRelMapper.deleteByArticleId(articleId);
        List<String> publishTags = req.getTags();
        insertTags(articleId, publishTags);

        // 发布文章修改消息
        ArticleMessage articleMessage = ArticleMessage.builder()
                .articleId(articleId)
                .title(articleDO.getTitle())
                .content(req.getContent())
                .build();
        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(articleMessage)).build();
        rocketMQTemplate.asyncSend(MQConstants.TOPIC_UPDATE_ARTICLE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==> 【文章服务：更新文章】MQ 发送成功，SendResult: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==> 【文章服务：更新文章】MQ 发送异常: ", throwable);
            }
        });
        threadPoolTaskExecutor.execute(() -> {
            deleteDetailCache(articleId);
            deletePageListCache();
        });

        return Response.success();
    }

    @Override
    public Response<?> updateArticleSummary(UpdateArticleSummaryRequest req) {
        ArticleDO articleDO = ArticleDO.builder()
                .id(req.getId())
                .summary(req.getSummary())
                .build();
        int count = articleMapper.updateById(articleDO);
        // 根据更新是否成功，来判断该文章是否存在
        if (count == 0) {
            log.warn("==> 该文章不存在, articleId: {}", req.getId());
            throw new BizException(BizResponseCodeEnum.ARTICLE_NOT_FOUND);
        }
        deleteDetailCache(req.getId());
        return Response.success();
    }

    /**
     * 更新文章是否置顶
     *
     * @param updateArticleIsTopReqVO
     * @return
     */
    @Override
    public Response updateArticleIsTop(UpdateArticleIsTopReqVO updateArticleIsTopReqVO) {
        Long articleId = updateArticleIsTopReqVO.getId();

        // 置顶枚举类
        ArticleIsTopEnum isTopEnum = ArticleIsTopEnum.valueOf(updateArticleIsTopReqVO.getIsTop());

        // 更新该篇文章的权重值
        articleMapper.updateById(ArticleDO.builder()
                .id(articleId)
                .isTop(isTopEnum.getCode())
                .build());

        deletePageListCache();

        return Response.success();
    }


    /**
     * 保存标签
     *
     * @param articleId
     * @param publishTags
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        // 筛选提交的标签（表中不存在的标签）
        List<String> notExistTags = null;
        // 筛选提交的标签（表中已存在的标签）
        List<String> existedTags = null;

        // 查询出所有标签
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // 如果表中还没有添加任何标签
        if (CollectionUtils.isEmpty(tagDOS)) {
            notExistTags = publishTags;
        } else {
            List<String> tagIds = tagDOS.stream().map(tagDO -> String.valueOf(tagDO.getId())).collect(Collectors.toList());
            // 表中已添加相关标签，则需要筛选
            // 通过标签 ID 来筛选，包含对应 ID 则表示提交的标签是表中存在的
            existedTags = publishTags.stream().filter(publishTag -> tagIds.contains(publishTag)).collect(Collectors.toList());
            // 否则则是不存在的
            notExistTags = publishTags.stream().filter(publishTag -> !tagIds.contains(publishTag)).collect(Collectors.toList());

            // 还有一种可能：按字符串名称提交上来的标签，也有可能是表中已存在的，比如表中已经有了 Java 标签，用户提交了个 java 小写的标签，需要内部装换为 Java 标签
            Map<String, Long> tagNameIdMap = tagDOS.stream().collect(Collectors.toMap(tagDO -> tagDO.getName().toLowerCase(), TagDO::getId));

            // 使用迭代器进行安全的删除操作
            Iterator<String> iterator = notExistTags.iterator();
            while (iterator.hasNext()) {
                String notExistTag = iterator.next();
                // 转小写, 若 Map 中相同的 key，则表示该新标签是重复标签
                if (tagNameIdMap.containsKey(notExistTag.toLowerCase())) {
                    // 从不存在的标签集合中清除
                    iterator.remove();
                    // 并将对应的 ID 添加到已存在的标签集合
                    existedTags.add(String.valueOf(tagNameIdMap.get(notExistTag.toLowerCase())));
                }
            }
        }

        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (!CollectionUtils.isEmpty(existedTags)) {
            List<ArticleTagDO> articleTagRelDOS = Lists.newArrayList();
            existedTags.forEach(tagId -> {
                ArticleTagDO articleTagRelDO = ArticleTagDO.builder()
                        .articleId(articleId)
                        .tagId(Long.valueOf(tagId))
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.batchInsert(articleTagRelDOS);
        }

        // 将提交的上来的，不存在于表中的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)) {
            // 需要先将标签入库，拿到对应标签 ID 后，再把文章-标签关联关系入库
            List<ArticleTagDO> articleTagRelDOS = Lists.newArrayList();
            notExistTags.forEach(tagName -> {
                TagDO tagDO = TagDO.builder()
                        .name(tagName)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build();

                tagMapper.insert(tagDO);

                // 拿到保存的标签 ID
                Long tagId = tagDO.getId();

                // 文章-标签关联关系
                ArticleTagDO articleTagRelDO = ArticleTagDO.builder()
                        .articleId(articleId)
                        .tagId(tagId)
                        .build();
                articleTagRelDOS.add(articleTagRelDO);
            });
            // 批量插入
            articleTagRelMapper.batchInsert(articleTagRelDOS);
        }
    }

    private void deletePageListCache() {
        try {
            // 获取全量分页缓存批次中的所有缓存 key
            String batchKey = RedisConstant.buildArticlePageBatchKeysKeyForAll();
            Set<Object> cacheKeys = redisTemplate.opsForSet().members(batchKey);

            if (cacheKeys != null && !cacheKeys.isEmpty()) {
                // 批量删除分页缓存
                Set<String> keysToDelete = new HashSet<>();
                for (Object key : cacheKeys) {
                    keysToDelete.add(key.toString());
                }
                redisTemplate.delete(keysToDelete);
                log.info("DeleteArticleCacheConsumer删除分页缓存数量: {}", keysToDelete.size());
            }

            // 清空批次集合
            redisTemplate.delete(batchKey);

        } catch (Exception e) {
            log.error("DeleteArticleCacheConsumer删除分页缓存失败", e);
        }
    }

    private void deleteDetailCache(Long articleId){
        String detailCacheKey = RedisConstant.buildArticleDetailKey(articleId);
        redisTemplate.delete(detailCacheKey);
    }

}
