import axios from "@/axios";


// 获取手机验证码
export function send(pictureId,pictureResult,phone) {
    return axios.post("/user/auth/verification/code/send", {pictureId,pictureResult,phone})
}

// 登录接口
export function login(phone, password, type, code) {
    return axios.post("/user/auth/login", {phone, password,type,code})
}


// 获取用户信息 (新接口，需要 Authorization header)
export function getUserInfoWithAuth() {
    return axios.post("/user/userInfo", {}, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    })
}

// 获取图片验证码
export function getVerificationPicture(pictureId) {
    return axios.get("/user/auth/verification/picture/get", {
        params: { pictureId },  // 注意这里使用 params
        responseType: 'blob'
    })
}

// 发送短信验证码
export function sendVerificationCode(pictureId, pictureResult, phone) {
    return axios.post("/user/auth/verification/code/send", {pictureId, pictureResult, phone})
}

// 更新用户信息 (头像、昵称、性别、个人介绍)
export function updateBlogSettings(data) {
    return axios.post("/user/update", data, {
        headers: {
            'Content-Type': 'application/json',
        }
    })
}

// 修改用户密码
export function updateAdminPassword(data) {
    return axios.post("/user/password/update", data)
}

// 获取博客设置详情
export function getBlogSettingsDetail(userId) {
    return axios.post("/user/findById", { id: userId })
}


// 获取用户分页
export function userPage(data) {
    return axios.post("/user/list", data)
}

// 修改用户角色
export function chanRole(userId,roleId) {
    return axios.post("/user/role/change", {userId,roleId})
}


export function deleteUser(id) {
    return axios.post("/user/delete", {id})
}