<template>
  <!-- AI 对话按钮 -->
  <div class="fixed bottom-6 right-6 z-50">
    <button
      v-if="!isOpen"
      @click="toggleChat"
      class="flex items-center justify-center w-14 h-14 text-white bg-transparent rounded-full shadow-lg hover:scale-110 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-blue-300 dark:focus:ring-blue-800 overflow-hidden p-0 border-0"
      aria-label="AI Chat"
    >
      <!-- AI 图标 -->
      <img :src="robotImage" alt="AI 助手" class="w-10 h-10 rounded-full object-cover" />
    </button>

    <button
      v-else
      @click="toggleChat"
      class="flex items-center justify-center w-10 h-10 mb-2 text-white bg-gray-600 rounded-full shadow-lg hover:bg-gray-700 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-400 shrink-0"
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
          <img :src="robotImage" alt="AI" class="w-8 h-8 rounded-full object-cover border-2 border-white" />
          <h2 class="text-lg font-bold">AI 智能助手</h2>
        </div>
        <span class="text-xs bg-white/20 px-2 py-1 rounded-full">在线</span>
      </div>

      <!-- 消息内容区域 -->
      <div class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50 dark:bg-gray-900" ref="chatContainer">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="flex items-start space-x-3">
          <img :src="robotImage" class="w-8 h-8 rounded-full object-cover shrink-0" alt="AI" />
          <div class="bg-white dark:bg-gray-700 p-3 rounded-2xl rounded-tl-none shadow-sm text-gray-800 dark:text-gray-200 max-w-[80%] text-sm">
            你好！我是 xzf-blog 的 AI 助手。有什么可以帮助你的吗？
          </div>
        </div>

        <!-- 消息列表 -->
        <template v-for="(msg, index) in messages" :key="index">
           <!-- 用户消息 -->
           <div v-if="msg.type === 'USER'" class="flex flex-row-reverse items-start space-x-3 space-x-reverse">
              <div class="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center text-gray-600 text-xs font-bold shrink-0 overflow-hidden">
                  <img v-if="userAvatar" :src="userAvatar" class="w-full h-full object-cover" />
                  <span v-else>我</span>
              </div>
              <div class="bg-blue-500 text-white p-3 rounded-2xl rounded-tr-none shadow-sm max-w-[80%] text-sm">
                  {{ msg.content }}
              </div>
           </div>

           <!-- AI消息 -->
           <div v-else class="flex items-start space-x-3">
              <img :src="robotImage" class="w-8 h-8 rounded-full object-cover shrink-0" alt="AI" />
              <div class="bg-white dark:bg-gray-700 p-3 rounded-2xl rounded-tl-none shadow-sm text-gray-800 dark:text-gray-200 max-w-[80%] text-sm whitespace-pre-wrap">
                  {{ msg.content }}
                  <span v-if="msg.loading" class="inline-block w-2 h-4 ml-1 bg-gray-400 animate-pulse"></span>
              </div>
           </div>
        </template>
      </div>

      <!-- 底部输入区域 -->
      <div class="p-4 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 shrink-0">
        <div class="relative">
          <input
            v-model="userInput"
            @keyup.enter="sendMessage"
            type="text"
            placeholder="输入消息..."
            class="w-full px-4 py-3 pr-12 rounded-full border border-gray-300 dark:border-gray-600 bg-gray-50 dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm"
          />
          <button @click="sendMessage" :disabled="isAILoading || !userInput.trim()" class="absolute right-2 top-1/2 transform -translate-y-1/2 p-2 text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300 disabled:opacity-50 disabled:cursor-not-allowed">
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
import { ref, onMounted, nextTick, defineProps } from 'vue'
import robotImage from '@/assets/robot.jpg'
import { getArticlePageList, getPersonalArticlePageList } from '@/api/frontend/ai'

const props = defineProps({
  articleId: {
    type: [String, Number],
    default: null
  }
})

const isOpen = ref(false)
const userInput = ref('')
const messages = ref([])
const chatContainer = ref(null)
const isAILoading = ref(false)

// 模拟用户头像，实际项目中可能从用户信息中获取
const userAvatar = ref(null)

// 获取或生成 conversationKey
function getConversationKey() {
  let key = localStorage.getItem('ai_conversation_key')
  if (!key) {
    // 使用浏览器原生 API 生成 UUID
    if (typeof crypto !== 'undefined' && crypto.randomUUID) {
        key = crypto.randomUUID()
    } else {
        // 降级方案：简单生成
        key = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
    localStorage.setItem('ai_conversation_key', key)
  }
  return key
}

const conversationKey = getConversationKey()

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value && messages.value.length === 0) {
    getHistory()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const getHistory = () => {
  getArticlePageList(1, 20, conversationKey).then(res => {
    if (res.success && res.data) {
      // 转换历史数据格式
      messages.value = res.data.map(item => {
         if (item.type === 'USER_MESSAGE') {
             try {
                 const contentObj = JSON.parse(item.content)
                 return {
                     type: 'USER',
                     content: contentObj.message
                 }
             } catch (e) {
                 return { type: 'USER', content: item.content }
             }
         } else {
             return { type: 'AI', content: item.content }
         }
      })
      scrollToBottom()
    }
  })
}
const sendMessage = async () => {
  if (!userInput.value.trim() || isAILoading.value) return

  const content = userInput.value.trim()
  userInput.value = ''

  // 添加用户消息
  messages.value.push({
    type: 'USER',
    content: content
  })
  scrollToBottom()

  // 准备AI占位消息
  const aiMessageIndex = messages.value.push({
    type: 'AI',
    content: '',
    loading: true
  }) - 1

  isAILoading.value = true

  try {
    // 构造 payload
    const payload = {
      articleId: props.articleId || null,
      conversationKey: conversationKey,
      message: content
    }

    const token = localStorage.getItem('token')

    const fetchRes = await fetch(`/api/ai/chat/stream/chat`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': token } : {})
        },
        body: JSON.stringify(payload)
    })

    if (!fetchRes.ok) {
        throw new Error('Network response was not ok')
    }

    const reader = fetchRes.body.getReader()
    const decoder = new TextDecoder()
    let done = false
    let aiContent = ""

    // 移除 loading 状态
    messages.value[aiMessageIndex].loading = false

    // 修复点1: 添加缓冲区处理不完整的chunk
    let buffer = ''
    
    while (!done) {
        const { value, done: doneReading } = await reader.read()
        done = doneReading
        
        if (value) {
            // 解码当前chunk并添加到缓冲区
            const chunk = decoder.decode(value, { stream: true })
            buffer += chunk
            
            // 修复点2: 按行分割处理SSE格式
            const lines = buffer.split('\n')
            
            // 保留最后一行（可能不完整）在缓冲区
            buffer = lines.pop() || ''
            
            for (const line of lines) {
                if (line.trim() === '' || line.startsWith(':')) {
                    // 跳过空行和注释行
                    continue
                }
                
                // 修复点3: 修复"data:"前缀去除问题
                // 原来只匹配"data: "，现在匹配"data:"开头，无论后面是否有空格
                if (line.startsWith('data:')) {
                    // 提取data:后面的内容
                    // 注意：SSE规范中"data:"后面可以有一个可选空格
                    let data = line.substring(5) // 去掉"data:"这5个字符
                    
                    // 如果第一个字符是空格，去掉它
                    if (data.startsWith(' ')) {
                        data = data.substring(1)
                    }
                    
                    // 修复点4: 正确处理空数据块
                    if (data.trim() === '') {
                        // 如果是空数据块(data:或data: )，添加一个换行符
                        aiContent += '\n'
                    } else {
                        // 如果是真实内容，直接追加
                        aiContent += data
                    }
                    
                    messages.value[aiMessageIndex].content = aiContent
                    scrollToBottom()
                } else if (line.startsWith('event:')) {
                    // 处理事件行（如果需要）
                    console.log('Event:', line.substring(6).trim())
                } else if (line.startsWith('id:')) {
                    // 处理ID行（如果需要）
                    console.log('ID:', line.substring(3).trim())
                } else if (line.startsWith('retry:')) {
                    // 处理重试时间行（如果需要）
                    console.log('Retry:', line.substring(6).trim())
                } else {
                    // 其他格式的数据，暂时按原样处理
                    console.warn('Unexpected SSE line format:', line)
                }
            }
        }
    }
    
    // 修复点5: 处理缓冲区中剩余的数据
    if (buffer.trim() !== '') {
        if (buffer.startsWith('data:')) {
            let data = buffer.substring(5)
            if (data.startsWith(' ')) {
                data = data.substring(1)
            }
            if (data.trim() === '') {
                aiContent += '\n'
            } else {
                aiContent += data
            }
            messages.value[aiMessageIndex].content = aiContent
        } else {
            // 如果不是data:开头，暂时忽略或按原样处理
            console.warn('Unexpected buffer content:', buffer)
        }
    }

    // 修复点6: 清理多余的空行和换行
    // 将连续的两个以上换行符替换为两个换行符（标准的段落分隔）
    messages.value[aiMessageIndex].content = aiContent.replace(/\n{3,}/g, '\n\n')

  } catch (error) {
    console.error("Error sending message:", error)
    messages.value[aiMessageIndex].content = "抱歉，发生了一些错误，请稍后再试。"
    messages.value[aiMessageIndex].loading = false
  } finally {
    isAILoading.value = false
  }
}


</script>
