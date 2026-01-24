<template>
    <!-- 外层容器 -->
    <el-container>

        <!-- 左边侧边栏 -->
        <el-aside :width='menuStore.menuWidth' class="transition-all duration-300">
            <PersonalMenu></PersonalMenu>
        </el-aside>

        <!-- 右边主内容区域 -->
        <el-container>

            <!-- 顶栏容器 -->
            <el-header>
                <PersonalHeader></PersonalHeader>
            </el-header>

            <el-main>
                <!-- 标签导航栏 -->
                <PersonalTagList></PersonalTagList>

                <!-- 主内容（根据路由动态展示不同页面） -->
                <router-view v-slot="{ Component }">
                    <Transition name="fade">
                        <!-- max 指定最多缓存 10 个组件 -->
                        <KeepAlive :max="10">
                            <component :is="Component"></component>
                        </KeepAlive>
                    </Transition>

                </router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
// 引入组件
import PersonalHeader from './components/PersonalHeader.vue';
import PersonalMenu from './components/PersonalMenu.vue';
import PersonalTagList from './components/PersonalTagList.vue';
import { onMounted } from 'vue';

import { useMenuStore } from '@/stores/menu'

const menuStore = useMenuStore()

onMounted(() => {
    // 移除 html 标签中的 class="dark"
    document.documentElement.classList.remove('dark');
})
</script>

<style scoped>
.el-header {
    padding: 0!important;
}

.el-footer {
    padding: 0!important;
}

/* 内容区域过渡动画：淡入淡出效果 */
/* 刚开始进入时 */
.fade-enter-from {
    /* 透明度 */
    opacity: 0;
}

/* 刚开始结束 */
.fade-enter-to {
    opacity: 1;
}

/* 刚开始离开 */
.fade-leave-from {
 opacity: 1;
}

/* 离开已结束 */
.fade-leave-to {
 opacity: 0;
}

/* 离开进行中 */
.fade-leave-active {
    transition: all 0.3s;
}

/* 进入进行中 */
.fade-enter-active {
    transition: all 0.3s;
    transition-delay: 0.3s;
}
</style>