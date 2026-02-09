<template>
  <div class="login-page">
    <!-- 背景图片 -->
    <img src="@/assets/login-bg.png" alt="login background" class="login__bg">

    <!-- 返回首页按钮 -->
    <el-button class="back-home-btn" type="link" @click="goHome">
      <i class="ri-arrow-left-line"></i> 返回首页
    </el-button>

    <!-- 登录表单卡片 -->
    <div class="login__form-container">
      <div class="login__form animate__animated animate__fadeInUp">
        <h1 class="login__title">Login</h1>

        <!-- 登录方式切换 -->
        <div class="login__type-switch">
          <span class="switch-line"></span>
          <span class="switch-text">{{ loginType === 'password' ? '账号密码登录' : '手机验证码登录' }}</span>
          <span class="switch-line"></span>
        </div>

        <div class="login__type-btn">
          <el-button type="primary" link @click="switchLoginType">
            {{ loginType === 'password' ? '使用手机验证码登录' : '使用账号密码登录' }}
          </el-button>
        </div>

        <!-- 账号密码登录表单 -->
        <el-form v-if="loginType === 'password'" class="login__form-content" ref="formRef" :rules="rules" :model="form">
          <!-- 手机号输入框 -->
          <div class="login__box">
            <i class="ri-user-3-line login__icon"></i>
            <div class="login__box-input">
              <el-input
                class="login__input"
                v-model="form.phone"
                placeholder=" "
                id="phone-input"
                clearable
              />
              <label for="phone-input" class="login__label">手机号</label>
            </div>
          </div>

          <!-- 密码输入框 -->
          <div class="login__box">
            <i class="ri-lock-2-line login__icon"></i>
            <div class="login__box-input">
              <el-input
                class="login__input"
                v-model="form.password"
                placeholder=" "
                type="password"
                id="password-input"
                show-password
                clearable
              />
              <label for="password-input" class="login__label">密码</label>
            </div>
          </div>

          <el-form-item class="login__submit">
            <el-button class="login__button" :loading="loading" type="primary" @click="onSubmit">登录</el-button>
          </el-form-item>
        </el-form>

        <!-- 手机验证码登录表单 -->
        <el-form v-else class="login__form-content" ref="phoneFormRef" :rules="phoneRules" :model="phoneForm">
          <!-- 手机号输入框 -->
          <div class="login__box">
            <i class="ri-user-3-line login__icon"></i>
            <div class="login__box-input">
              <el-input
                class="login__input"
                v-model="phoneForm.phone"
                placeholder=" "
                id="phone-code-input"
                clearable
              />
              <label for="phone-code-input" class="login__label">手机号</label>
            </div>
          </div>

          <!-- 图片验证码 -->
          <div class="login__box">
            <i class="ri-image-line login__icon"></i>
            <div class="login__box-input login__box-input-flex">
              <el-input
                class="login__input login__pic-input"
                v-model="phoneForm.pictureResult"
                placeholder=" "
                id="pic-input"
              />
              <label for="pic-input" class="login__label">图片验证码</label>
              <img v-if="pictureUrl" :src="pictureUrl" @click="refreshPicture" class="login__captcha" alt="验证码" />
            </div>
          </div>

          <!-- 短信验证码 -->
          <div class="login__box">
            <i class="ri-message-3-line login__icon"></i>
            <div class="login__box-input login__box-input-flex">
              <el-input
                class="login__input login__code-input"
                v-model="phoneForm.code"
                placeholder=" "
                id="code-input"
              />
              <label for="code-input" class="login__label">短信验证码</label>
              <el-button class="login__code-btn" size="large" type="primary" :disabled="codeBtnDisabled || !phoneForm.pictureResult" @click="sendCode">
                {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
              </el-button>
            </div>
          </div>

          <el-form-item class="login__submit">
            <el-button class="login__button" :loading="loading" type="primary" @click="onPhoneSubmit">登录</el-button>
          </el-form-item>
        </el-form>

        <!-- 暗色模式固定 -->
      </div>
    </div>
  </div>
</template>

<script setup>
// 引入 Remix Icons 图标
import { login, getUserInfoWithAuth, getVerificationPicture, sendVerificationCode } from '@/api/admin/user'
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { showMessage } from '@/composables/util'
import { setToken } from '@/composables/cookie'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 登录方式：'password' 账号密码登录，'phone' 手机验证码登录
const loginType = ref('password')

// 定义响应式的表单对象
const form = reactive({
    phone: '17891997260',
    password: ''
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
</script>

<style scoped>
/*=============== GOOGLE FONTS ===============*/
@import url("https://fonts.googleapis.com/css2?family=Poppins:wght@400;500&display=swap");

/*=============== LOGIN PAGE - 暗色主题 ===============*/
.login-page {
  position: relative;
  height: 100vh;
  display: grid;
  align-items: center;
  font-family: "Poppins", sans-serif;
  background-color: #1a1a2e;
}

.login__bg {
  position: absolute;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  z-index: 0;
}

/* 返回首页按钮 */
.back-home-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 10;
  color: #fff !important;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.back-home-btn:hover {
  text-decoration: underline;
}

/* 表单容器 */
.login__form-container {
  position: relative;
  z-index: 1;
  width: 100%;
  display: flex;
  justify-content: center;
  padding-inline: 1.5rem;
}

/* 登录表单卡片 */
.login__form {
  background-color: hsla(0, 0%, 10%, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.8);
  padding: 2.5rem 2rem;
  border-radius: 1rem;
  backdrop-filter: blur(10px);
  width: 100%;
  max-width: 420px;
  transition: all 0.3s ease;
}

.login__form:hover {
  background-color: hsla(0, 0%, 10%, 0.25);
  border-color: rgba(255, 255, 255, 1);
}

/* 标题 */
.login__title {
  text-align: center;
  font-size: 1.5rem;
  font-weight: 500;
  color: #fff;
  margin-bottom: 1.5rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 登录方式切换文字 */
.login__type-switch {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 10px;
}

.switch-line {
  width: 50px;
  height: 1px;
  background-color: rgba(255, 255, 255, 0.6);
}

.switch-text {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.875rem;
}

/* 切换登录方式按钮 */
.login__type-btn {
  text-align: center;
  margin-bottom: 1.5rem;
}

.login__type-btn .el-button--primary {
  color: #ffd700 !important;
  font-size: 0.875rem;
}

.login__type-btn .el-button--primary:hover {
  text-decoration: underline;
}

/* 表单内容 */
.login__form-content {
  display: grid;
  row-gap: 1.25rem;
}

/* 输入框容器 */
.login__box {
  display: grid;
  grid-template-columns: max-content 1fr;
  align-items: center;
  column-gap: 0.75rem;
  border-bottom: 2px solid rgba(255, 255, 255, 0.6);
  padding-bottom: 5px;
  transition: border-color 0.3s ease;
}

.login__box:focus-within {
  border-color: #ffd700;
}

.login__icon {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
}

.login__box-input {
  position: relative;
}

.login__box-input-flex {
  display: flex;
  align-items: center;
}

.login__input {
  width: 100%;
  padding: 8px 0;
  background: none;
  color: #fff;
  font-size: 1rem;
  font-family: "Poppins", sans-serif;
  position: relative;
  z-index: 1;
}

.login__input::placeholder {
  color: transparent;
}

.login__label {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  pointer-events: none;
  font-size: 1rem;
}

/* 输入框聚焦或非空时上移标签 */
.login__input:focus + .login__label,
.login__input:not(:placeholder-shown) + .login__label {
  top: -5px;
  font-size: 0.75rem;
}

/* 图片验证码输入框 */
.login__pic-input {
  flex: 1;
}

/* 验证码图片 */
.login__captcha {
  width: 80px;
  height: 36px;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: border-color 0.3s ease;
}

.login__captcha:hover {
  border-color: #ffd700;
}

/* 短信验证码输入框 */
.login__code-input {
  flex: 1;
}

/* 获取验证码按钮 */
.login__code-btn {
  padding: 8px 12px !important;
  margin-left: 10px;
  font-size: 0.75rem !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
  border-color: rgba(255, 255, 255, 0.95) !important;
  color: #333 !important;
  border-radius: 6px !important;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.login__code-btn:hover:not(:disabled) {
  background-color: #fff !important;
  transform: scale(1.05);
}

.login__code-btn:disabled {
  background-color: rgba(255, 255, 255, 0.3) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  color: #999 !important;
}

/* 登录按钮 */
.login__submit {
  margin-bottom: 0;
  margin-top: 10px;
}

.login__button {
  width: 100%;
  padding: 14px;
  border-radius: 8px;
  background-color: #409EFF;
  font-weight: 500;
  font-size: 1rem;
  font-family: "Poppins", sans-serif;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  color: #fff;
}

.login__button:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

/* Element Plus 输入框样式覆盖 */
.login__input :deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  padding: 0 !important;
}

.login__input :deep(.el-input__inner) {
  background: transparent !important;
  color: #fff !important;
  font-family: "Poppins", sans-serif;
  font-size: 1rem !important;
  padding: 0 !important;
  height: auto !important;
}

.login__input :deep(.el-input__prefix),
.login__input :deep(.el-input__suffix) {
  display: none;
}

.login__input :deep(.el-form-item__error) {
  color: #ff6b6b;
}

/*=============== RESPONSIVE ===============*/
@media screen and (min-width: 576px) {
  .login__form-container {
    justify-content: center;
  }

  .login__form {
    width: 400px;
    padding: 3rem 2.5rem;
  }

  .login__title {
    font-size: 1.75rem;
  }
}

@media screen and (min-width: 992px) {
  .login__form {
    width: 420px;
    padding: 3.5rem 3rem;
  }
}

/*=============== ANIMATIONS ===============*/
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate__fadeInUp {
  animation: fadeInUp 0.6s ease-out;
}
</style>