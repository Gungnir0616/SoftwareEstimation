<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <MyHeader :breadcrumb="breadcrumb" :title="title"></MyHeader>
      </el-header>
      <el-divider />
      <el-container>
        <el-aside width="200px">
          <Aside @menu-select="handleMenuSelect" :user-info="userInfo" />
        </el-aside>
        <el-main>
          <el-card >
            <template #header>
              <div class="card-header">
                <span class="header-title">基本资料</span>
              </div>
            </template>

            <el-tabs type="border-card">
              <el-tab-pane label="基本资料">
                <el-form :model="form" :rules="rules" ref="formRef">
                  <el-form-item class="must" label="用户昵称">
                    <el-input v-model="form.userName" :readonly="!isEditing" />
                  </el-form-item>
                  <el-form-item class="must" label="手机号码">
                    <el-input v-model="form.phone" :readonly="!isEditing"/>
                  </el-form-item>
                  <el-form-item class="must" label=邮箱>
                    <el-input v-model="form.mailbox" :readonly="!isEditing" />
                  </el-form-item>
                  <el-form-item label=部门>
                    <el-input v-model="form.department" :readonly="!isEditing" />
                  </el-form-item>
                  <el-form-item label=角色>
                    <el-input v-model="form.role" :readonly="!isEditing" />
                  </el-form-item>
                  <el-form-item class="must" label="性别">
                    <div class="my-2 flex items-center text-sm">
                      <el-radio-group v-model="form.gender" class="ml-4" :disabled="!isEditing">
                        <el-radio :label="false">男</el-radio>
                        <el-radio :label="true">女</el-radio>
                      </el-radio-group>
                    </div>
                  </el-form-item>

                  <el-form-item>
                    <el-button type="primary" @click="editForm" v-if="!isEditing">编辑</el-button>
                    <el-button type="success" @click="saveForm" v-else>保存</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              <el-tab-pane label="修改密码">
                <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
                  <el-form-item label="旧密码" prop="oldPassword">
                    <el-input type="password" v-model="passwordForm.oldPassword" autocomplete="off"/>
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input type="password" v-model="passwordForm.newPassword" autocomplete="off"/>
                  </el-form-item>
                  <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input type="password" v-model="passwordForm.confirmPassword" autocomplete="off"/>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="changePassword">修改密码</el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
            </el-tabs>

          </el-card>
        </el-main>
      </el-container>
    </el-container>
    <div class="survey-container">
    <div class="survey-icon" @mouseover="showSurvey = true" @mouseleave="showSurvey = false">
      <img src="https://g.csdnimg.cn/side-toolbar/3.3/images/nps.png" alt="调研" />
      <el-card v-if="showSurvey" class="survey-card">
        <h3 class="nps-title">你会推荐软件造价评估平台么？</h3>
        <div class="nps-content">
          <ul class="newnps-list">
            <li class="newnps-item" v-for="(item, index) in surveyOptions" :key="index" @click="selectOption(item)">
              <div class="newnps-img-box">
                <img :src="item.activeImg" alt="" class="newnps-img active" />
                <img :src="item.defaultImg" alt="" class="newnps-img default" />
              </div>
              <div class="newnps-text">{{ item.text }}</div>
            </li>
          </ul>
          <div class="newnps-form-box">
            <div class="newnps-form">
              <input type="text" placeholder="请说明理由，帮助我们改进" class="newnps-input" v-model="feedback" />
              <span class="newnps-btn" @click="submitFeedback">提交</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Aside from '@/components/Aside.vue'
import MyHeader from '@/components/MyHeader.vue'
import axios from "axios";
import router from "@/router";

const route = useRoute()
const userInfo = JSON.parse(route.query.user || '{}');
const title = ref(`${userInfo.userName}，welcome`);
const isEditing = ref(false);

const form = reactive({
  userName: userInfo.userName || '',
  phone: userInfo.phone || '',
  mailbox: userInfo.mailbox || '',
  gender: userInfo.gender !== undefined ? userInfo.gender : false,
  department: userInfo.department || '',
  role: userInfo.role || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const breadcrumb = ref([])

const radio2 = ref('1')
const passwordFormRef = ref(null)
const formRef = ref(null)
const rules = {
  userName: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  phone: [{ required: true, message: '手机号不能为空', trigger: 'blur' }],
  mailbox: [{ required: true, message: '邮箱不能为空', trigger: 'blur' }],
}
const passwordRules = {
  oldPassword: [{ required: true, message: '旧密码不能为空', trigger: 'blur' }],
  newPassword: [{ required: true, message: '新密码不能为空', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '确认新密码不能为空', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'));
        } else {
          callback();
        }
      }, trigger: 'blur' }]
}
const editForm = () => {
  isEditing.value = true;
}

const saveForm = async () => {
  try {
    await formRef.value.validate()
    console.log(form)
    const response = await updateUserInfo(userInfo.userName, form)
    console.log(response.data)
    alert(response.data)
    console.log(userInfo.userName)
    getUserInfo(userInfo.userName)
    isEditing.value = false;
  } catch (error) {
    console.error('Error saving user info:', error)
  }
}
const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    await updatePassword(userInfo.userName, passwordForm.oldPassword, passwordForm.newPassword)
    alert('密码修改成功')
    // 重置表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('Error changing password:', error)
  }
}
const updateUserInfo = (userName, data) => {
  return axios.put(`http://localhost:5000/user/${userName}`, data);
};
const getUserInfo = async(userName) => {
  try {
    const response = await axios.get(`http://localhost:5000/user/${userName}`, userName)
    console.log(response.data)
    form.userName = response.data.userName
    form.role = response.data.role
    form.department = response.data.department
    form.mailbox = response.data.mailbox
    form.gender = response.data.gender
    form.phone = response.data.phone
    Object.assign(form, response)
    console.log(form)
  } catch (error) {
    console.error('Error fetching courses:', error)
  }
}
const updatePassword = (userName, oldPassword, newPassword) => {
  return axios.post(`http://localhost:5000/user/${userName}/change-password`, {
    oldPassword,
    newPassword
  });
}

const handleMenuSelect = (selectedItem) => {
  breadcrumb.value = selectedItem.breadcrumb
  title.value = selectedItem.title
}

const showSurvey = ref(false);
const feedback = ref('');
const surveyOptions = [
  { text: '强烈不推荐', activeImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeel1.png', defaultImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeelGrey1.png' },
  { text: '不推荐', activeImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeel2.png', defaultImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeelGrey2.png' },
  { text: '一般般', activeImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeel3.png', defaultImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeelGrey3.png' },
  { text: '推荐', activeImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeel4.png', defaultImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeelGrey4.png' },
  { text: '强烈推荐', activeImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeel5.png', defaultImg: '//g.csdnimg.cn/csdn-nps/1.1.1/images/npsFeelGrey5.png' }
];

const selectOption = (item) => {
  console.log(`Selected option: ${item.text}`);
};

const submitFeedback = () => {
  console.log(`Submitted feedback: ${feedback.value}`);
  alert('反馈已提交，谢谢您的参与！');
};

</script>

<style>
.card-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.header-title {
  margin-left: 0; /* 确保文字紧贴左边 */
}
.must .el-form-item__label:before {
  content: "*";
  color: red;
  margin-right: 4px;
  display: inline-block;
}

.home-page {
  display: flex;
}

.survey-container {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 1000;
}

.survey-icon {
  position: relative;
  width: 40px;
  height: 40px;
  cursor: pointer;
}

.survey-icon img {
  width: 100%;
  height: 100%;
}

.survey-card {
  position: absolute;
  top: -200px; /* 调整这个值以控制调研卡片显示位置 */
  right: 0;
  width: 350px;
  z-index: 10;
  background: #fff;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  border-radius: 4px;
}

.nps-title {
  font-size: 16px;
  margin-bottom: 10px;
}

.nps-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.newnps-list {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 10px;
}

.newnps-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.newnps-img-box {
  position: relative;
  width: 30px;
  height: 30px;
}

.newnps-img {
  width: 100%;
  height: 100%;
}

.newnps-text {
  font-size: 12px;
  margin-top: 5px;
}

.newnps-form-box {
  width: 100%;
}

.newnps-form {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.newnps-input {
  flex: 1;
  padding: 5px;
  font-size: 14px;
}

.newnps-btn {
  padding: 5px 10px;
  background-color: #409EFF;
  color: #fff;
  border-radius: 3px;
  cursor: pointer;
  margin-left: 10px;
}
</style>