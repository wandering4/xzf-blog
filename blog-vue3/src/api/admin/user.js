import axios from "@/axios";

// 登录接口
export function login(phone, password) {
    return axios.post("/user/auth/login", {phone, password,type:2})
}

// 获取用户信息 (新接口，需要 Authorization header)
export function getUserInfoWithAuth() {
    return axios.post("/user/userInfo", {}, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    })
}

// 更新用户信息 (头像、昵称、性别、个人介绍)
export function updateBlogSettings(data) {
    return axios.post("/user/update", data, {}, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
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
