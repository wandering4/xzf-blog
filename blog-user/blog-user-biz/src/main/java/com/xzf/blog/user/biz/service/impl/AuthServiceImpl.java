package com.xzf.blog.user.biz.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.wf.captcha.SpecCaptcha;
import com.xzf.blog.framework.commons.constant.RedisKeyConstants;
import com.xzf.blog.framework.commons.exception.BizException;
import com.xzf.blog.framework.commons.response.Response;
import com.xzf.blog.user.biz.domain.dataobject.RoleDO;
import com.xzf.blog.user.biz.domain.manager.RoleManager;
import com.xzf.blog.user.biz.exception.BizResponseCodeEnum;
import com.xzf.blog.user.biz.enums.LoginTypeEnum;
import com.xzf.blog.user.biz.model.vo.request.SendVerificationCodeReqVO;
import com.xzf.blog.user.biz.model.vo.request.UserLoginRequest;
import com.xzf.blog.user.biz.service.AuthService;
import com.xzf.blog.user.biz.service.UserService;
import com.xzf.blog.user.biz.sms.AliyunSmsHelper;
import com.xzf.blog.user.dto.req.FindUserByPhoneRequest;
import com.xzf.blog.user.dto.req.RegisterUserRequest;
import com.xzf.blog.user.dto.resp.FindUserByPhoneRspDTO;
import com.xzf.framework.biz.context.holder.LoginUserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 暂时写死
    private static final Long loginExpireHours = 72L;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private AliyunSmsHelper aliyunSmsHelper;
    @Autowired
    private RoleManager roleManager;

    /**
     * 登录与注册
     *
     * @param userLoginRequest
     * @return
     */
    @Override
    public Response<String> loginAndRegister(UserLoginRequest userLoginRequest) {
        String phone = userLoginRequest.getPhone();
        Integer type = userLoginRequest.getType();

        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);

        // 登录类型错误
        if (Objects.isNull(loginTypeEnum)) {
            throw new BizException(BizResponseCodeEnum.LOGIN_TYPE_ERROR);
        }

        Long userId = null;

        //判断登录类型
        switch (loginTypeEnum) {
            case VERIFICATION_CODE:
                //验证码登录
                String verificationCode = userLoginRequest.getCode();

                Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode), "验证码不能为空");

                // 构建验证码 Redis Key
                String key = RedisKeyConstants.buildVerificationCodeKey(phone);
                String code = (String) redisTemplate.opsForValue().get(key);

                if (!StringUtils.equals(verificationCode, code)) {
                    throw new BizException(BizResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }

                // 注册用户
                Long userIdTmp = userService.register(RegisterUserRequest.builder().phone(phone).build()).getData();

                // 若调用用户服务，返回的用户 ID 为空，则提示登录失败
                if (Objects.isNull(userIdTmp)) {
                    throw new BizException(BizResponseCodeEnum.LOGIN_FAIL);
                }

                userId = userIdTmp;

                break;

            case PASSWORD:
                String password = userLoginRequest.getPassword();
                if (ObjectUtils.isEmpty(password)) {
                    throw new BizException(BizResponseCodeEnum.LOGIN_FAIL);
                }

                FindUserByPhoneRspDTO findUserByPhoneRspDTO = userService.findByPhone(FindUserByPhoneRequest.builder().phone(phone).build());

                //是否注册
                if (Objects.isNull(findUserByPhoneRspDTO)) {
                    throw new BizException(BizResponseCodeEnum.USER_NOT_FOUND);
                }

                String encodedPassword = findUserByPhoneRspDTO.getPassword();

                boolean matches = passwordEncoder.matches(password, encodedPassword);

                //如果不匹配，则抛出业务异常
                if (!matches) {
                    throw new BizException(BizResponseCodeEnum.PASSWORD_ERROR);
                }
                userId = findUserByPhoneRspDTO.getId();
                break;
            default:
                break;
        }
        if (userId == null || userId.toString().isEmpty()) {
            return Response.fail(BizResponseCodeEnum.LOGIN_FAILURE);
        }


        // SaToken 登录用户，并返回 token 令牌
        // SaToken 登录用户, 入参为用户 ID
        StpUtil.login(userId);

        // 根据用户 ID ，从 Redis 中获取该用户的角色集合
        RoleDO roleDO = roleManager.selectByUserId(userId);
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
        redisTemplate.opsForValue().set(userRolesKey, roleDO.getRoleKey(), loginExpireHours, TimeUnit.HOURS);


        // 获取 Token 令牌
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 返回 Token 令牌
        return Response.success(tokenInfo.tokenValue);
    }


    @Override
    public Response<?> logout() {

        Long userId = LoginUserContextHolder.getUserId();

        log.info("==> 用户退出登录, userId: {}", userId);

        if (ObjectUtils.isEmpty(userId)) {
            return Response.fail(BizResponseCodeEnum.USER_NOT_FOUND);
        }

        // 退出登录 (指定用户 ID)
        StpUtil.logout(userId);
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
        redisTemplate.delete(userRolesKey);

        return Response.success();
    }

    @Override
    public void getVerificationPicture(String pictureId, HttpServletResponse response) {
        String redisKey=RedisKeyConstants.buildVerificationPictureKey(pictureId);

        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        String result = captcha.text();
        redisTemplate.opsForValue().set(redisKey, result, 5, TimeUnit.MINUTES);

        // 输出图片
        try {
            response.setContentType("image/png");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            throw new BizException(BizResponseCodeEnum.VERIFICATION_PICTURE_GENERATE_FAIL);
        }
    }

    @Override
    public Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        String phone = sendVerificationCodeReqVO.getPhone();
        String pictureId= sendVerificationCodeReqVO.getPictureId();
        String userPicRes = sendVerificationCodeReqVO.getPictureResult();

        // 校验图片验证码
        String pictureKey = RedisKeyConstants.buildVerificationPictureKey(pictureId);
        String pictureRes = (String) redisTemplate.opsForValue().get(pictureKey);
        if (StringUtils.isEmpty(pictureRes)) {
            throw new BizException(BizResponseCodeEnum.VERIFICATION_PICTURE_NOT_EXIST);
        }
        if (!pictureRes.equalsIgnoreCase(userPicRes)) {
            throw new BizException(BizResponseCodeEnum.VERIFICATION_PICTURE_ERROR);
        }

        // 校验之前的邮箱验证码
        String codeKey = RedisKeyConstants.buildVerificationCodeKey(phone);
        boolean exist = redisTemplate.hasKey(codeKey);
        if (exist) {
            //若之前的验证码未过期，则提示发送频繁
            throw new BizException(BizResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }


        //生成6位随机验证码
        String verificationCode = RandomUtil.randomNumbers(6);

        log.info("-->手机号：{},已生成验证码：[{}]", phone, verificationCode);

        //调用第三方阿里云邮件发送服务
        threadPoolTaskExecutor.submit(() -> {
            String signName = "速通互联验证码";
            String templateCode = "100001";
            String templateParam = String.format("{\"code\":\"%s\",\"min\":\"%d\"}", verificationCode, 5);
            aliyunSmsHelper.sendMessage(signName, templateCode, phone, templateParam);
        });


        log.info("--> 手机号：{},已发送验证码：[{}]", phone, verificationCode);


        //验证码存储到redis,方便后面校验
        redisTemplate.opsForValue().set(codeKey, verificationCode, 5, TimeUnit.MINUTES);
        redisTemplate.delete(pictureKey);

        return Response.success();
    }
}
