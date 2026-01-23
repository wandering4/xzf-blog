import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfoWithAuth } from '@/api/admin/user'
import { removeToken } from '@/composables/cookie'

export const useUserStore = defineStore('user', () => {
  // 用户信息 - 新的数据结构
  const userInfo = ref({
    userId: null,
    role: null,
    userName: null,
    avatarUrl: null
  })

  // 博客设置信息 - 主要用于前端页面显示写死的logo
  const blogSettings = ref({
    // 前端页面只使用logo（写死）
    logo: '/src/assets/weblog-logo.png'
  })

  // 设置用户信息（使用新的 /user/userInfo 接口）
  function setUserInfo() {
    // 调用后端获取用户信息接口
    getUserInfoWithAuth().then(res => {
      if (res.success == true) {
        userInfo.value = res.data
      }
    })
  }

  // 设置用户信息（直接传入数据）
  function setUserInfoDirect(userData) {
    userInfo.value = userData
  }

  // 前端页面不需要额外设置，直接使用写死数据
  function getFrontendSettings() {
    console.log('前端页面使用写死logo')
    // logo已经在初始化时设置好了
  }

  // Admin页面获取用户头像用于博客设置
  function getAdminSettings() {
    console.log('Admin页面获取用户头像')
    getUserInfoWithAuth().then(res => {
      if (res.success) {
        // 更新用户头像到博客设置中（用于admin页面可能需要的地方）
        blogSettings.value.avatar = res.data.avatarUrl || '/src/assets/developer.png'
      }
    })
  }

  // 退出登录
  function logout() {
    // 删除 cookie 中的 token 令牌
    removeToken()
    // 删除登录用户信息
    userInfo.value = {
      userId: null,
      role: null,
      userName: null,
      avatarUrl: null
    }
  }

  return {
    userInfo,
    blogSettings,
    setUserInfo,
    setUserInfoDirect,
    getFrontendSettings,
    getAdminSettings,
    logout
  }
},
{
  // 开启持久化
  persist: true,
}
)