package com.xzf.blog.article.api;

import com.xzf.blog.article.constants.ApiConstants;
import com.xzf.blog.article.dto.request.article.UpdateArticleSummaryRequest;
import com.xzf.blog.framework.commons.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface ArticleFeignApi {

    String PREFIX = "/article";

    @PostMapping(PREFIX + "/updateSummary")
    public Response<?> updateArticleSummary(@RequestBody UpdateArticleSummaryRequest req);

}