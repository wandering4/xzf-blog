package com.xzf.blog.comment.biz.rpc;

import com.google.common.collect.Lists;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.api.UserFeignApi;
import com.xzf.blog.user.dto.req.FindUsersByIdsReqDTO;
import com.xzf.blog.user.dto.resp.FindUserByIdResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class UserRpcService {

    @Resource
    private UserFeignApi userFeignApi;

    public List<FindUserByIdResponse> getByIds(List<Long> userIds) {
        Response<List<FindUserByIdResponse>> userResponse = userFeignApi.findByIds(FindUsersByIdsReqDTO.builder()
                .ids(userIds)
                .build());
        if (!userResponse.isSuccess()) {
            return Lists.newArrayList();
        }
        return userResponse.getData();
    }

}
