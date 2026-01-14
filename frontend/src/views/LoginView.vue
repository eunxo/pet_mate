<template>
  <div class="login-container">
    <h2>반려인 커뮤니티 로그인 🐾</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>이메일</label>
        <input type="email" v-model="loginData.email" placeholder="example@email.com" required>
      </div>
      <div class="form-group">
        <label>비밀번호</label>
        <input type="password" v-model="loginData.password" required>
      </div>
      <button type="submit" class="login-btn">로그인</button>
    </form>
    <p class="link">계정이 없으신가요? <router-link to="/signup">회원가입</router-link></p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginData = ref({ email: '', password: '' })

const handleLogin = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/users/login', loginData.value)
    
    // 로그인 성공 시 유저 정보(이름, 동네 등)를 로컬 스토리지에 저장
    localStorage.setItem('user', JSON.stringify(response.data))
    
    alert(`${response.data.nickname}님, 환영합니다!`)
    router.push('/') // 메인 페이지로 이동
  } catch (error) {
    alert(error.response?.data || '로그인에 실패했습니다.')
  }
}
</script>

<style scoped>
.login-container { max-width: 400px; margin: 100px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background: white; }
.form-group { margin-bottom: 15px; text-align: left; }
.form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.form-group input { width: 100%; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 5px; }
.login-btn { width: 100%; padding: 12px; background-color: #4AAE9B; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
.link { margin-top: 15px; font-size: 14px; }
</style>