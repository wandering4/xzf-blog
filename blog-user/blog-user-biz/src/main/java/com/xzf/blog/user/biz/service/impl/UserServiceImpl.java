package com.xzf.blog.user.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.PageResponse;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.framework.commons.util.JsonUtils;
import com.xzf.blog.framework.commons.util.ParamUtils;
import com.xzf.blog.user.biz.model.vo.request.UpdateRoleRequest;
import com.xzf.blog.user.constant.MQConstants;
import com.xzf.blog.user.biz.constant.RedisKeyConstants;
import com.xzf.blog.user.biz.convert.UserConvert;
import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.dataobject.UserDO;
import com.xzf.blog.user.biz.domain.dataobject.UserRoleDO;
import com.xzf.blog.user.biz.domain.mapper.RoleDOMapper;
import com.xzf.blog.user.biz.domain.mapper.UserDOMapper;
import com.xzf.blog.user.biz.domain.mapper.UserRoleDOMapper;
import com.xzf.blog.framework.commons.enums.RoleEnums;
import com.xzf.blog.user.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.user.biz.model.vo.request.UpdatePasswordRequest;
import com.xzf.blog.user.biz.model.vo.request.UpdateUserInfoRequest;
import com.xzf.blog.user.biz.rpc.OssRpcService;
import com.xzf.blog.user.biz.service.UserService;
import com.xzf.blog.user.dto.req.*;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import com.xzf.blog.user.dto.resp.FindUserByPhoneRspDTO;
import com.xzf.blog.user.dto.resp.FindUserPageListRspVO;
import com.xzf.blog.user.dto.resp.LoginUserInfoResponse;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private OssRpcService ossRpcService;

    @Resource
    private UserRoleDOMapper userRoleDOMapper;

    @Resource
    private RoleDOMapper roleDOMapper;

    @Resource
    private PasswordEncoder passwordEncoder;


    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    /**
     * 更新用户信息
     *
     * @param updateUserInfoRequest
     * @return
     */
    @Override
    public Response<?> updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest) {
        // 被更新的用户 ID
        Long userId = LoginUserContextHolder.getUserId();
        UserDO userDO = userDOMapper.selectById(userId);

        boolean needUpdate = false;

        // 头像
        String avatarUrl = updateUserInfoRequest.getAvatarUrl();
        if (ObjectUtils.isNotEmpty(avatarUrl) && !avatarUrl.equals(userDO.getAvatarUrl())) {
            userDO.setAvatarUrl(avatarUrl);
            needUpdate = true;
        }

        // 昵称
        String nickname = updateUserInfoRequest.getNickname();
        if (StringUtils.isNotBlank(nickname) && !nickname.equals(userDO.getUsername())) {
            Preconditions.checkArgument(ParamUtils.checkNickname(nickname), BizResponseCodeEnum.NICK_NAME_VALID_FAIL.getErrorMessage());
            userDO.setUsername(nickname);
            needUpdate = true;
        }


        // 个人简介
        String introduction = updateUserInfoRequest.getIntroduction();
        if (StringUtils.isNotBlank(introduction) && !introduction.equals(userDO.getIntroduction())) {
            Preconditions.checkArgument(ParamUtils.checkLength(introduction, 100), BizResponseCodeEnum.INTRODUCTION_VALID_FAIL.getErrorMessage());
            userDO.setIntroduction(introduction);
            needUpdate = true;
        }

        if (needUpdate) {

            // 删除用户缓存
            deleteUserRedisCache(userId);

            // 更新用户信息
            userDO.setUpdateTime(LocalDateTime.now());
            userDOMapper.updateById(userDO);

            // 延时双删
            sendDelayDeleteUserRedisCacheMQ(userId);

        }
        return Response.success();

    }

    /**
     * 删除 Redis 中的用户缓存
     * @param userId
     */
    private void deleteUserRedisCache(Long userId) {
        // 构建 Redis Key
        String userInfoRedisKey = RedisKeyConstants.buildUserInfoKey(userId);
        String userProfileRedisKey = RedisKeyConstants.buildUserProfileKey(userId);

        // 批量删除
        redisTemplate.delete(Arrays.asList(userInfoRedisKey, userProfileRedisKey));
    }

    /**
     * 异步发送延时消息
     * @param userId
     */
    private void sendDelayDeleteUserRedisCacheMQ(Long userId) {
        Message<String> message = MessageBuilder.withPayload(String.valueOf(userId))
                .build();

        rocketMQTemplate.asyncSend(MQConstants.TOPIC_DELAY_DELETE_USER_REDIS_CACHE, message,
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("## 延时删除 Redis 用户缓存消息发送成功...");
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("## 延时删除 Redis 用户缓存消息发送失败...", e);
                    }
                },
                3000, // 超时时间
                1 // 延迟级别，1 表示延时 1s
        );
    }

    /**
     * 用户注册
     *
     * @param registerUserRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Long> register(RegisterUserRequest registerUserRequest) {
        String phone = registerUserRequest.getPhone();

        // 先判断该手机号是否已被注册
        UserDO userDO1 = userDOMapper.selectByPhone(phone);

        log.info("==> 用户是否注册, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO1));

        // 若已注册，则直接返回用户 ID
        if (Objects.nonNull(userDO1)) {
            return Response.success(userDO1.getId());
        }

        // 否则注册新用户
        UserDO userDO = UserDO.builder()
                .phone(phone)
                .username("momo") // 自动生成昵称
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 添加入库
        userDOMapper.insert(userDO);

        // 获取刚刚添加入库的用户 ID
        Long userId = userDO.getId();

        // 给该用户分配一个默认角色
        UserRoleDO userRoleDO = UserRoleDO.builder()
                .userId(userId)
                .roleId(RoleEnums.COMMON_USER.getId())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        userRoleDOMapper.insert(userRoleDO);


        return Response.success(userId);
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param findUserByPhoneRequest
     * @return
     */
    @Override
    public FindUserByPhoneRspDTO findByPhone(FindUserByPhoneRequest findUserByPhoneRequest) {
        String phone = findUserByPhoneRequest.getPhone();

        UserDO userDO = userDOMapper.selectByPhone(phone);

        // 判空
        if (Objects.isNull(userDO)) {
            throw new BizException(BizResponseCodeEnum.USER_NOT_FOUND);
        }

        // 构建返参
        return FindUserByPhoneRspDTO.builder()
                .id(userDO.getId())
                .password(userDO.getPassword())
                .build();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        // 新密码
        String newPassword = updatePasswordRequest.getNewPassword();
        // 密码加密
        String encodePassword = passwordEncoder.encode(newPassword);

        // 获取当前请求对应的用户 ID
        Long userId = LoginUserContextHolder.getUserId();

        UserDO userDO = UserDO.builder()
                .id(userId)
                .password(encodePassword)
                .updateTime(LocalDateTime.now())
                .build();
        userDOMapper.updateById(userDO);

        return Response.success();
    }

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param userIdRequest
     * @return
     */
    @Override
    public Response<FindUserByIdResponse> findById(UserIdRequest userIdRequest) {
        Long userId = userIdRequest.getId();

        // redis缓存
        String userInfoRedisKey = RedisKeyConstants.buildUserInfoKey(userId);
        String userInfoRedisValue = (String) redisTemplate.opsForValue().get(userInfoRedisKey);

        // 若 Redis 缓存中存在该用户信息
        if (StringUtils.isNotBlank(userInfoRedisValue)) {
            // 将存储的 Json 字符串转换成对象，并返回
            FindUserByIdResponse findUserByIdRspDTO = JsonUtils.parseObject(userInfoRedisValue, FindUserByIdResponse.class);

            return Response.success(findUserByIdRspDTO);
        }

        // 否则, 从数据库中查询
        UserDO userDO = userDOMapper.selectById(userId);

        // 判空
        if (Objects.isNull(userDO)) {
            threadPoolTaskExecutor.execute(() -> {
                // 防止缓存穿透，将空数据存入 Redis 缓存 (过期时间不宜设置过长)
                // 保底1分钟 + 随机秒数 避免缓存穿透
                long expireSeconds = 60 + RandomUtil.randomInt(60);
                redisTemplate.opsForValue().set(userInfoRedisKey, "null", expireSeconds, TimeUnit.SECONDS);
            });
            throw new BizException(BizResponseCodeEnum.USER_NOT_FOUND);
        }

        // 构建返参
        FindUserByIdResponse findUserByIdResponse = FindUserByIdResponse.builder()
                .id(userDO.getId())
                .userName(userDO.getUsername())
                .avatarUrl(userDO.getAvatarUrl())
                .introduction(userDO.getIntroduction())
                .build();

        // 异步将用户信息存入 Redis 缓存，提升响应速度
        threadPoolTaskExecutor.submit(() -> {
            // 过期时间（保底1天 + 随机秒数，将缓存过期时间打散，防止同一时间大量缓存失效，导致数据库压力太大）
            long expireSeconds = 60 * 60 * 24 + RandomUtil.randomInt(60 * 60 * 24);
            redisTemplate.opsForValue()
                    .set(userInfoRedisKey, JsonUtils.toJsonString(findUserByIdResponse), expireSeconds, TimeUnit.SECONDS);
        });

        return Response.success(findUserByIdResponse);
    }


    /**
     * 批量根据用户 ID 查询用户信息
     *
     * @param findUsersByIdsReqDTO
     * @return
     */
    @Override
    public Response<List<FindUserByIdResponse>> findByIds(FindUsersByIdsReqDTO findUsersByIdsReqDTO) {
        // 需要查询的用户 ID 集合
        List<Long> userIds = findUsersByIdsReqDTO.getIds().stream().distinct().toList();

        // 构建 Redis Key 集合
        List<String> redisKeys = userIds.stream()
                .map(RedisKeyConstants::buildUserInfoKey)
                .toList();

        // 先从 Redis 缓存中查, multiGet 批量查询提升性能
        List<Object> redisValues = redisTemplate.opsForValue().multiGet(redisKeys);
        // 如果缓存中不为空
        if (CollUtil.isNotEmpty(redisValues)) {
            // 过滤掉为空的数据
            redisValues = redisValues.stream().filter(Objects::nonNull).toList();
        }

        // 返参
        List<FindUserByIdResponse> findUserByIdRspDTOS = Lists.newArrayList();

        // 将过滤后的缓存集合，转换为 DTO 返参实体类
        if (CollUtil.isNotEmpty(redisValues)) {
            findUserByIdRspDTOS = redisValues.stream()
                    .map(value -> JsonUtils.parseObject(String.valueOf(value), FindUserByIdResponse.class))
                    .collect(Collectors.toList());
        }

        // 如果被查询的用户信息，都在 Redis 缓存中, 则直接返回
        if (CollUtil.size(userIds) == CollUtil.size(findUserByIdRspDTOS)) {
            return Response.success(findUserByIdRspDTOS);
        }

        // 还有另外两种情况：一种是缓存里没有用户信息数据，还有一种是缓存里数据不全，需要从数据库中补充
        // 筛选出缓存里没有的用户数据，去查数据库
        List<Long> userIdsNeedQuery = null;

        if (CollUtil.isNotEmpty(findUserByIdRspDTOS)) {
            // 将 findUserInfoByIdRspDTOS 集合转 Map
            Map<Long, FindUserByIdResponse> map = findUserByIdRspDTOS.stream()
                    .collect(Collectors.toMap(FindUserByIdResponse::getId, p -> p));

            // 筛选出需要查 DB 的用户 ID
            userIdsNeedQuery = userIds.stream()
                    .filter(id -> Objects.isNull(map.get(id)))
                    .toList();
        } else { // 缓存中一条用户信息都没查到，则提交的用户 ID 集合都需要查数据库
            userIdsNeedQuery = userIds;
        }

        // 从数据库中批量查询
        List<UserDO> userDOS = userDOMapper.selectBatchIds(userIdsNeedQuery);

        List<FindUserByIdResponse> findUserByIdRspDTOS2 = null;

        // 若数据库查询的记录不为空
        if (CollUtil.isNotEmpty(userDOS)) {
            // DO 转 DTO
            findUserByIdRspDTOS2 = userDOS.stream()
                    .map(userDO -> FindUserByIdResponse.builder()
                            .id(userDO.getId())
                            .userName(userDO.getUsername())
                            .avatarUrl(userDO.getAvatarUrl())
                            .introduction(userDO.getIntroduction())
                            .build())
                    .collect(Collectors.toList());


            // 异步线程将用户信息同步到 Redis 中
            List<FindUserByIdResponse> finalFindUserByIdRspDTOS = findUserByIdRspDTOS2;
            threadPoolTaskExecutor.submit(() -> {
                // DTO 集合转 Map
                Map<Long, FindUserByIdResponse> map = finalFindUserByIdRspDTOS.stream()
                        .collect(Collectors.toMap(FindUserByIdResponse::getId, p -> p));

                // 执行 pipeline 操作
                redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                    for (UserDO userDO : userDOS) {
                        Long userId = userDO.getId();

                        // 用户信息缓存 Redis Key
                        String userInfoRedisKey = RedisKeyConstants.buildUserInfoKey(userId);

                        // DTO 转 JSON 字符串
                        FindUserByIdResponse findUserInfoByIdRspDTO = map.get(userId);
                        String value = JsonUtils.toJsonString(findUserInfoByIdRspDTO);

                        // 过期时间（保底1天 + 随机秒数，将缓存过期时间打散，防止同一时间大量缓存失效，导致数据库压力太大）
                        long expireSeconds = 60 * 60 * 24 + RandomUtil.randomInt(60 * 60 * 24);
                        redisTemplate.opsForValue().set(userInfoRedisKey, value, expireSeconds, TimeUnit.SECONDS);
                    }
                    return null;
                });
            });
        }

        // 合并数据
        if (CollUtil.isNotEmpty(findUserByIdRspDTOS2)) {
            findUserByIdRspDTOS.addAll(findUserByIdRspDTOS2);
        }

        return Response.success(findUserByIdRspDTOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> deleteUser(UserIdRequest req) {
        Long userId = req.getId();

        userRoleDOMapper.deleteByUserId(userId);
        userDOMapper.deleteById(userId);

        // redis缓存
        String userInfoRedisKey = RedisKeyConstants.buildUserInfoKey(userId);
        redisTemplate.delete(userInfoRedisKey);

        // 发送用户删除事件
        Message<Long> message = MessageBuilder.withPayload(userId).build();
        rocketMQTemplate.asyncSend(MQConstants.USER_DELETE, message,
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("## 用户删除消息发送成功...");
                    }

                    @Override
                    public void onException(Throwable e) {
                        log.error("## 用户删除消息发送失败...", e);
                    }
                }
        );
        return Response.success();
    }

    @Override
    public Response<LoginUserInfoResponse> getLoginUserInfo() {
        Long userId = LoginUserContextHolder.getUserId();
        String role = LoginUserContextHolder.getUserRole();

        FindUserByIdResponse data = findById(UserIdRequest.builder().id(userId).build()).getData();
        LoginUserInfoResponse loginUserInfoResponse = LoginUserInfoResponse.builder()
                .userId(userId)
                .role(role)
                .userName(data.getUserName())
                .avatarUrl(data.getAvatarUrl())
                .build();
        return Response.success(loginUserInfoResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> updateRole(UpdateRoleRequest req) {
        Long userId = req.getUserId();
        Long roleId = req.getRoleId();

        RoleEnums role = RoleEnums.getEnum(roleId);
        if (Objects.isNull(role)) {
            throw new BizException(BizResponseCodeEnum.ROLE_NOT_FOUND);
        }

        LambdaUpdateWrapper<UserRoleDO> updateWrapper = new LambdaUpdateWrapper<UserRoleDO>()
                .set(UserRoleDO::getRoleId, roleId)
                .eq(UserRoleDO::getUserId, userId);
        int update = userRoleDOMapper.update(updateWrapper);
        if (update == 0) {
            throw new BizException(BizResponseCodeEnum.USER_NOT_FOUND);
        }
        return Response.success();
    }

    @Override
    public PageResponse<FindUserPageListRspVO> findUserPage(FindUserPageRequest req) {
        Long current = req.getCurrent();
        Long size = req.getSize();
        String name = req.getName();
        Long userId = LoginUserContextHolder.getUserId();

        // 查询用户表
        Page<UserDO> commentDOPage = userDOMapper.selectPageList(current, size, name, userId);
        List<UserDO> userDOs = commentDOPage.getRecords();

        // DO 转 VO
        List<FindUserPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(userDOs)) {
            // 查询所有相关角色
            List<Long> userIds = userDOs.stream().map(UserDO::getId).toList();
            List<UserRoleDO> userRoleDOS = userRoleDOMapper.selectByUserIds(userIds);
            List<Long> roleIds = userRoleDOS.stream().distinct().map(UserRoleDO::getRoleId).toList();
            List<RoleDO> roles = roleDOMapper.selectBatchIds(roleIds);

            // 构建 <角色id, 角色对象> 映射
            Map<Long, RoleDO> roleMap = roles.stream()
                    .collect(Collectors.toMap(
                            RoleDO::getId,  // 使用角色ID作为key
                            role -> role,   // 角色对象本身作为value
                            (existing, replacement) -> existing  // 如果有重复key，保留现有的
                    ));
            // 构建<用户id，角色>映射
            Map<Long, RoleDO> userRoleMapSingle = userRoleDOS.stream()
                    .collect(Collectors.toMap(
                            UserRoleDO::getUserId,
                            userRoleDO -> roleMap.get(userRoleDO.getRoleId()),
                            (existing, replacement) -> existing  // 如果有重复用户ID，保留现有的角色
                    ));

            vos = userDOs.stream()
                    .map(userDO-> UserConvert.userDOtoPageVO(userDO,userRoleMapSingle.get(userDO.getId())))
                    .toList();
        }
        return PageResponse.success(commentDOPage, vos);
    }
}
