package com.xzf.blog.comment.biz.rpc;

import com.google.common.collect.Lists;
import com.xzf.blog.article.api.ArticleFeignApi;
import com.xzf.blog.article.dto.request.IdsRequest;
import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.framework.commons.response.Response;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ArticleRpcService {

    @Resource
    private ArticleFeignApi articleFeignApi;

    public List<FindIndexArticlePageListRspVO> getByIds(List<Long> articleIds) {
        Response<List<FindIndexArticlePageListRspVO>> articleResp = articleFeignApi.getByIds(IdsRequest.builder()
                .ids(articleIds).
                build());
        if (!articleResp.isSuccess()) {
            return Lists.newArrayList();
        }
        return articleResp.getData();
    }
}
