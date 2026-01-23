<template>
    <div class="w-full py-5 px-2 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
        <div class="relative w-full h-48 overflow-hidden rounded-lg">
            <!-- 轮播图片 -->
            <div class="flex transition-transform duration-500 ease-in-out"
                 :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
                <div v-for="(image, index) in images" :key="index"
                     class="flex-shrink-0 w-full h-48 bg-cover bg-center"
                     :style="{ backgroundImage: `url(${image.url})` }">
                </div>
            </div>

            <!-- 左侧导航按钮 -->
            <button @click="prevSlide"
                    class="absolute left-2 top-1/2 transform -translate-y-1/2 bg-black/50 text-white p-2 rounded-full hover:bg-black/70 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                </svg>
            </button>

            <!-- 右侧导航按钮 -->
            <button @click="nextSlide"
                    class="absolute right-2 top-1/2 transform -translate-y-1/2 bg-black/50 text-white p-2 rounded-full hover:bg-black/70 transition-colors">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                </svg>
            </button>

            <!-- 指示器 -->
            <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-2">
                <button v-for="(image, index) in images" :key="index"
                        @click="goToSlide(index)"
                        class="w-2 h-2 rounded-full transition-colors"
                        :class="index === currentIndex ? 'bg-white' : 'bg-white/50'">
                </button>
            </div>
        </div>

        <!-- 标题 -->
        <div class="mt-4 text-center">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">精彩内容推荐</h3>
            <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">发现更多精彩内容</p>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 轮播图片数据
const images = ref([
    {
        url: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&h=300&fit=crop&crop=face',
        title: '技术分享'
    },
    {
        url: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500&h=300&fit=crop',
        title: '学习笔记'
    },
    {
        url: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500&h=300&fit=crop',
        title: '项目经验'
    },
    {
        url: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=500&h=300&fit=crop',
        title: '生活感悟'
    }
])

// 当前显示的图片索引
const currentIndex = ref(0)

// 上一张
const prevSlide = () => {
    currentIndex.value = currentIndex.value === 0 ? images.value.length - 1 : currentIndex.value - 1
}

// 下一张
const nextSlide = () => {
    currentIndex.value = currentIndex.value === images.value.length - 1 ? 0 : currentIndex.value + 1
}

// 跳转到指定图片
const goToSlide = (index) => {
    currentIndex.value = index
}

// 自动轮播
let autoplayInterval

onMounted(() => {
    autoplayInterval = setInterval(() => {
        nextSlide()
    }, 4000) // 每4秒切换一次
})

// 清理定时器
const clearAutoplay = () => {
    if (autoplayInterval) {
        clearInterval(autoplayInterval)
    }
}

// 组件卸载时清理定时器
import { onUnmounted } from 'vue'
onUnmounted(() => {
    clearAutoplay()
})
</script>