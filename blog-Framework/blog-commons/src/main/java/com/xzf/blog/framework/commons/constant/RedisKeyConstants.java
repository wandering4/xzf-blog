package com.xzf.blog.framework.commons.constant;

public interface RedisKeyConstants {

    /**
     * 验证图片 KEY 前缀
     */
    String VERIFICATION_PICTURE_KEY_PREFIX = "verification_picture:";

    /**
     * 验证码 KEY 前缀
     */
    String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    /**
     * 用户角色数据 KEY 前缀
     */
    String USER_ROLES_KEY_PREFIX = "user:roles:";

    /**
     * 角色对应的权限集合 KEY 前缀
     */
    String ROLE_PERMISSIONS_KEY_PREFIX = "role:permissions:";

    /**
     * Sa-Token 登录的 Token KEY 前缀
     */
    String SA_TOKEN_TOKEN_KEY_PREFIX = "Authorization:login:token:";

    /**
     * 用户信息数据 KEY 前缀
     */
    String USER_INFO_KEY_PREFIX = "user:info:";

    /**
     * 构建验证图 KEY
     *
     * @param pictureId
     * @return
     */
    public static String buildVerificationPictureKey(String pictureId) {
        return VERIFICATION_PICTURE_KEY_PREFIX + pictureId;
    }

    /**
     * 构建验证码 KEY
     *
     * @param phone
     * @return
     */
    public static String buildVerificationCodeKey(String phone) {
        return VERIFICATION_CODE_KEY_PREFIX + phone;
    }


    /**
     * 用户对应的角色集合 KEY
     *
     * @param userId
     * @return
     */
    public static String buildUserRoleKey(Long userId) {
        return USER_ROLES_KEY_PREFIX + userId;
    }


    /**
     * 构建角色对应的权限集合 KEY
     *
     * @param roleKey
     * @return
     */
    public static String buildRolePermissionsKey(String roleKey) {
        return ROLE_PERMISSIONS_KEY_PREFIX + roleKey;
    }


    /**
     * 构建角色对应的权限集合 KEY
     *
     * @param userId
     * @return
     */
    public static String buildUserInfoKey(Long userId) {
        return USER_INFO_KEY_PREFIX + userId;
    }

}
