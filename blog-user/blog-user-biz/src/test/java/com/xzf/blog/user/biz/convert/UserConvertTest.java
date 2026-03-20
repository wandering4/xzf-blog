package com.xzf.blog.user.biz.convert;

import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.dataobject.UserDO;
import com.xzf.blog.user.dto.resp.FindUserPageListRspVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserConvertTest {

    @Test
    void userDOtoPageVOShouldCopyFields() {
        LocalDateTime now = LocalDateTime.of(2026, 3, 20, 9, 30);
        UserDO user = UserDO.builder()
                .id(1L)
                .username("alice")
                .avatarUrl("avatar")
                .createTime(now)
                .build();

        FindUserPageListRspVO response = UserConvert.userDOtoPageVO(user, RoleDO.builder().roleName("admin").build());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getRole()).isEqualTo("admin");
        assertThat(response.getCreateDate()).isNotNull();
    }
}
