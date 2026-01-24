<template>
    <Header></Header>

    <!-- 主内容区域 -->
    <main class="container max-w-screen-xl mx-auto px-4 md:px-6 py-4">
        <!-- grid 表格布局，分为 4 列 -->
        <div class="grid grid-cols-4 gap-7">
            <!-- 左边栏，占用 3 列 -->
            <div class="col-span-4 md:col-span-3 mb-3">
                <!-- 标签 -->
                <div v-if="tags && tags.length > 0"
                    class="w-full p-5 pb-7 mb-6 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                    <!-- 标签标题 -->
                    <h2 class="flex items-center mb-5 font-bold text-gray-900 uppercase dark:text-white">
                        <!-- 标签图标 -->
                        <svg t="1698980289658" class="icon w-4 h-4 mr-2" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="13858" width="200" height="200">
                            <path
                                d="M646.4512 627.5584m-298.1888 0a298.1888 298.1888 0 1 0 596.3776 0 298.1888 298.1888 0 1 0-596.3776 0Z"
                                fill="#C7ACEF" p-id="13859"></path>
                            <path
                                d="M467.6096 962.5088c-34.4064 0-68.7616-13.1072-94.976-39.2704l-276.48-276.48c-52.3776-52.3776-52.3776-137.5744 0-189.9008L465.4592 87.552a105.216 105.216 0 0 1 76.8512-30.6176l308.6336 8.3456c55.3472 1.4848 100.096 46.0288 101.7856 101.376l9.5744 310.1696c0.8704 28.7744-10.2912 56.9344-30.6176 77.2608l-369.2032 369.2032c-26.112 26.112-60.4672 39.2192-94.8736 39.2192z m71.8848-844.1856c-11.4176 0-22.4768 4.5568-30.5664 12.6464L139.6224 500.2752c-28.416 28.416-28.416 74.6496 0 103.0144l276.48 276.48c28.416 28.416 74.6496 28.416 103.0144 0l369.2032-369.2032a43.4176 43.4176 0 0 0 12.6464-31.8976l-9.5744-310.1696c-0.7168-22.8864-19.2-41.2672-42.0352-41.8816l-308.6336-8.3456c-0.4608 0.0512-0.8192 0.0512-1.2288 0.0512z"
                                fill="#4F4F4F" p-id="13860"></path>
                            <path
                                d="M676.4032 445.5424c-62.208 0-112.8448-50.6368-112.8448-112.8448s50.6368-112.8448 112.8448-112.8448c62.208 0 112.8448 50.6368 112.8448 112.8448s-50.6368 112.8448-112.8448 112.8448z m0-164.1984c-28.3648 0-51.4048 23.04-51.4048 51.4048s23.04 51.4048 51.4048 51.4048c28.3648 0 51.4048-23.04 51.4048-51.4048s-23.0912-51.4048-51.4048-51.4048z"
                                fill="#4F4F4F" p-id="13861"></path>
                        </svg>
                        标签
                        <span class="ml-2 text-gray-600 font-normal dark:text-gray-300">( {{ tags.length }} )</span>
                    </h2>
                    <!-- 标签列表 -->
                    <div class="flex flex-wrap gap-3">
                        <a v-for="(tag, index) in tags" :key="index" @click="goTagArticleListPage(tag.id, tag.name)" class="cursor-pointer inline-flex items-center px-3.5 py-1.5 text-xs font-medium text-center border rounded-[12px]
            hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-gray-300
            dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-gray-700 dark:focus:ring-gray-800
            dark:border-gray-700 dark:hover:text-white">
                            {{ tag.name }}
                            <span
                                class="inline-flex items-center justify-center w-4 h-4 ms-2 text-xs font-semibold text-sky-800 bg-sky-200 rounded-full">
                                {{ tag.articlesTotal }}
                            </span>
                        </a>
                    </div>
                </div>

                <!-- 文章列表，grid 表格布局，分为 2 列 -->
                <div class="grid grid-cols-2 gap-4 mb-6">
                    <div v-for="(article, index) in articles" :key="index" class="col-span-2 md:col-span-1 animate__animated animate__fadeInUp">
                        <div class=" relative bg-white hover:scale-[1.03] h-full border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
                            <!-- 文章封面 -->
                            <a @click="goArticleDetailPage(article.id)" class="cursor-pointer">
                                <img class="rounded-t-lg h-48 w-full"
                                    :src="article.cover" />
                            </a>
                            <div class="p-5 flex flex-col min-h-max">
                                <!-- 标签 -->
                                <div class="mb-3">
                                    <span v-for="(tag, tagIndex) in article.tags" :key="tagIndex" @click="goTagArticleListPage(tag.id, tag.name)"
                                        class="cursor-pointer bg-green-100 text-green-800 text-xs font-medium mr-2 px-2.5 py-0.5
                                        rounded hover:bg-green-200 hover:text-green-900 dark:bg-green-900
                                        dark:hover:bg-green-950
                                        dark:text-green-300">
                                        {{ tag.name }}
                                    </span>
                                </div>
                                <!-- 文章标题 -->
                                <a @click="goArticleDetailPage(article.id)" class="cursor-pointer">
                                    <h2 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                                        <span class="hover:border-gray-600 hover:border-b-2 dark:hover:border-gray-400">{{ article.title }}</span>
                                    </h2>
                                </a>
                                <!-- 文章摘要 -->
                                <p v-if="article.summary" class="mb-3 font-normal text-gray-500 dark:text-gray-400">{{ article.summary }}</p>
                                <!-- 文章发布时间、所属分类 -->
                                <p class="mt-auto flex items-center font-normal text-gray-400 text-sm dark:text-gray-400">
                                    <!-- 发布时间 -->
                                    <svg class="inline w-3 h-3 mr-2 text-gray-400" aria-hidden="true"
                                        xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                            stroke-width="2"
                                            d="M5 1v3m5-3v3m5-3v3M1 7h18M5 11h10M2 3h16a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1Z" />
                                    </svg>
                                    {{ article.createDate }}

                                    <!-- 所属分类 -->
                                    <svg class="inline w-3 h-3 ml-5 mr-2 text-gray-400" aria-hidden="true"
                                        xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 18 18">
                                        <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                            stroke-width="2"
                                            d="M1 5v11a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1H1Zm0 0V2a1 1 0 0 1 1-1h5.443a1 1 0 0 1 .8.4l2.7 3.6H1Z" />
                                    </svg>
                                    <a @click="goCategoryArticleListPage(article.category.id, article.category.name)" class="cursor-pointer text-gray-400 hover:underline">{{ article.category.name }}</a>
                                </p>
                            </div>

                            <!-- 是否置顶 -->
                            <div v-if="article.isTop" class="absolute inline-flex items-center justify-center w-14 h-7 text-xs font-bold text-white bg-red-500 border-2 border-white rounded-full -top-2 -end-2 dark:border-gray-900">
                                置顶
                            </div>

                        </div>
                    </div>
                </div>

                <!-- 分页 -->
                <nav aria-label="Page navigation example" class="flex justify-center">
                    <ul class="flex items-center -space-x-px h-10 text-base">
                        <!-- 上一页 -->
                        <li>
                            <a @click="getArticles(current - 1)"
                                class="flex items-center justify-center px-4 h-10 ml-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-l-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                                :class="[current > 1 ? '' : 'cursor-not-allowed']"
                                >

                                <span class="sr-only">上一页</span>
                                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                                    viewBox="0 0 6 10">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                        stroke-width="2" d="M5 1 1 5l4 4" />
                                </svg>
                            </a>
                        </li>
                        <!-- 页码 -->
                        <li v-for="pageNo in pageNumbers" :key="pageNo">
                            <a @click="getArticles(pageNo)"
                                class="flex items-center justify-center px-4 h-10 leading-tight border  dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                                :class="[pageNo == current ? 'text-sky-600  bg-sky-50 border-sky-500 hover:bg-sky-100 hover:text-sky-700' : 'text-gray-500 border-gray-300 bg-white hover:bg-gray-100 hover:text-gray-700']"
                                >
                                {{ pageNo }}
                            </a>
                        </li>
                        <!-- 下一页 -->
                        <li>
                            <a @click="getArticles(current + 1)"
                                class="flex items-center justify-center px-4 h-10 leading-tight text-gray-500 bg-white border border-gray-300 rounded-r-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
                                :class="[current < pages ? '' : 'cursor-not-allowed']"
                                >
                                <span class="sr-only">下一页</span>
                                <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                                    viewBox="0 0 6 10">
                                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                        stroke-width="2" d="m1 9 4-4-4-4" />
                                </svg>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>

            <!-- 右边侧边栏，占用一列 -->
            <aside class="col-span-4 md:col-span-1">
                <div class="sticky top-[5.5rem]">
                    <!-- 博主信息 -->
                    <Carousel></Carousel>

                    <!-- 分类 -->
                    <CategoryListCard></CategoryListCard>
                </div>
            </aside>
        </div>

    </main>

    <!-- 返回顶部 -->
    <ScrollToTopButton></ScrollToTopButton>

    <Footer></Footer>
</template>

<script setup>
import Header from '@/layouts/frontend/components/Header.vue'
import Footer from '@/layouts/frontend/components/Footer.vue'
import Carousel from '@/layouts/frontend/components/Carousel.vue'
import CategoryListCard from '@/layouts/frontend/components/CategoryListCard.vue'
import ScrollToTopButton from '@/layouts/frontend/components/ScrollToTopButton.vue'
import { getTagList } from '@/api/frontend/tag'
import { getArticlePageList } from '@/api/frontend/article'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 文章集合
const articles = ref([])
// 当前页码
const current = ref(1)
// 每页显示的文章数
const size = ref(10)
// 总文章数
const total = ref(0)
// 总共多少页
const pages = ref(0)

// 生成页码数组
const pageNumbers = computed(() => {
    const numbers = []
    for (let i = 1; i <= pages.value; i++) {
        numbers.push(i)
    }
    return numbers
})

function getArticles(currentNo) {
    // 上下页是否能点击判断，当要跳转上一页且页码小于 1 时，则不允许跳转；当要跳转下一页且页码大于总页数时，则不允许跳转
    if (currentNo < 1 || (pages.value > 0 && currentNo > pages.value)) return
    // 调用分页接口渲染数据
    getArticlePageList({current: currentNo, size: size.value}).then((res) => {
        if (res.success) {
            articles.value = res.data  // 直接使用data数组
            current.value = res.pageNo
            size.value = res.pageSize
            total.value = res.totalCount
            pages.value = res.totalPage
        }
    })
}
// 页面初始化时获取文章数据
getArticles(current.value)

// 跳转文章详情页
const goArticleDetailPage = (articleId) => {
    router.push('/article/' + articleId)
}

// 跳转分类文章列表页
const goCategoryArticleListPage = (id, name) => {
    // 跳转时通过 query 携带参数（分类 ID、分类名称）
    router.push({ path: '/category/article/list', query: { id, name } })
}

// 跳转标签文章列表页
const goTagArticleListPage = (id, name) => {
    // 跳转时通过 query 携带参数（标签 ID、标签名称）
    router.push({ path: '/tag/article/list', query: { id, name } })
}

// 所有标签
const tags = ref([])
getTagList({}).then((res) => {
    if (res.success) {
        tags.value = res.data
    }
})
</script>
