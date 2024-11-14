<template>
  <span v-for="bubble in bubbles" :style="bubble" class="bubble"></span>
  <div class="background">
    <div class="login-box">
      <h1>软件造价评估平台</h1>
      <form @submit.prevent="submitForm">
        <div class="input-container">
          <input type="text" v-model="formData.userName" placeholder="用户名" required />
          <input type="password" v-model="formData.password" placeholder="密码" required />
        </div>
        <div class="error-message" v-if="errorMessage">{{ errorMessage }}</div>
        <button type="submit">登录</button>
        <div class="options">
          <span @click="openRegister">没有账号？去注册</span>
          <span @click="forgotPassword">忘记密码？</span>
        </div>
      </form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import router from '@/router';
import {useStore} from "vuex";
const store = useStore();

interface FormData {
  userName: string;
  password: string;
}

const formData = ref<FormData>({
  userName: '',
  password: ''
});

const errorMessage = ref<string>('');

const submitForm = async () => {
  try {
    const loginResponse = await axios.post('http://localhost:5000/login', formData.value, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log(loginResponse.data);
    if (loginResponse.data.success) {
      const userInfoResponse = await getUserInfo(formData.value.userName); // 获取用户信息
      router.push({ path: '/home', query: { user: JSON.stringify(userInfoResponse.data) } }); // 将用户信息传递到HomePage
      console.log(userInfoResponse.data.userName)
      console.log(userInfoResponse.data.role)
      const userInfo = {
        userName: userInfoResponse.data.userName,
        role: userInfoResponse.data.role
      };
      store.dispatch('loginUser', userInfo);
    } else {
      errorMessage.value = loginResponse.data.message;
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = '登录失败，请检查用户名和密码';
  }
};

const getUserInfo = (userName: string) => {
  console.log("getUserInfo");
  return axios.get(`http://localhost:5000/user/${userName}`);
};

const openRegister = () => {
  router.push('/register');
};

const forgotPassword = () => {
  // 处理忘记密码逻辑
};
interface Bubble {
  width: string;
  height: string;
  left: string;
}
const createBubble = (): Bubble => {
  let r = Math.random() * 5 + 25;
  return {
    width: `${r}px`,
    height: `${r}px`,
    left: `${Math.random() * window.innerWidth}px`
  };
};
const bubbles = ref<Bubble[]>([]);

onMounted(() => {
  setInterval(() => {
    bubbles.value.push(createBubble());
  }, 300);

  setTimeout(() => {
    setInterval(() => {
      bubbles.value.pop();
    }, 2000);
  }, 1000);
});

</script>

<style scoped lang="less">
html, body {
  height: 100%;
  margin: 0;
}

.background {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/background.png'); /* 请确保背景图片路径正确 */
  background-size: cover;
  background-position: center;
}

.login-box {
  background: rgba(255, 255, 255, 0.9);
  padding: 40px; /* 增加内边距 */
  border-radius: 12px; /* 调整圆角 */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2); /* 增加阴影 */
  width: 500px; /* 增加宽度 */
  text-align: center;
}

.login-box h1 {
  margin-bottom: 30px;
  font-size: 32px; /* 调整标题字体大小 */
  color: #333;
}

.input-container {
  display: flex;
  flex-direction: column;
}

.input-container input {
  margin-bottom: 20px;
  padding: 15px; /* 增加内边距 */
  border: 1px solid #ccc;
  border-radius: 6px; /* 调整圆角 */
  font-size: 18px; /* 调整输入框字体大小 */
}

.input-container input:focus {
  border-color: #007bff;
}

.error-message {
  color: red;
  margin-bottom: 20px;
  font-size: 18px; /* 调整错误消息字体大小 */
}

button {
  background-color: #007bff;
  color: white;
  padding: 15px; /* 增加内边距 */
  border: none;
  border-radius: 6px; /* 调整圆角 */
  cursor: pointer;
  width: 100%;
  font-size: 20px; /* 调整按钮字体大小 */
}

button:hover {
  background-color: #0056b3;
}

.options {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  font-size: 18px; /* 调整选项字体大小 */
}

.options span {
  color: #007bff;
  cursor: pointer;
}

.options span:hover {
  text-decoration: underline;
}

.bubble {
  position: absolute;
  bottom: 0;
  background: radial-gradient(circle at 72% 28%, #fff 5px, #ff7edf 8%, #5b5b5b, #aad7f9 100%);
  box-shadow: inset 0 0 6px #fff, inset 3px 0 6px #eaf5fc, inset 2px -2px 10px #efcde6, inset 0 0 60px #f9f6de,
  0 0 20px #fff;
  border-radius: 50%;
  z-index: 998;
  animation: myMove 4s linear infinite;
}

@keyframes myMove {
  0% {
    transform: translateY(0%);
    opacity: 1;
  }

  50% {
    transform: translate(10%, -1000%);
  }

  75% {
    transform: translate(-20%, -1200%);
  }

  99% {
    opacity: 0.9;
  }

  100% {
    transform: translateY(-1000%) scale(1.5);
    opacity: 0;
  }
}

</style>
