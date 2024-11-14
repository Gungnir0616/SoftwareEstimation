<template>
  <el-page-header @back="onBack">
    <template #breadcrumb>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/home' }">
          homepage
        </el-breadcrumb-item>
        <el-breadcrumb-item v-for="(route, index) in breadcrumb" :key="index">
          <span v-if="index < breadcrumb.length - 1">{{ route }}</span>
          <span v-else>{{ route }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </template>
    <template #content>
      <div class="flex items-center">
        <el-avatar
            class="mr-3"
            :size="32"
            src="/user2.png"
        />
        <span class="text-large font-600 mr-3">{{ title }}</span>
      </div>
    </template>
  </el-page-header>
</template>

<script setup>
import { defineProps } from 'vue'
import { ElNotification as notify } from 'element-plus'
import router from "@/router";
const onBack = () => {
  router.push('/')
}
const props = defineProps({
  breadcrumb: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: 'Welcome!'
  }
})
</script>

<style scoped>
.flex {
  display: flex;
  align-items: center;
}
.text-large {
  font-size: 20px;
}
.font-600 {
  font-weight: 600;
}
</style>
