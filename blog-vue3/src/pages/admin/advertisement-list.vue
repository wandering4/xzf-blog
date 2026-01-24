<template>
    <div>
        <el-card shadow="never">
            <!-- 新增广告图按钮和编辑相关按钮 -->
            <div class="flex justify-between mb-4">
                <!-- 左边：新增广告图按钮 -->
                <el-button @click="openAddDialog" :disabled="!isEditMode">
                    <el-icon class="mr-1">
                        <Plus />
                    </el-icon>
                    新增广告图
                </el-button>

                <!-- 右边：编辑相关按钮 -->
                <div class="flex">
                    <el-button @click="toggleEditMode" :disabled="isEditMode">
                        <el-icon class="mr-1">
                            <Edit />
                        </el-icon>
                        编辑
                    </el-button>
                    <el-button @click="cancelEdit" :disabled="!isEditMode" class="ml-2">
                        <el-icon class="mr-1">
                            <Close />
                        </el-icon>
                        取消
                    </el-button>
                    <el-button type="primary" @click="saveSortOrder" :disabled="!isEditMode" class="ml-2">
                        <el-icon class="mr-1">
                            <Check />
                        </el-icon>
                        完成
                    </el-button>
                </div>
            </div>

            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe style="width: 100%" v-loading="tableLoading">
                <el-table-column label="展示顺序" width="100">
                    <template #default="scope">
                        {{ scope.$index + 1 }}
                    </template>
                </el-table-column>
                <el-table-column prop="url" label="图片" width="200">
                    <template #default="scope">
                        <el-image style="width: 150px; height: 100px;" :src="scope.row.url" fit="cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="200">
                    <template #default="scope">
                        <el-button size="small" @click="moveUp(scope.$index)" :disabled="!isEditMode || scope.$index === 0">
                            <el-icon class="mr-1">
                                <ArrowUp />
                            </el-icon>
                            上移
                        </el-button>
                        <el-button size="small" @click="moveDown(scope.$index)" :disabled="!isEditMode || scope.$index === tableData.length - 1">
                            <el-icon class="mr-1">
                                <ArrowDown />
                            </el-icon>
                            下移
                        </el-button>
                        <el-button type="danger" size="small" @click="deletePictureItem(scope.row)" :disabled="!isEditMode">
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

        <!-- 新增广告图弹窗 -->
        <el-dialog v-model="addDialogVisible" title="新增广告图" width="600px" :close-on-click-modal="false">
            <el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="100px">
                <el-form-item label="图片来源" required>
                    <el-radio-group v-model="imageSource">
                        <el-radio label="upload">上传图片</el-radio>
                        <el-radio label="url">图片链接</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item v-if="imageSource === 'upload'" label="选择图片" required>
                    <el-upload
                        ref="uploadRef"
                        action="#"
                        :on-change="handleFileChange"
                        :auto-upload="false"
                        :show-file-list="false"
                        accept="image/*"
                        class="upload-demo">
                        <el-button type="primary" :loading="uploading">
                            <el-icon class="mr-1">
                                <Upload />
                            </el-icon>
                            点击上传
                        </el-button>
                        <template #tip>
                            <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
                        </template>
                    </el-upload>
                    <div v-if="addForm.url" class="mt-2">
                        <el-image :src="addForm.url" style="width: 150px; height: 100px;" fit="cover" />
                    </div>
                </el-form-item>

                <el-form-item v-else label="图片链接" prop="url" required>
                    <el-input v-model="addForm.url" placeholder="请输入图片链接" clearable />
                    <div v-if="addForm.url" class="mt-2">
                        <el-image :src="addForm.url" style="width: 150px; height: 100px;" fit="cover" />
                    </div>
                </el-form-item>
            </el-form>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmAdd" :loading="submitting">
                        确定
                    </el-button>
                </span>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Delete, ArrowUp, ArrowDown, Check, Edit, Close, Plus, Upload } from '@element-plus/icons-vue'
import { showMessage, showModel } from '@/composables/util'
import { getPictureList, deletePicture, editPicture } from '@/api/admin/settings'
import { uploadFile } from '@/api/admin/file'

// 表格数据
const tableData = ref([])
const tableLoading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)

// 编辑状态
const isEditMode = ref(false)

// 新增广告图相关
const addDialogVisible = ref(false)
const imageSource = ref('upload') // upload 或 url
const uploading = ref(false)
const submitting = ref(false)
const addFormRef = ref(null)
const uploadRef = ref(null)

// 新增表单数据
const addForm = reactive({
    url: '',
    file: null
})

// 表单验证规则
const addFormRules = {
    url: [
        { required: true, message: '请提供图片链接', trigger: 'blur' }
    ]
}

// 待新增的广告图列表（临时存储）
const pendingAddItems = ref([])

// 获取表格数据
const getTableData = async () => {
    tableLoading.value = true
    try {
        const res = await getPictureList({
            current: current.value,
            size: size.value
        })
        if (res.success) {
            // 按 sortOrder 排序
            tableData.value = res.data.sort((a, b) => a.sortOrder - b.sortOrder)
            total.value = res.totalCount
        } else {
            showMessage(res.message || '获取数据失败', 'error')
        }
    } catch (error) {
        console.error('获取图片列表失败:', error)
        showMessage('获取数据失败', 'error')
    } finally {
        tableLoading.value = false
    }
}

// 上移图片
const moveUp = (index) => {
    if (index <= 0) return

    // 交换数组中的位置
    const temp = tableData.value[index]
    tableData.value[index] = tableData.value[index - 1]
    tableData.value[index - 1] = temp

    // 重新分配sortOrder
    tableData.value.forEach((item, idx) => {
        item.sortOrder = idx + 1
    })
}

// 下移图片
const moveDown = (index) => {
    if (index >= tableData.value.length - 1) return

    // 交换数组中的位置
    const temp = tableData.value[index]
    tableData.value[index] = tableData.value[index + 1]
    tableData.value[index + 1] = temp

    // 重新分配sortOrder
    tableData.value.forEach((item, idx) => {
        item.sortOrder = idx + 1
    })
}

// 删除图片
const deletePictureItem = async (row) => {
    showModel('确定要删除这张图片吗？').then(async () => {
        try {
            // 检查id是否为null，如果为null说明是编辑态新增但未保存的数据
            if (row.id === null || row.id === undefined) {
                // 从表格数据中移除该项
                const index = tableData.value.findIndex(item => item === row)
                if (index > -1) {
                    tableData.value.splice(index, 1)
                    // 重新分配排序
                    tableData.value.forEach((item, idx) => {
                        item.sortOrder = idx + 1
                    })
                }

                // 从待新增列表中移除（如果存在）
                const pendingIndex = pendingAddItems.value.findIndex(item => item === row)
                if (pendingIndex > -1) {
                    pendingAddItems.value.splice(pendingIndex, 1)
                }

                showMessage('删除成功')
                return
            }

            // id不为null，调用后端删除接口
            const res = await deletePicture(row.id)
            if (res.success) {
                showMessage('删除成功')
                getTableData()
            } else {
                showMessage(res.message || '删除失败', 'error')
            }
        } catch (error) {
            console.error('删除失败:', error)
            showMessage('删除失败', 'error')
        }
    })
}

// 保存排序顺序和新增的项目
const saveSortOrder = async () => {
    try {
        // 合并现有数据和新增数据
        const allItems = tableData.value.map(item => ({
            url: item.url,
            sortOrder: item.sortOrder
        }))

        const res = await editPicture({ items: allItems })
        if (res.success) {
            showMessage('保存成功')
            isEditMode.value = false // 退出编辑态
            pendingAddItems.value = [] // 清空待新增列表
            getTableData() // 重新获取数据以确保与后端同步
        } else {
            showMessage(res.message || '保存失败', 'error')
        }
    } catch (error) {
        console.error('保存失败:', error)
        showMessage('保存失败', 'error')
    }
}

// 切换编辑模式
const toggleEditMode = () => {
    isEditMode.value = true
}

// 取消编辑
const cancelEdit = () => {
    isEditMode.value = false
    pendingAddItems.value = [] // 清空待新增列表
    getTableData() // 刷新页面重新渲染数据
}

// 分页大小改变
const handleSizeChange = (val) => {
    size.value = val
    current.value = 1
    getTableData()
}

// 打开新增弹窗
const openAddDialog = () => {
    addDialogVisible.value = true
    imageSource.value = 'upload'
    addForm.url = ''
    addForm.file = null
    if (addFormRef.value) {
        addFormRef.value.clearValidate()
    }
}

// 文件选择处理
const handleFileChange = (file) => {
    // 验证文件类型
    const isImage = file.raw.type.startsWith('image/')
    const isLt2M = file.raw.size / 1024 / 1024 < 2

    if (!isImage) {
        showMessage('只能上传图片文件！', 'error')
        return false
    }
    if (!isLt2M) {
        showMessage('上传图片大小不能超过 2MB！', 'error')
        return false
    }

    uploading.value = true

    // 创建FormData对象
    let formData = new FormData()
    formData.append('file', file.raw)

    // 调用上传接口
    uploadFile(formData).then((res) => {
        uploading.value = false
        if (res.success) {
            addForm.url = res.data
            showMessage('图片上传成功')
        } else {
            showMessage(res.message || '上传失败', 'error')
        }
    }).catch((error) => {
        uploading.value = false
        showMessage('上传失败', 'error')
        console.error('上传失败:', error)
    })
}

// 确认新增
const confirmAdd = async () => {
    try {
        // 根据图片来源分别验证
        if (imageSource.value === 'url') {
            // 链接模式：验证表单
            const valid = await addFormRef.value.validate().catch(() => false)
            if (!valid) {
                return
            }
        } else {
            // 上传模式：检查是否已上传图片
            if (!addForm.url) {
                showMessage('请先上传图片', 'warning')
                return
            }
        }

        submitting.value = true

        // 创建新的广告图对象，添加到待新增列表
        const newItem = {
            url: addForm.url,
            sortOrder: 1, // 默认添加到第一个位置
            createTime: new Date().toLocaleString(),
            isNew: true // 标记为新增项
        }

        // 将新项目添加到待新增列表
        pendingAddItems.value.unshift(newItem)

        // 临时添加到表格显示（用于预览）
        tableData.value.unshift(newItem)

        // 重新分配所有项目的排序
        tableData.value.forEach((item, index) => {
            item.sortOrder = index + 1
        })

        showMessage('广告图已添加到列表，请点击完成按钮保存')
        addDialogVisible.value = false

    } catch (error) {
        console.error('新增失败:', error)
        showMessage('新增失败', 'error')
    } finally {
        submitting.value = false
    }
}

// 页面加载时获取数据
onMounted(() => {
    getTableData()
})
</script>

<style scoped>
.el-image {
    border-radius: 4px;
}
</style>