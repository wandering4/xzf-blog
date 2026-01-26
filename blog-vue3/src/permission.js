import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading, hidePageLoading } from '@/composables/util'
import { useUserStore } from '@/stores/user'

// 全局路由前置守卫
router.beforeEach(async (to, from, next) => {
    console.log('==> 全局路由前置守卫')

    // 展示页面加载 Loading
    showPageLoading()
    
    let token = getToken()

    // 受保护的路由前缀：/admin 和 /personal
    const isProtected = to.path.startsWith('/admin') || to.path.startsWith('/personal')

    if (!token && isProtected) {
        // 若用户想访问受保护页面（admin 或 personal 前缀），未登录，则强制跳转登录页
        showMessage('请先登录', 'warning')
        next({ path: '/login' })
    } else if (token && to.path == '/login') {
        // 若用户已经登录，且重复访问登录页
        showMessage('请勿重复登录', 'warning')
        // 跳转首页
        next({ path: '/' })
    } else if (!isProtected) {
        // 访问的非管理/个人前缀路由（即公开前端页面）
        // 前端页面使用写死的数据，不请求接口
        let userStore = useUserStore()
        userStore.getFrontendSettings()
        next()
    } else {
        // 访问 admin 或 personal 页面时，才请求接口获取最新数据
        let userStore = useUserStore()
        // If accessing admin routes, ensure user has role 'root'
        if (to.path.startsWith('/admin')) {
            // try to ensure userInfo is loaded
            if (!userStore.userInfo || !userStore.userInfo.role) {
                try {
                    await userStore.setUserInfo()
                } catch (e) {
                    console.error('获取用户信息失败', e)
                }
            }
            if (userStore.userInfo && userStore.userInfo.role === 'root') {
                userStore.getAdminSettings()
                next()
            } else {
                showMessage('无权限访问后台', 'error')
                next({ path: '/' })
            }
        } else {
            // personal pages - any logged-in user allowed
            userStore.getAdminSettings()
            next()
        }
    }
})

// 全局路由后置守卫
router.afterEach((to, from) => {
    // 动态设置页面 Titile
    let title = (to.meta.title ? to.meta.title : '') + ' - Weblog'
    document.title = title

    // 隐藏页面加载 Loading
    hidePageLoading()
})