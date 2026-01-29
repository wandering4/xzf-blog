<template>
    <!-- 使用 grid 网格布局，并指定列数为 2，高度占满全屏 -->
    <div class="grid grid-cols-2 h-screen">
        <!-- 默认占两列，order 用于指定排列顺序，md 用于适配非移动端（PC 端） -->
        <div class="col-span-2 order-2 p-10 md:col-span-1 md:order-1 bg-slate-900 relative">
            <!-- 返回首页按钮（左半边左上） -->
            <el-button class="absolute top-4 left-4 text-white" type="link" @click="goHome">返回首页</el-button>
            <!-- 指定为 flex 布局，并设置为屏幕垂直水平居中，高度为 100% -->
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInLeft animate__fast">
                <h2 class="font-bold text-4xl mb-7 text-white">Weblog 博客登录</h2>
                <!-- 指定图片宽度为父级元素的 1/2 -->
                <img src="@/assets/developer.png" class="w-1/2">
            </div>
        </div>
        <div class="flex flex-col col-span-2 order-1 md:col-span-1 md:order-2 bg-white dark:bg-gray-800">

            <!-- 顶部：黑夜白天开关 -->
            <div class="flex justify-end items-center">
                <label class="switch ml-auto mt-4 mr-4">
                    <input type="checkbox" v-model="isLight" @click="toggleDark()">
                    <span class="slider"></span>
                </label>
            </div>

            <!-- flex-col 用于指定子元素垂直排列 -->
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__bounceInRight animate__fast">
                <!-- 大标题，设置字体粗细、大小、下边距 -->
                <h1 class="font-bold text-4xl mb-5 dark:text-white">欢迎回来</h1>
                <!-- 设置 flex 布局，内容垂直水平居中，文字颜色，以及子内容水平方向 x 轴间距 -->
                <div class="flex items-center justify-center mb-7 text-gray-400 space-x-2 dark:text-gray-500">
                    <!-- 左边横线，高度为 1px, 宽度为 16，背景色设置 -->
                    <span class="h-[1px] w-16 bg-gray-200 dark:bg-gray-700"></span>
                    <span>{{ loginType === 'password' ? '账号密码登录' : '手机验证码登录' }}</span>
                    <!-- 右边横线 -->
                    <span class="h-[1px] w-16 bg-gray-200 dark:bg-gray-700"></span>
                </div>

                <!-- 切换登录方式按钮 -->
                <div class="mb-5">
                    <el-button type="primary" link @click="switchLoginType">
                        {{ loginType === 'password' ? '使用手机验证码登录' : '使用账号密码登录' }}
                    </el-button>
                </div>

                <!-- 引入 Element Plus 表单组件，移动端设置宽度为 5/6，PC 端设置为 2/5 -->
                <el-form v-if="loginType === 'password'" class="w-5/6 md:w-2/5" ref="formRef" :rules="rules" :model="form">
                    <el-form-item prop="phone">
                        <!-- 输入框组件 -->
                        <el-input size="large" v-model="form.phone" placeholder="请输入手机号" :prefix-icon="User" clearable />
                    </el-form-item>
                    <el-form-item prop="password">
                        <!-- 密码框组件 -->
                        <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item>
                        <!-- 登录按钮，宽度设置为 100% -->
                        <el-button class="w-full mt-2" size="large" :loading="loading" type="primary" @click="onSubmit">登录</el-button>
                    </el-form-item>
                </el-form>

                <!-- 手机验证码登录表单 -->
                <el-form v-else class="w-5/6 md:w-2/5" ref="phoneFormRef" :rules="phoneRules" :model="phoneForm">
                    <el-form-item prop="phone">
                        <el-input size="large" v-model="phoneForm.phone" placeholder="请输入手机号" :prefix-icon="User" clearable />
                    </el-form-item>
                    <el-form-item prop="pictureResult">
                        <div class="flex w-full gap-2">
                            <el-input size="large" v-model="phoneForm.pictureResult" placeholder="请输入图片验证码" />
                            <img v-if="pictureUrl" :src="pictureUrl" @click="refreshPicture" class="h-12 cursor-pointer rounded border border-gray-300" alt="验证码" />
                        </div>
                    </el-form-item>
                    <el-form-item prop="code">
                        <div class="flex w-full gap-2">
                            <el-input size="large" v-model="phoneForm.code" placeholder="请输入短信验证码" />
                            <el-button size="large" type="primary" :disabled="codeBtnDisabled || !phoneForm.pictureResult" @click="sendCode">
                                {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '获取验证码' }}
                            </el-button>
                        </div>
                    </el-form-item>
                    <el-form-item>
                        <el-button class="w-full mt-2" size="large" :loading="loading" type="primary" @click="onPhoneSubmit">登录</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
// 引入 Element Plus 中的用户、锁图标
import { User, Lock } from '@element-plus/icons-vue'
import { login, getUserInfoWithAuth, getVerificationPicture, sendVerificationCode } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { showMessage} from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'
import { useDark, useToggle } from '@vueuse/core'

const userStore = useUserStore()

// 登录方式：'password' 账号密码登录，'phone' 手机验证码登录
const loginType = ref('password')

// 定义响应式的表单对象
const form = reactive({
    phone: '17891997260',
    password: 'bb43181782'
})

// 手机验证码登录表单
const phoneForm = reactive({
    phone: '',
    pictureResult: '',
    code: ''
})

// 图片验证码相关
const pictureId = ref('')
const pictureUrl = ref('')
const pictureLoading = ref(false)

// 发送验证码按钮倒计时
const codeCountdown = ref(0)
const codeBtnDisabled = ref(false)

const router = useRouter()
// 登录按钮加载
const loading = ref(false)

// 返回首页
const goHome = () => {
    router.push('/')
}

// 表单引用
const formRef = ref(null)
const phoneFormRef = ref(null)
// 表单验证规则
const rules = {
    phone: [
        {
            required: true,
            message: '手机号不能为空',
            trigger: 'blur'
        }
    ],
    password: [
        {
            required: true,
            message: '密码不能为空',
            trigger: 'blur',
        },
    ]
}

// 手机验证码登录表单验证规则
const phoneRules = {
    phone: [
        {
            required: true,
            message: '手机号不能为空',
            trigger: 'blur'
        }
    ],
    pictureResult: [
        {
            required: true,
            message: '图片验证码不能为空',
            trigger: 'blur'
        }
    ],
    code: [
        {
            required: true,
            message: '短信验证码不能为空',
            trigger: 'blur'
        }
    ]
}

// 切换登录方式
const switchLoginType = () => {
    if (loginType.value === 'password') {
        loginType.value = 'phone'
        refreshPicture()
        startPictureTimer()
    } else {
        loginType.value = 'password'
        stopPictureTimer()
    }
}
// 图片验证码自动刷新定时器
let pictureTimer = null

// 刷新图片验证码
const refreshPicture = () => {
    // 生成uuid
    pictureId.value = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0
        const v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
    })
    pictureLoading.value = true
    getVerificationPicture(pictureId.value).then(res => {
        // 将blob转换为base64
        const reader = new FileReader()
        reader.onload = (e) => {
            pictureUrl.value = e.target.result
        }
        reader.readAsDataURL(res)
    }).catch(() => {
        showMessage('获取验证码失败', 'error')
    }).finally(() => {
        pictureLoading.value = false
    })
}

// 启动图片验证码自动刷新（55秒）
const startPictureTimer = () => {
    if (pictureTimer) {
        clearInterval(pictureTimer)
    }
    pictureTimer = setInterval(() => {
        if (loginType.value === 'phone') {
            refreshPicture()
        }
    }, 55000)
}

// 停止图片验证码自动刷新
const stopPictureTimer = () => {
    if (pictureTimer) {
        clearInterval(pictureTimer)
        pictureTimer = null
    }
}

// 发送短信验证码
const sendCode = () => {
    if (!phoneForm.phone) {
        showMessage('请输入手机号', 'warning')
        return
    }
    if (!phoneForm.pictureResult) {
        showMessage('请输入图片验证码', 'warning')
        return
    }

    sendVerificationCode(pictureId.value, phoneForm.pictureResult, phoneForm.phone).then(res => {
        if (res.success === true) {
            showMessage('验证码发送成功')
            // 开始5分钟倒计时
            codeCountdown.value = 300
            codeBtnDisabled.value = true
            const timer = setInterval(() => {
                codeCountdown.value--
                if (codeCountdown.value <= 0) {
                    clearInterval(timer)
                    codeBtnDisabled.value = false
                }
            }, 1000)
        } else {
            showMessage(res.message || '验证码发送失败', 'error')
            refreshPicture()
            phoneForm.pictureResult = ''
        }
    }).catch(() => {
        showMessage('验证码发送失败', 'error')
        refreshPicture()
        phoneForm.pictureResult = ''
    })
}

// 手机验证码登录
const onPhoneSubmit = () => {
    phoneFormRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }
        loading.value = true
        // 调用登录接口，type=1 表示手机验证码登录
        login(phoneForm.phone, '', 1, phoneForm.code).then((res) => {
            console.log(res)
            if (res.success == true) {
                let token = res.data
                setToken(token)
                getUserInfoWithAuth().then(userRes => {
                    if (userRes.success == true) {
                        userStore.setUserInfoDirect(userRes.data)
                        showMessage('登录成功')
                        router.push('/')
                    } else {
                        showMessage('获取用户信息失败', 'error')
                    }
                }).catch(() => {
                    showMessage('获取用户信息失败', 'error')
                })
            } else {
                let message = res.message
                showMessage(message, 'error')
            }
        }).finally(() => {
            loading.value = false
        })
    })
}

const onSubmit = () => {
    console.log('登录')
    // 先验证 form 表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }
        // 开始加载
        loading.value = true

        // 调用登录接口，type=2 表示账号密码登录
        login(form.phone, form.password, 2).then((res) => {
            console.log(res)
            // 判断是否成功
            if (res.success == true) {
                // 存储 Token 到 Cookie 中
                let token = res.data
                setToken(token)

                // 使用新接口获取用户信息
                getUserInfoWithAuth().then(userRes => {
                    if (userRes.success == true) {
                        // 将用户信息存储到全局状态中
                        userStore.setUserInfoDirect(userRes.data)

                        // 提示登录成功
                        showMessage('登录成功')

                        // 登录成功后跳转到首页
                        router.push('/')
                    } else {
                        showMessage('获取用户信息失败', 'error')
                    }
                }).catch(() => {
                    showMessage('获取用户信息失败', 'error')
                })
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示消息
                showMessage(message, 'error')
            }
        })
        .finally(() => {
            // 结束加载
            loading.value = false
        })
    })
}

// 按回车键后，执行登录事件
function onKeyUp(e) {
    console.log(e)
    if (e.key == 'Enter') {
        if (loginType.value === 'password') {
            onSubmit()
        } else {
            onPhoneSubmit()
        }
    }
}

// 添加键盘监听
onMounted(() => {
    console.log('添加键盘监听')
    document.addEventListener('keyup', onKeyUp)
})

// 移除键盘监听
onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
    stopPictureTimer()
})

// 是否是白天
const isLight = ref(true)
const isDark = useDark({
  onChanged(dark) {
    // update the dom, call the API or something
    console.log('onchange:' + dark)
    if (dark) {
        // 给 body 添加 class="dark"
        document.documentElement.classList.add('dark');
        // 设置 switch 的值
        isLight.value = false
    } else {
        // 移除 body 中添加 class="dark"
        document.documentElement.classList.remove('dark');
        isLight.value = true
    }
  },
})
const toggleDark = useToggle(isDark)
</script>

<style scoped>
/* The switch - the box around the slider */
.switch {
  font-size: 14px;
  position: relative;
  display: inline-block;
  width: 3.5em;
  height: 2em;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  --background: #28096b;
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--background);
  transition: .5s;
  border-radius: 30px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 1.4em;
  width: 1.4em;
  border-radius: 50%;
  left: 10%;
  bottom: 15%;
  box-shadow: inset 8px -4px 0px 0px #fff000;
  background: var(--background);
  transition: .5s;
}

input:checked + .slider {
  background-color: #522ba7;
}

input:checked + .slider:before {
  transform: translateX(100%);
  box-shadow: inset 15px -4px 0px 15px #fff000;
}</style>