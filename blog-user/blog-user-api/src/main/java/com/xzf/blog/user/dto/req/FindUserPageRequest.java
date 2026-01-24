package com.xzf.blog.user.dto.req;

import com.xzf.blog.framework.commons.request.BasePageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FindUserPageRequest extends BasePageQuery {
    private String name;
}
