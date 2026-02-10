<template>
    <div>
        <!-- 卡片组件， shadow="never" 指定 card 卡片组件没有阴影 -->
        <el-card shadow="never">
            <el-form ref="formRef" :model="form" label-width="160px" :rules="rules">
                <el-form-item>
                    <h2 class="font-bold text-base mb-1">基础设置</h2>
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" clearable />
                </el-form-item>
                <el-form-item label="头像" prop="avatar">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleAvatarChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.avatar && typeof form.avatar === 'string'" :src="form.avatar" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="个人介绍" prop="introduction">
                    <el-input v-model="form.introduction" type="textarea" />
                </el-form-item>


                <el-form-item>
                    <el-button type="primary" :loading="btnLoading" @click="onSubmit">保存</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref, nextTick } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'
import { getBlogSettingsDetail, updateBlogSettings } from '@/api/admin/user'
import { uploadFile } from '@/api/admin/file'
import { showMessage } from '@/composables/util'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 是否显示保存按钮的 loading 状态，默认为 false
const btnLoading = ref(false)

// 表单引用
const formRef = ref(null)
// 表单对象
const form = reactive({
    nickname: '', // 昵称
    avatar: '', // 头像URL，用于显示
    avatarUrl: '', // 头像URL，用于提交
    introduction: '' // 个人介绍
})


// 规则校验
const rules = {
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    avatar: [{ required: true, message: '请上传头像', trigger: 'blur' }],
    introduction: [{ required: true, message: '请输入个人介绍', trigger: 'blur' }],
}


// 初始化博客设置数据，并渲染到页面上
function initBlogSettings() {
    getBlogSettingsDetail(userStore.userInfo.userId).then((e) => {
        if (e.success) {
            // 设置表单数据
            form.nickname = e.data.userName || ''
            form.avatar = e.data.avatarUrl || '' // 头像URL，用于显示
            form.avatarUrl = e.data.avatarUrl || '' // 头像URL，用于提交
            form.introduction = e.data.introduction || ''
        }
    }).catch((error) => {
        // 网络错误或其他异常
        console.error('初始化博客设置出错:', error)
    })
}
initBlogSettings()


// 上传头像
const handleAvatarChange = (file) => {
    // 表单对象
    let formData = new FormData()
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // 同时设置显示用的URL和提交用的URL
        form.avatar = e.data
        form.avatarUrl = e.data
        showMessage('上传成功')
    })
}

// 保存当前博客设置
const onSubmit = () => {
    // 验证表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return
        }

        // 显示保存按钮 loading
        btnLoading.value = true

        // 创建 JSON 对象来发送数据
        const submitData = {
            nickname: form.nickname,
            introduction: form.introduction,
        }

        // 如果有头像URL，则添加
        if (form.avatarUrl) {
            submitData.avatarUrl = form.avatarUrl
        }

        updateBlogSettings(submitData).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            // 重新渲染页面中的信息
            initBlogSettings()
            // 刷新 store 中的用户信息
            userStore.setUserInfo()
            showMessage('保存成功')
        }).finally(() => btnLoading.value = false) // 隐藏保存按钮 loading
    })
}

</script>

<style scoped>
.avatar-uploader .avatar {
    width: 100px;
    height: 100px;
    display: block;
}

</style>

<style>
/* 解决 textarea :focus 状态下，边框消失的问题 */
.el-textarea__inner:focus {
    outline: 0 !important;
    box-shadow: 0 0 0 1px var(--el-input-focus-border-color) inset !important;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    text-align: center;
}
</style>