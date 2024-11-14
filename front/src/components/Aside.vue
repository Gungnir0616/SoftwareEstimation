<template>
  <div>
    <el-radio-group v-model="isCollapse" style="margin-bottom: 20px">
      <el-radio-button :value="false">expand</el-radio-button>
      <el-radio-button :value="true">collapse</el-radio-button>
    </el-radio-group>
    <el-menu
        default-active="2"
        class="el-menu-vertical-demo"
        :collapse="isCollapse"
        @select="handleMenuSelect"
    >
      <el-sub-menu index="1">
        <template #title>
          <el-icon><location /></el-icon>
          <span>租户和项目管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="1-1">租户管理</el-menu-item>
          <el-menu-item index="1-2">项目管理</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>

      <el-menu-item index="2">
        <el-icon><Document /></el-icon>
        <template #title>功能点分析</template>
      </el-menu-item>

      <el-menu-item index="3">
        <el-icon><setting /></el-icon>
        <template #title>造价综合评估</template>
      </el-menu-item>

      <el-menu-item index="4">
        <el-icon><Calendar /></el-icon>
        <template #title>报告生成与展示</template>
      </el-menu-item>

    </el-menu>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
  Calendar,
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import {computed} from "vue";
import {useStore} from "vuex";

const props = defineProps({
  userInfo: Object
})
const isCollapse = ref(true)
const role = computed(() => props.userInfo.role)
const store = useStore();
const emit = defineEmits(['menu-select'])
const router = useRouter()
const userInfo = computed(() => store.state.user);
const isSuperAdmin = computed(() => store.getters.isSuperAdmin);
const handleMenuSelect = (index) => {
  const selectedItem = {
    title: '',
    path: [],
    breadcrumb:[]
  }
  switch (index) {
    case '1-1':
      selectedItem.title = '租户管理'
      selectedItem.path = [{ name: '租户管理', path: '/tenant-management' }]
      selectedItem.breadcrumb = ['租户和项目管理', '租户管理']
      break
    case '1-2':
      selectedItem.title = '项目管理'
      selectedItem.path = [{ name: '项目管理', path: '/project-management' }]
      selectedItem.breadcrumb = ['租户和项目管理', '项目管理']
      break
    case '2':
      selectedItem.title = '功能点分析'
      selectedItem.path = [{ name: '功能点分析', path: '/function-management' }]
      selectedItem.breadcrumb = ['功能点分析']
      break
    case '3':
      selectedItem.title = '造价综合评估'
      selectedItem.path = [{ name: '造价综合评估', path: '/composite-management' }]
      selectedItem.breadcrumb = ['造价综合评估']
      break
    case '4':
      selectedItem.title = '报告生成与展示'
      selectedItem.path = [{ name: '报告生成与展示', path: '/report-management' }]
      selectedItem.breadcrumb = ['报告生成与展示']
      break

  }
  emit('menu-select', selectedItem)
  router.push(selectedItem.path[0].path)
}
</script>

<style>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>
