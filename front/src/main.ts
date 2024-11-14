import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import $ from 'jquery'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'
import axios from 'axios'




const app = createApp(App);
app.config.globalProperties.$ = $; // 将 jQuery 添加到全局属性
app.use(ElementPlus);
app.use(router).use(store).mount('#app');
app.config.globalProperties.$axios=axios;

