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

  // 防止重复请求用户信息的标志
  const isLoadingUserInfo = ref(false)

  // 设置用户信息（使用新的 /user/userInfo 接口）
  function setUserInfo() {
    // 如果正在加载中，直接返回，避免重复请求
    if (isLoadingUserInfo.value) {
      console.log('用户信息正在加载中，跳过重复请求')
      return
    }

    console.log('设置用户信息')
    isLoadingUserInfo.value = true
    // 调用后端获取用户信息接口，并返回 Promise 以便调用方等待
    return getUserInfoWithAuth().then(res => {
      if (res.success == true) {
        userInfo.value = res.data
      }
      return res
    }).finally(() => {
      // 请求完成后，重置加载状态
      isLoadingUserInfo.value = false
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
    // 如果正在加载中，直接返回，避免重复请求
    if (isLoadingUserInfo.value) {
      console.log('用户信息正在加载中，跳过重复请求')
      return
    }

    console.log('Admin页面获取用户头像')
    isLoadingUserInfo.value = true

    getUserInfoWithAuth().then(res => {
      if (res.success) {
        // 更新用户头像到博客设置中（用于admin页面可能需要的地方）
        blogSettings.value.avatar = res.data.avatarUrl || '/src/assets/developer.png'
      }
    }).finally(() => {
      // 请求完成后，重置加载状态
      isLoadingUserInfo.value = false
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
    isLoadingUserInfo,
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