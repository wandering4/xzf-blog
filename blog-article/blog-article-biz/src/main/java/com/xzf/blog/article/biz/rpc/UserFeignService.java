package com.xzf.blog.article.biz.rpc;

import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.api.UserFeignApi;
import com.xzf.blog.user.dto.req.FindUsersByIdsReqDTO;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserFeignService {

    @Autowired
    private UserFeignApi userFeignApi;

    public List<FindUserByIdResponse> findByIds(List<Long> userIds) {
        Response<List<FindUserByIdResponse>> response = userFeignApi.findByIds(FindUsersByIdsReqDTO.builder()
                .ids(userIds)
                .build());
        if (!response.isSuccess()) {
            return null;
        }
        return response.getData();
    }

}
