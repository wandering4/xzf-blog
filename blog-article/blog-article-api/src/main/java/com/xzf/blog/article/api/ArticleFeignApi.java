package com.xzf.blog.article.api;

import com.xzf.blog.article.constants.ApiConstants;
import com.xzf.blog.article.dto.IdsRequest;
import com.xzf.blog.article.dto.request.article.UpdateArticleSummaryRequest;
import com.xzf.blog.article.dto.response.article.FindIndexArticlePageListRspVO;
import com.xzf.blog.framework.commons.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface ArticleFeignApi {

    @PostMapping("/updateSummary")
    public Response<?> updateArticleSummary(@RequestBody UpdateArticleSummaryRequest req);

    @PostMapping("/getByIds")
    public Response<List<FindIndexArticlePageListRspVO>> getByIds(@RequestBody IdsRequest req);

}