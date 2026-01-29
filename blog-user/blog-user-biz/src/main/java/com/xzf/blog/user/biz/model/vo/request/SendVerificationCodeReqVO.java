package com.xzf.blog.user.biz.model.vo.request;


import com.xzf.blog.framework.commons.validator.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendVerificationCodeReqVO {

    @NotBlank(message = "图片id不能为空")
    private String pictureId;

    @NotBlank(message = "图片结果不能为空")
    private String pictureResult;

    @NotBlank(message = "手机号不能为空")
    @PhoneNumber
    private String phone;

}