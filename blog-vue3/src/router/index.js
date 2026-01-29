import Index from '@/pages/frontend/index.vue'
import CategoryList from '@/pages/frontend/category-list.vue'
import CategoryArticleList from '@/pages/frontend/category-article-list.vue'
import TagList from '@/pages/frontend/tag-list.vue'
import TagArticleList from '@/pages/frontend/tag-article-list.vue'
import ArticleDetail from '@/pages/frontend/article-detail.vue'
import NotFound from '@/pages/frontend/404.vue'
import Login from '@/pages/admin/login.vue'
import AdminArticleList from '@/pages/admin/article-list.vue'
import AdminCategoryList from '@/pages/admin/category-list.vue'
import AdminTagList from '@/pages/admin/tag-list.vue'
import AdminCommentList from '@/pages/admin/comment-list.vue'
import AdminAdvertisementList from '@/pages/admin/advertisement-list.vue'
import AdminUserList from '@/pages/admin/user-list.vue'
import PersonalDashboard from '@/pages/personal/dashboard.vue'
import PersonalBlogSettings from '@/pages/personal/blog-settings.vue'
import PersonalArticleList from '@/pages/personal/article-list.vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import Admin from '@/layouts/admin/admin.vue'
import Personal from '@/layouts/personal/personal.vue'

// 统一在这里声明所有路由
const routes = [
    {
        path: '/', // 路由地址，首页
        component: Index, // 对应组件
        meta: { // meta 信息
            title: 'Weblog 首页' // 页面标题
        }
    },
    {
        path: '/category/list', // 分类列表页
        component: CategoryList,
        meta: { // meta 信息
            title: 'Weblog 分类列表页'
        }
    },
    {
        path: '/category/article/list', // 分类文章页
        component: CategoryArticleList,
        meta: { // meta 信息
            title: 'Weblog 分类文章页'
        }
    },
    {
        path: '/tag/list', // 标签列表页
        component: TagList,
        meta: { // meta 信息
            title: 'Weblog 标签列表页'
        }
    },
    {
        path: '/tag/article/list', // 标签列表页
        component: TagArticleList,
        meta: { // meta 信息
            title: 'Weblog 标签文章页'
        }
    },
    {
        path: '/article/:articleId', // 文章详情页
        component: ArticleDetail,
        meta: { // meta 信息
            title: 'Weblog 详情页'
        }
    },
    {
        path: '/login', // 登录页
        component: Login,
        meta: {
            title: 'Weblog 登录页'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound,
        meta: {
            title: '404 页'
        }
    },
    {
        path: "/admin", // 后台首页
        component: Admin,
        // 使用到 admin.vue 布局的，都需要放置在其子路由下面
        children: [
            {
                path: "/admin/article/list",
                component: AdminArticleList,
                meta: {
                    title: '文章管理'
                }
            },
            {
                path: "/admin/category/list",
                component: AdminCategoryList,
                meta: {
                    title: '分类管理'
                }
            },
            {
                path: "/admin/tag/list",
                component: AdminTagList,
                meta: {
                    title: '标签管理'
                }
            },
            {
                path: "/admin/comment/list",
                component: AdminCommentList,
                meta: {
                    title: '评论管理'
                }
            },
            {
                path: "/admin/advertisement/list",
                component: AdminAdvertisementList,
                meta: {
                    title: '广告图管理'
                }
            },
            {
                path: "/admin/user/list",
                component: AdminUserList,
                meta: {
                    title: '用户管理'
                }
            },
        ]

    },
    {
        path: "/personal", // 个人主页
        component: Personal,
        // 使用到 personal.vue 布局的，都需要放置在其子路由下面
        children: [
            {
                path: "/personal/dashboard",
                component: PersonalDashboard,
                meta: {
                    title: '仪表盘'
                }
            },
            {
                path: "/personal/blog-settings",
                component: PersonalBlogSettings,
                meta: {
                    title: '博客设置'
                }
            },
            {
                path: "/personal/article/list",
                component: PersonalArticleList,
                meta: {
                    title: '文章管理'
                }
            },
        ]

    }
]

// 创建路由
const router = createRouter({
    // 指定路由的历史管理方式，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
    history: createWebHashHistory(),
    // routes: routes 的缩写
    routes, 
    // 每次切换路后，页面滚动到顶部
    scrollBehavior() {
        return { top: 0 }
    }
})

// 暴露出去
export default router

