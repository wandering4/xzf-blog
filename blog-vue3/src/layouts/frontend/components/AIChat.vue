<template>
  <!-- AI 对话按钮 -->
  <div class="fixed bottom-6 right-6 z-50">
    <button
      v-if="!isOpen"
      @click="toggleChat"
      class="flex items-center justify-center w-14 h-14 text-white bg-gradient-to-r from-blue-500 to-purple-600 rounded-full shadow-lg hover:from-blue-600 hover:to-purple-700 hover:scale-110 transition-all duration-300 focus:outline-none focus:ring-4 focus:ring-blue-300 dark:focus:ring-blue-800"
      aria-label="AI Chat"
    >
      <!-- AI SVG 图标 -->
      <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
      </svg>
    </button>

    <button
      v-else
      @click="toggleChat"
      class="flex items-center justify-center w-10 h-10 mb-2 text-white bg-gray-600 rounded-full shadow-lg hover:bg-gray-700 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-400"
      aria-label="Close Chat"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
      </svg>
    </button>
  </div>

  <!-- 弹窗区域 -->
  <Transition
    enter-active-class="transition ease-out duration-300"
    enter-from-class="transform translate-y-full opacity-0"
    enter-to-class="transform translate-y-0 opacity-100"
    leave-active-class="transition ease-in duration-200"
    leave-from-class="transform translate-y-0 opacity-100"
    leave-to-class="transform translate-y-full opacity-0"
  >
    <div
      v-if="isOpen"
      class="fixed bottom-24 right-6 w-[90vw] max-w-md h-[60vh] md:h-[70vh] bg-white dark:bg-gray-800 rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-700 flex flex-col overflow-hidden z-40"
    >
      <!-- 头部 -->
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 p-4 flex items-center justify-between text-white shrink-0">
        <div class="flex items-center space-x-2">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
          </svg>
          <h2 class="text-lg font-bold">AI 智能助手</h2>
        </div>
        <span class="text-xs bg-white/20 px-2 py-1 rounded-full">在线</span>
      </div>

      <!-- 消息内容区域 -->
      <div class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50 dark:bg-gray-900">
        <!-- 欢迎消息 -->
        <div class="flex items-start space-x-3">
          <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-xs font-bold shrink-0">
            AI
          </div>
          <div class="bg-white dark:bg-gray-700 p-3 rounded-2xl rounded-tl-none shadow-sm text-gray-800 dark:text-gray-200 max-w-[80%] text-sm">
            你好！我是 xzf-blog 的 AI 助手。有什么可以帮助你的吗？
          </div>
        </div>

        <!-- 固定消息 1 -->
        <div class="flex items-start space-x-3">
          <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white text-xs font-bold shrink-0">
            AI
          </div>
          <div class="bg-white dark:bg-gray-700 p-3 rounded-2xl rounded-tl-none shadow-sm text-gray-800 dark:text-gray-200 max-w-[80%] text-sm">
            您可以尝试询问关于博客文章、分类或者技术栈的问题。
          </div>
        </div>
      </div>

      <!-- 底部输入区域 -->
      <div class="p-4 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 shrink-0">
        <div class="relative">
          <input
            type="text"
            placeholder="输入消息..."
            class="w-full px-4 py-3 pr-12 rounded-full border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm"
          />
          <button class="absolute right-2 top-1/2 transform -translate-y-1/2 p-2 text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref } from 'vue'

const isOpen = ref(false)

const toggleChat = () => {
  isOpen.value = !isOpen.value
}
</script>

