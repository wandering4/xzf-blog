package com.xzf.blog.user.dto.req;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUsersByIdsReqDTO {

    @NotNull(message = "用户 ID 集合不能为空")
    @Size(min = 1, max = 20, message = "用户 ID 集合大小必须大于等于 1, 小于等于 20")
    private List<Long> ids;

}