import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import UserLogin from "@/views/UserLogin.vue";
import UserRegister from "@/views/UserRegister.vue";
import Aside from "@/components/Aside.vue";
import HomePage from "@/views/HomePage.vue";
import MyHeader from "@/components/MyHeader.vue";
import CompositeManagement from "@/views/CompositeManagement.vue"

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'UserLogin',
    component: UserLogin
  },
  {
    path:'/register', // 要路由到的url路径
    name:'UserRegister',
    component: UserRegister
  },
  {
    path: '/home',
    name: 'HomePage',
    components: {
      default: HomePage,
      Aside: Aside,
      myHeader: MyHeader,
    },
  },
  {
    path: '/composite-management',
    name: 'CompositeManagement',
    components: {
      default: CompositeManagement,
      Aside: Aside,
      myHeader: MyHeader,
    },
  }

]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
