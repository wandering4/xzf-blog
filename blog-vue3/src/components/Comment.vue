<template>
    <div class="mt-14">
        <h2 class="flex justify-center items-center mb-7 text-gray-500">全部评论<span>({{ total }})</span></h2>
        <!-- 卡片 -->
        <div :class="props.customeCss">
            <!-- 评论发布表单 -->
            <form>
                <div class="flex gap-3">
                    <!-- 头像 -->
                    <div>
                        <img v-if="userStore.userInfo.avatarUrl && userStore.userInfo.avatarUrl.length > 0"
                            :src="userStore.userInfo.avatarUrl" class="w-10 h-10 rounded-full">
                        <svg v-else class="w-10 h-10 text-gray-400 dark:text-gray-400" aria-hidden="true"
                            xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
                            <path
                                d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
                        </svg>
                    </div>
                    <!-- 评论内容 -->
                    <div class="grow">
                        <div
                            class="w-full mb-4 border border-gray-200 rounded-lg bg-gray-50 dark:bg-gray-700 dark:border-gray-600">
                            <div class="px-4 py-2 bg-white rounded-t-lg dark:bg-gray-800">
                                <label for="comment" class="sr-only">Your comment</label>
                                <textarea id="comment" rows="4" v-model="commentForm.content"
                                    class="w-full px-0 text-sm text-gray-900 bg-white border-0 dark:bg-gray-800 focus:ring-0 dark:text-white dark:placeholder-gray-400"
                                    placeholder="发表一个友善的评论吧..." required></textarea>
                            </div>
                            <div class="flex items-center justify-between px-3 py-2 border-t dark:border-gray-600">
                                <div @click="onPublishCommentClick" class="inline-flex items-center py-2.5 px-4 text-xs font-medium text-center text-white 
bg-sky-600 rounded-lg focus:ring-4 focus:ring-sky-200 dark:focus:ring-sky-900 hover:bg-sky-700">
                                    发送
                                </div>
                                <div class="flex ps-0 space-x-1 rtl:space-x-reverse sm:ps-2">
                                    <!-- Emoji -->
                                    <div data-popover-target="popover-emoji" type="button"
                                        class="inline-flex justify-center items-center p-2 text-gray-500 rounded cursor-pointer hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600">
                                        <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                                            fill="none" viewBox="0 0 24 24">
                                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                                                stroke-width="2"
                                                d="M15 9h0M9 9h0m12 3a9 9 0 1 1-18 0 9 9 0 0 1 18 0ZM7 13c0 1 .5 2.4 1.5 3.2a5.5 5.5 0 0 0 7 0c1-.8 1.5-2.2 1.5-3.2 0 0-2 1-5 1s-5-1-5-1Z" />
                                        </svg>
                                    </div>

                                    <!-- Emoji Popover -->
                                    <div data-popover id="popover-emoji" role="tooltip"
                                        class="absolute z-10 invisible inline-block w-64 text-sm text-gray-500 transition-opacity duration-300 bg-white border border-gray-200 rounded-lg shadow-sm opacity-0 dark:text-gray-400 dark:border-gray-600 dark:bg-gray-800">
                                        <div class="p-2">
                                            <div class="grid grid-cols-6 gap-2">
                                                <div v-for="(emoji, index) in emojis" :key="index"
                                                    class="text-2xl hover:cursor-pointer" @click="addEmoji(emoji)">{{
            emoji }}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <!-- 评论列表 -->
            <div v-if="comments && comments.length > 0" v-for="(comment, index) in comments" :key="index">

                <!-- 边界线 -->
                <div v-if="index > 0" class="border-t ml-12 mt-5  border-gray-100 dark:border-gray-700"></div>

                <!-- 一级评论 -->
                <div class="flex gap-3 mt-5">
                    <!-- 左边头像栏 -->
                    <div>
                        <img v-if="comment.userInfo?.avatarUrl" :src="comment.userInfo.avatarUrl"
                            class="w-10 h-10 rounded-full">
                        <svg v-else class="w-10 h-10 text-gray-400 rounded-full dark:text-gray-400"
                            aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                            viewBox="0 0 20 20">
                            <path
                                d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
                        </svg>
                    </div>
                    <!-- 右边评论信息 -->
                    <div class="flex flex-col gap-2 grow">
                        <!-- 昵称和时间、操作 -->
                        <div class="flex items-center justify-between">
                            <div class="text-xs text-[#FB7299] font-bold">{{ comment.userInfo?.name }}</div>
                            <!-- 删除按钮（本人发布的评论显示） -->
                            <div v-if="isOwnComment(comment)" class="relative">
                                <button @click="toggleCommentMenu(comment.id)"
                                    class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
                                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 20">
                                        <path
                                            d="M5 10a2 2 0 110-4 2 2 0 010 4zM12 10a2 2 0 110-4 2 2 0 010 4zM19 10a2 2 0 110-4 2 2 0 010 4z" />
                                    </svg>
                                </button>
                                <!-- 下拉菜单 -->
                                <div v-show="activeCommentMenu === comment.id"
                                    class="absolute right-0 mt-2 w-32 bg-white dark:bg-gray-700 rounded-md shadow-lg z-10 border dark:border-gray-600">
                                    <button @click="handleDeleteComment(comment.id)"
                                        class="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 dark:hover:bg-gray-600 dark:text-red-400">
                                        删除
                                    </button>
                                </div>
                            </div>
                        </div>
                        <!-- 评论内容 -->
                        <div class="text-sm dark:text-gray-400 whitespace-pre-line">{{ comment.content }}</div>
                        <!-- Meta 信息 -->
                        <div class="flex items-center text-xs text-gray-400">
                            <!-- 发布时间 -->
                            <div>{{ comment.createTime }}</div>
                        </div>
                    </div>
                </div>

            </div>
            <!-- 没有评论的提示文字 -->
            <div v-else class="flex items-center mt-10 mb-10 justify-center text-gray-400">还没有任何评论哟~</div>


        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import {
    initTooltips, initPopovers
} from 'flowbite'
import { useCommentStore } from '@/stores/comment'
import { useUserStore } from '@/stores/user'
import { publishComment, getCommentPageList, deleteComment } from '@/api/frontend/comment'
import { useRoute } from 'vue-router'
import { showMessage } from '@/composables/util'

const route = useRoute()
const commentStore = useCommentStore()
const userStore = useUserStore()

// 当前文章ID
const articleId = computed(() => route.params.articleId)

// 对外暴露属性
const props = defineProps({
    customeCss: {
        type: String,
        default: 'w-full px-5 py-10 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700'
    }
})

onMounted(() => {
    initTooltips()
    initPopovers()
})

// emojis 表情符号
const emojis = ref(['😃', '😁', '😅', '😂', '😍', '😜', '😝', '🤑', '🥵', '🥰', '😙', '😎'
    , '😵', '😭', '😱', '😖', '🥳', '👽', '🙈', '🤡', '😤', '💣', '💯', '💢', '❤️', '👍', '👏', '👋', '👌', '🤏', '🙏'])

// 评论表单
const commentForm = reactive({
    content: '',
    replyCommentId: null,
    parentCommentId: null
})

// 一级评论发布点击事件
const onPublishCommentClick = () => {
    // 校验
    if (commentForm.content.length === 0) {
        showMessage('请填写评论内容', 'warning')
        return
    }
    if (commentForm.content.length > 120) {
        showMessage('评论内容不能超过 120 个字符', 'warning')
        return
    }

    // 构建请求参数
    const requestData = {
        articleId: articleId.value,
        content: commentForm.content
    }

    publishComment(requestData).then(res => {
        if (!res.success) {
            // 获取服务端返回的错误消息
            let message = res.message
            // 提示错误消息
            showMessage(message, 'error')
            return
        }

        showMessage('评论发布成功')
        // 将表单对象中的 content 评论内容置空
        commentForm.content = ''
        // 重新渲染表单列表
        initComments()
    })
}

// 添加 Emoji 表情
const addEmoji = (emoji) => {
    commentForm.content = commentForm.content + emoji
}



// 评论数组
const comments = ref([])
// 评论总数量
const total = ref(0)

function initComments() {
    getCommentPageList({ articleId: articleId.value }).then(res => {
        if (res.success) {
            total.value = res.totalCount
            comments.value = res.data
        }
    })
}
initComments()

// 判断是否是本人发布的评论
const isOwnComment = (comment) => {
    // 比较当前登录用户的ID和评论用户的ID
    return comment.userInfo?.id === userStore.userInfo.userId
}

// 当前展开的评论菜单
const activeCommentMenu = ref(null)

// 切换评论菜单
const toggleCommentMenu = (commentId) => {
    if (activeCommentMenu.value === commentId) {
        activeCommentMenu.value = null
    } else {
        activeCommentMenu.value = commentId
    }
}

// 删除评论
const handleDeleteComment = (commentId) => {
    // 调用删除评论接口
    deleteComment(commentId).then(res => {
        if (res.success) {
            showMessage('评论删除成功')
            // 重新加载评论列表
            initComments()
        } else {
            showMessage(res.message || '删除失败', 'error')
        }
    }).catch(() => {
        showMessage('删除失败', 'error')
    }).finally(() => {
        // 关闭下拉菜单
        activeCommentMenu.value = null
    })
}

</script>
