<template>
    <div>
        <!-- 表头分页查询条件， shadow="never" 指定 card 卡片组件没有阴影 -->
        <el-card shadow="never" class="mb-5">
            <!-- flex 布局，内容垂直居中 -->
            <div class="flex items-center">
                <el-text>文章标题</el-text>
                <div class="ml-3 w-52 mr-5"><el-input v-model="searchArticleTitle" placeholder="请输入（模糊查询）" /></div>

                <el-text>创建日期</el-text>
                <div class="ml-3 w-30 mr-5">
                    <!-- 日期选择组件（区间选择） -->
                    <el-date-picker v-model="pickDate" type="daterange" range-separator="至" start-placeholder="开始时间"
                        end-placeholder="结束时间" size="default" :shortcuts="shortcuts" @change="datepickerChange" />
                </div>

                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">查询</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- 写文章按钮 -->
            <div class="mb-5">
                <el-button type="primary" @click="isArticlePublishEditorShow = true">
                    <el-icon class="mr-1">
                        <EditPen />
                    </el-icon>
                    写文章
                </el-button>
            </div>

            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column prop="id" label="ID" width="50" />
                <el-table-column prop="title" label="标题" width="250" />
                <el-table-column prop="cover" label="封面" width="120">
                    <template #default="scope">
                        <el-image style="width: 100px;" :src="scope.row.cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="summary" label="摘要" width="200">
                    <template #default="scope">
                        <div class="text-gray-600 text-sm line-clamp-2" :title="scope.row.summary">
                            {{ scope.row.summary || '暂无摘要' }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="category" label="分类" width="120">
                    <template #default="scope">
                        <div v-if="scope.row.category" class="flex flex-wrap gap-1">
                            <span class="cursor-pointer bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded hover:bg-blue-200 hover:text-blue-900 dark:bg-blue-900 dark:hover:bg-blue-950 dark:text-blue-300">
                                {{ scope.row.category.name }}
                            </span>
                        </div>
                        <span v-else class="text-gray-400">未分类</span>
                    </template>
                </el-table-column>
                <el-table-column prop="tags" label="标签" width="200">
                    <template #default="scope">
                        <div v-if="scope.row.tags && scope.row.tags.length > 0" class="flex flex-wrap gap-1">
                            <span v-for="(tag, index) in scope.row.tags" :key="index"
                                class="cursor-pointer bg-green-100 text-green-800 text-xs font-medium px-2.5 py-0.5 rounded hover:bg-green-200 hover:text-green-900 dark:bg-green-900 dark:hover:bg-green-950 dark:text-green-300">
                                {{ tag.name }}
                            </span>
                        </div>
                        <span v-else class="text-gray-400">无标签</span>
                    </template>
                </el-table-column>
                <el-table-column prop="createDate" label="发布时间" width="180" />
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button size="small" @click="showArticleUpdateEditor(scope.row)">
                      <el-icon class="mr-1">
                        <Edit />
                      </el-icon>
                      编辑</el-button>
                    <el-button size="small" @click="goArticleDetailPage(scope.row.id)">
                      <el-icon class="mr-1">
                        <View />
                      </el-icon>
                      查看</el-button>
                    <el-button type="danger" size="small" @click="deleteArticleSubmit(scope.row)">
                      <el-icon class="mr-1">
                        <Delete />
                      </el-icon>
                      删除
                    </el-button>
                  </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="mt-10 flex justify-center">
                <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[10, 20, 50]"
                    :small="false" :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="getTableData" />
            </div>

        </el-card>

        <!-- 编辑博客 -->
        <el-dialog v-model="isArticleUpdateEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false">
            <template #header="{ close, titleId, titleClass }">
                <!-- 固钉组件，固钉到顶部 -->
                <el-affix :offset="20" style="width: 100%;">
                    <!-- 指定 flex 布局， 高度为 10， 背景色为白色 -->
                    <div class="flex h-10 bg-white">
                        <!-- 字体加粗 -->
                        <h4 class="font-bold">编辑文章</h4>
                        <!-- 靠右对齐 -->
                        <div class="ml-auto flex">
                            <el-button @click="isArticleUpdateEditorShow = false">取消</el-button>
                            <el-button type="primary" @click="updateSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                保存
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <!-- label-position="top" 用于指定 label 元素在上面 -->
            <el-form :model="updateArticleForm" ref="updateArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="updateArticleForm.title" autocomplete="off" size="large" maxlength="40"
                        show-word-limit clearable />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <!-- Markdown 编辑器 -->
                    <div class="mb-2">
                        <el-alert
                            title="提示：当前显示的是 HTML 格式内容，请手动转换为 Markdown 格式进行编辑"
                            type="warning"
                            :closable="false"
                            show-icon />
                    </div>
                    <MdEditor v-model="updateArticleForm.content" @onUploadImg="onUploadImg"
                        editorId="updateArticleEditor" />
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleUpdateCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="updateArticleForm.cover" :src="updateArticleForm.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="摘要" prop="summary">
                    <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
                    <el-input v-model="updateArticleForm.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" />
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="updateArticleForm.categoryId" clearable placeholder="---请选择---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <span class="w-60">
                        <!-- 标签选择 -->
                        <el-select v-model="updateArticleForm.tags" multiple filterable remote reserve-keyword
                            placeholder="请输入文章标签" remote-show-suffix allow-create default-first-option
                            :remote-method="remoteMethod" :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>

        <!-- 写博客 -->
        <el-dialog v-model="isArticlePublishEditorShow" :fullscreen="true" :show-close="false"
            :close-on-press-escape="false">
            <template #header="{ close, titleId, titleClass }">
                <!-- 固钉组件，固钉到顶部 -->
                <el-affix :offset="20" style="width: 100%;">
                    <!-- 指定 flex 布局， 高度为 10， 背景色为白色 -->
                    <div class="flex h-10 bg-white">
                        <!-- 字体加粗 -->
                        <h4 class="font-bold">写文章</h4>
                        <!-- 靠右对齐 -->
                        <div class="ml-auto flex">
                            <el-button @click="isArticlePublishEditorShow = false">取消</el-button>
                            <el-button type="primary" @click="publishArticleSubmit">
                                <el-icon class="mr-1">
                                    <Promotion />
                                </el-icon>
                                发布
                            </el-button>
                        </div>
                    </div>
                </el-affix>
            </template>
            <!-- label-position="top" 用于指定 label 元素在上面 -->
            <el-form :model="form" ref="publishArticleFormRef" label-position="top" size="large" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" autocomplete="off" size="large" maxlength="40" show-word-limit
                        clearable />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <!-- Markdown 编辑器 -->
                    <MdEditor v-model="form.content" @onUploadImg="onUploadImg" editorId="publishArticleEditor" />
                </el-form-item>
                <el-form-item label="封面" prop="cover">
                    <el-upload class="avatar-uploader" action="#" :on-change="handleCoverChange" :auto-upload="false"
                        :show-file-list="false">
                        <img v-if="form.cover" :src="form.cover" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="摘要" prop="summary">
                    <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
                    <el-input v-model="form.summary" :rows="3" type="textarea" placeholder="请输入文章摘要" />
                </el-form-item>
                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="form.categoryId" clearable placeholder="---请选择---" size="large">
                        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标签" prop="tags">
                    <span class="w-60">
                        <!-- 标签选择 -->
                        <el-select v-model="form.tags" multiple filterable remote reserve-keyword placeholder="请输入文章标签"
                            remote-show-suffix allow-create default-first-option :remote-method="remoteMethod"
                            :loading="tagSelectLoading" size="large">
                            <el-option v-for="item in tags" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </span>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Search, RefreshRight, EditPen, Edit, View, Promotion, Plus } from '@element-plus/icons-vue'
import {getArticleDetail, getPersonalArticlePageList, updateArticle, deleteArticle, publishArticle} from '@/api/frontend/article'
import { uploadFile } from '@/api/admin/file'
import { getCategorySelectList } from '@/api/admin/category'
import { searchTags, getTagSelectList } from '@/api/admin/tag'
import { showMessage, showModel } from '@/composables/util'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const router = useRouter()

// 文章标题搜索关键词
const searchArticleTitle = ref('')
// 日期选择器数据
const pickDate = ref([])
// 日期选择器快捷选项
const shortcuts = [
    {
        text: '最近一周',
        value: (() => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        })(),
    },
    {
        text: '最近一个月',
        value: (() => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        })(),
    },
    {
        text: '最近三个月',
        value: (() => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            return [start, end]
        })(),
    },
]

// 当前页码，给了一个默认值 1
const current = ref(1)
// 每页显示的数据量，给了个默认值 10
const size = ref(10)
// 数据总量
const total = ref(0)
// 总页数
const pages = ref(0)
// 表格数据
const tableData = ref([])
// 表格加载状态
const tableLoading = ref(false)

// 获取表格数据
function getTableData() {
    tableLoading.value = true

    // 构建查询参数
    const params = {
        current: current.value,
        size: size.value,
        title: searchArticleTitle.value || null,
        startCreateTime: pickDate.value && pickDate.value.length > 0 ? pickDate.value[0] : null,
        endCreateTime: pickDate.value && pickDate.value.length > 0 ? pickDate.value[1] : null
    }

    getPersonalArticlePageList(params).then((res) => {
        console.log(res)
        if (res.success) {
            tableData.value = res.data  // 直接使用data数组
            current.value = res.pageNo
            size.value = res.pageSize
            total.value = res.totalCount
            pages.value = res.totalPage
        }
    }).finally(() => tableLoading.value = false)
}

// 日期选择器变化事件
function datepickerChange() {
    console.log(pickDate.value)
}

// 重置查询条件
function reset() {
    searchArticleTitle.value = ''
    pickDate.value = []
    current.value = 1
    getTableData()
}

// 分页大小改变事件
function handleSizeChange(val) {
    size.value = val
    current.value = 1
    getTableData()
}

// 跳转到文章详情页
function goArticleDetailPage(articleId) {
    router.push('/article/' + articleId)
}

// 是否显示文章发布对话框
const isArticlePublishEditorShow = ref(false)
// 发布文章表单引用
const publishArticleFormRef = ref(null)

// 表单对象
const form = reactive({
    id: null,
    title: '',
    content: '请输入内容',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// 删除文章
const deleteArticleSubmit = (row) => {
    console.log(row)
    showModel('是否确定要删除该文章？').then(() => {
        deleteArticle(row.id).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            showMessage('删除成功')
            // 重新请求分页接口，渲染数据
            getTableData()
        })
    }).catch(() => {
        console.log('取消了')
    })
}


// 修改文章表单对象
const updateArticleForm = reactive({
    id: null,
    title: '',
    content: '请输入内容',
    cover: '',
    categoryId: null,
    tags: [],
    summary: ""
})

// 表单校验规则
const rules = {
    title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' },
        { min: 1, max: 40, message: '文章标题要求大于1个字符，小于40个字符', trigger: 'blur' },
    ],
    content: [{ required: true }],
    cover: [{ required: true }],
    categoryId: [{ required: true, message: '请选择文章分类', trigger: 'blur' }],
    tags: [{ required: true, message: '请选择文章标签', trigger: 'blur' }],
}

// 是否显示编辑文章对话框
const isArticleUpdateEditorShow = ref(false)
// 编辑文章表单引用
const updateArticleFormRef = ref(null)

// 文章分类
const categories = ref([])
// 标签 select Loading 状态，默认不显示
const tagSelectLoading = ref(false)
// 文章标签
const tags = ref([])
// 编辑文章按钮点击事件
const showArticleUpdateEditor = (row) => {
  // 显示编辑文章对话框
  isArticleUpdateEditorShow.value = true
  // 拿到文章 ID
  let articleId = row.id
  getArticleDetail(articleId).then((res) => {
    if (res.success) {
      // 设置表单数据
      updateArticleForm.id = articleId // 从 row 中获取文章 ID
      updateArticleForm.title = res.data.title
      updateArticleForm.cover = row.cover || '' // 从表格行数据中获取封面，如果没有则为空
      updateArticleForm.content = res.data.content // HTML 内容，需要用户手动转换为 Markdown
      updateArticleForm.categoryId = res.data.categoryId
      updateArticleForm.tags = res.data.tags ? res.data.tags.map(tag => tag.id) : [] // 提取标签 ID 数组
      updateArticleForm.summary = '' // 新的接口没有摘要字段，设置为空
    }
  })
}

// 保存文章
const updateSubmit = () => {
  console.log('tijiao')
  updateArticleFormRef.value.validate((valid) => {
    // 校验表单
    if (!valid) {
      return false
    }

    // 请求更新文章接口
    updateArticle(updateArticleForm).then((res) => {
      if (res.success == false) {
        // 获取服务端返回的错误消息
        let message = res.message
        // 提示错误消息
        showMessage(message, 'error')
        return
      }

      showMessage('保存成功')
      // 隐藏编辑框
      isArticleUpdateEditorShow.value = false
      // 重新请求分页接口，渲染列表数据
      getTableData()
    })
  })
}

// 编辑文章：上传文章封面图片
const handleUpdateCoverChange = (file) => {
    // 表单对象
    let formData = new FormData()
    // 添加 file 字段，并将文件传入
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // 成功则设置表单对象中的封面链接，并提示上传成功
        updateArticleForm.cover = e.data
        showMessage('上传成功')
    })
}

// 上传文章封面图片
const handleCoverChange = (file) => {
    // 表单对象
    let formData = new FormData()
    // 添加 file 字段，并将文件传入
    formData.append('file', file.raw)
    uploadFile(formData).then((e) => {
        // 响参失败，提示错误消息
        if (e.success == false) {
            let message = e.message
            showMessage(message, 'error')
            return
        }

        // 成功则设置表单对象中的封面链接，并提示上传成功
        form.cover = e.data
        showMessage('上传成功')
    })
}

// 发布文章
const publishArticleSubmit = () => {
    console.log('提交 md 内容：' + form.content)
    // 校验表单
    publishArticleFormRef.value.validate((valid) => {
        if (!valid) {
            return false
        }

        publishArticle(form).then((res) => {
            if (res.success == false) {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
                return
            }

            showMessage('发布成功')
            // 隐藏发布文章对话框
            isArticlePublishEditorShow.value = false
            // 将 form 表单字段置空
            form.title = ''
            form.content = ''
            form.cover = ''
            form.summary = ''
            form.categoryId = null
            form.tags = []
            // 重新请求分页接口，渲染列表数据
            getTableData()
        })
    })
}

// 编辑器图片上传
const onUploadImg = async (files, callback) => {
    const res = await Promise.all(
        files.map((file) => {
            return new Promise((rev, rej) => {
                console.log('==> 编辑器开始上传文件...')
                let formData = new FormData()
                formData.append("file", file);
                uploadFile(formData).then((res) => {
                    console.log(res)
                    console.log('访问路径：' + res.data)
                    // 调用 callback 函数，回显上传图片
                    callback([res.data]);
                })
            });
        })
    );
}

// 根据用户输入的标签名称，远程模糊查询
const remoteMethod = (query) => {
    console.log('远程搜索：' + tags.value)
    // 如果用户的查询关键词不为空
    if (query) {
        // 显示 loading
        tagSelectLoading.value = true
        // 调用标签模糊查询接口
        searchTags(query).then((e) => {
            if (e.success) {
                // 设置到 tags 变量中
                tags.value = e.data
            }
        }).finally(() => tagSelectLoading.value = false) // 隐藏 loading
    }
}


// 页面初始化时获取数据
getTableData()

// 获取分类数据
getCategorySelectList().then((e) => {
    console.log('获取分类数据')
    categories.value = e.data
})

// 渲染标签数据
getTagSelectList().then(res => {
    tags.value = res.data
})

</script>

<style scoped>
/* 封面图片样式 */
.avatar-uploader .avatar {
    width: 200px;
    height: 100px;
    display: block;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 100px;
    text-align: center;
}

/* 指定 select 下拉框宽度 */
.el-select--large {
    width: 600px;
}

/* 摘要文本截断样式 */
.line-clamp-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>

<style>
.md-editor-footer {
    height: 40px;
}
</style>