<template>
  <div class="register-container">
    <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        status-icon
        :rules="rules"
        label-width="100px"
        class="demo-ruleForm"
    >
      <h1>企业注册</h1>
      <el-form-item label="企业名称" prop="name">
        <el-input v-model="ruleForm.name" />
      </el-form-item>

      <el-form-item label="联系方式" prop="contactInfo">
        <el-input v-model="ruleForm.contactInfo" />
      </el-form-item>

      <el-form-item label="密码" prop="pass">
        <el-input v-model="ruleForm.pass" type="password" autocomplete="off" />
      </el-form-item>

      <el-form-item label="密码确认" prop="checkPass">
        <el-input
            v-model="ruleForm.checkPass"
            type="password"
            autocomplete="off"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)">注册</el-button>
        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </el-form-item>

      <div class="options">
        <span @click="backLogin">返回登录</span>
      </div>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import axios from 'axios';
import router from '@/router';
import type { FormInstance, FormRules } from 'element-plus';

const ruleFormRef = ref<FormInstance>();

const validatePass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else {
    if (ruleForm.checkPass !== '') {
      if (!ruleFormRef.value) return;
      ruleFormRef.value.validateField('checkPass');
    }
    callback();
  }
};

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== ruleForm.pass) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const ruleForm = reactive({
  name: '',
  contactInfo: '',
  pass: '',
  checkPass: '',
});

const rules = reactive<FormRules>({
  pass: [{ validator: validatePass, trigger: 'blur' }],
  checkPass: [{ validator: validatePass2, trigger: 'blur' }],
});

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.validate(async (valid) => {
    if (valid) {
      try {
        const response = await axios.post('http://localhost:5000/register', {
          userName: ruleForm.name,
          phone: ruleForm.contactInfo,
          password: ruleForm.pass,
        });
        alert(response.data);
        router.push('/');
      } catch (error) {
        alert('注册失败');
      }
    } else {
      console.log('error submit!');
    }
  });
};

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.resetFields();
};

const backLogin = () => {
  router.push('/');
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/background.png');
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
}

.demo-ruleForm {
  background: rgba(255, 255, 255, 0.9);
  padding: 60px 80px;
  border-radius: 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  text-align: center;
  width: 600px; /* 调整表单容器宽度 */
  font-size: 22px; /* 调整整体字体大小 */
}

h1 {
  margin-bottom: 30px;
  font-size: 36px; /* 调整标题字体大小 */
  color: #333;
}

::v-deep .el-input__inner {
  font-size: 22px; /* 调整输入框字体大小 */
  padding: 15px; /* 调整输入框内边距 */
}

::v-deep .el-button {
  font-size: 20px; /* 调整按钮字体大小 */
  padding: 12px 20px; /* 调整按钮内边距 */
}

::v-deep .el-form-item__label {
  font-size: 22px; /* 调整表单标签字体大小 */
}

.options {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  font-size: 20px; /* 调整选项字体大小 */
}

.options span {
  color: #007bff;
  cursor: pointer;
}

.options span:hover {
  text-decoration: underline;
}
</style>
