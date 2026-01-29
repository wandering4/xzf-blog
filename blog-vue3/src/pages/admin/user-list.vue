<template>
    <div>
        <!-- 查询条件 -->
        <el-card shadow="never" class="mb-5">
            <div class="flex items-center">
                <el-text class="w-20">用户姓名</el-text>
                <el-input v-model="name" placeholder="请输入用户姓名" class="w-30" clearable @keyup.enter="getTableData" />

                <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">查询</el-button>
                <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
            </div>
        </el-card>

        <el-card shadow="never">
            <!-- 分页列表 -->
            <el-table :data="tableData" border stripe v-loading="tableLoading" table-layout="auto">
                <el-table-column prop="id" label="序号" width="60" />
                <el-table-column prop="username" label="用户姓名" width="150" />
                <el-table-column prop="avatarUrl" label="头像" width="100">
                    <template #default="scope">
                        <el-avatar :size="40" :src="scope.row.avatarUrl" />
                    </template>
                </el-table-column>
                <el-table-column prop="role" label="角色" width="120">
                    <template #default="scope">
                        <el-select v-model="scope.row.role" size="small" @change="(val) => handleRoleChange(val, scope.row)">
                            <el-option v-for="role in roleList" :key="role.id" :label="role.desc" :value="role.name" />
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column prop="createDate" label="创建时间" width="180" />
                <el-table-column fixed="right" label="操作" width="150">
                    <template #default="scope">
                        <el-tooltip class="box-item" effect="dark" content="查看详情" placement="bottom">
                            <el-button type="primary" size="small" :icon="View" @click="showDetailDialog(scope.row)" circle>
                            </el-button>
                        </el-tooltip>
                        <el-tooltip class="box-item" effect="dark" content="删除" placement="bottom">
                            <el-button type="danger" size="small" :icon="Delete" @click="deleteUserSubmit(scope.row)"
                                circle>
                            </el-button>
                        </el-tooltip>
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

        <!-- 用户详情对话框 -->
        <el-dialog v-model="detailDialogVisible" title="用户详情" width="500px">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
                <el-descriptions-item label="用户姓名">{{ userDetail.userName }}</el-descriptions-item>
                <el-descriptions-item label="头像">
                    <el-avatar :size="60" :src="userDetail.avatarUrl" />
                </el-descriptions-item>
                <el-descriptions-item label="角色">
                    <el-tag v-if="userDetail.role === 'root'" type="danger">管理员</el-tag>
                    <el-tag v-else type="success">普通用户</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="个人介绍">{{ userDetail.introduction || '暂无' }}</el-descriptions-item>
            </el-descriptions>
        </el-dialog>

    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { userPage, getBlogSettingsDetail, chanRole , deleteUser } from '@/api/admin/user'
import { getAllRoles } from '@/api/admin/role'
import { Search, RefreshRight, Delete, View } from '@element-plus/icons-vue'
import { showMessage, showModel } from '@/composables/util'

// 查询条件：用户姓名
const name = ref('')

// 角色列表
const roleList = ref([])

// 获取所有角色
const getRoleList = () => {
    getAllRoles().then((res) => {
        if (res.success == true) {
            roleList.value = res.data
        }
    })
}
getRoleList()

// 重置
const reset = () => {
    name.value = ''
}

// 表格加载 Loading
const tableLoading = ref(false)
// 表格数据
const tableData = ref([])
// 当前页码，给了一个默认值 1
const current = ref(1)
// 总数据量，给了个默认值 0
const total = ref(0)
// 每页显示的数据量，给了个默认值 10
const size = ref(10)

// 获取分页数据
function getTableData() {
    // 显示表格 loading
    tableLoading.value = true
    // 调用后台分页接口，并传入所需参数
    userPage({
        name: name.value,
        current: current.value,
        size: size.value
    })
        .then((res) => {
            if (res.success == true) {
                tableData.value = res.data
                current.value = res.pageNo
                size.value = res.pageSize
                total.value = res.totalCount
            }
        })
        .finally(() => tableLoading.value = false) // 隐藏表格 loading
}
getTableData()

// 每页展示数量变更事件
const handleSizeChange = (chooseSize) => {
    size.value = chooseSize
    getTableData()
}

// 角色变更处理
const handleRoleChange = (val, row) => {
    // 找到对应的角色ID
    const roleItem = roleList.value.find(r => r.name === val)
    if (roleItem) {
        chanRole(row.id, roleItem.id).then((res) => {
            if (res.success == true) {
                showMessage('角色修改成功')
            } else {
                showMessage(res.message || '角色修改失败', 'error')
                // 恢复原角色
                getTableData()
            }
        })
    }
}

// 删除用户
const deleteUserSubmit = (row) => {
    showModel('是否确定要删除该用户？').then(() => {
        deleteUser(row.id).then((res) => {
            if (res.success == true) {
                showMessage('删除成功')
                getTableData()
            } else {
                let message = res.message
                showMessage(message, 'error')
            }
        })
    }).catch((e) => {
        console.log('取消了')
    })
}

// 用户详情对话框是否展示
const detailDialogVisible = ref(false)
// 用户详情数据
const userDetail = ref({})
// 展示用户详情对话框
const showDetailDialog = (row) => {
    detailDialogVisible.value = true
    // 先保存角色信息（从列表数据获取）
    userDetail.value = { role: row.role }
    // 调用接口获取用户详细信息
    getBlogSettingsDetail(row.id).then((res) => {
        if (res.success == true) {
            userDetail.value = { ...userDetail.value, ...res.data }
        }
    })
}
</script>

