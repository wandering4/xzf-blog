import router from '@/router/index'
import { getToken } from '@/composables/cookie'
import { showMessage } from '@/composables/util'
import { showPageLoading, hidePageLoading } from '@/composables/util'
import { useUserStore } from '@/stores/user'

// 全局路由前置守卫
router.beforeEach((to, from, next) => {
    console.log('==> 全局路由前置守卫')

    // 展示页面加载 Loading
    showPageLoading()
    
    let token = getToken()

    if (!token && to.path.startsWith('/admin')) { 
        // 若用户想访问后台（以 /admin 为前缀的路由）
        // 未登录，则强制跳转登录页
        showMessage('请先登录', 'warning')
        next({ path: '/login' })
    } else if (token && to.path == '/login') {
        // 若用户已经登录，且重复访问登录页
        showMessage('请勿重复登录', 'warning')
        // 跳转后台首页
        next({ path: '/admin/article/list' })
    } else if (!to.path.startsWith('/admin')) {
        // 如果访问的非 /admin 前缀路由
        // 前端页面使用写死的数据，不请求接口
        let userStore = useUserStore()
        userStore.getFrontendSettings()
        next()
    } else {
        // 访问 admin 页面时，才请求接口获取最新数据
        let userStore = useUserStore()
        userStore.getAdminSettings()
        next()
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