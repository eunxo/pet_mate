<template>
  <header>
    <nav class="navbar">
      <div class="logo-container">
        <router-link to="/" class="logo-text">멍냥모아</router-link>
      </div>

      <div class="nav-links">
        <router-link to="/">홈</router-link>
        
        <template v-if="!isLoggedIn">
          <router-link to="/login">로그인</router-link>
          <router-link to="/signup">회원가입</router-link>
        </template>
        
        <template v-else>
          <router-link to="/mypage">마이페이지</router-link>
          <a @click="handleLogout" class="logout-link">로그아웃</a>
        </template>
      </div>
    </nav>
  </header>

  <router-view />
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isLoggedIn = ref(false)

const checkLoginStatus = () => {
  isLoggedIn.value = !!localStorage.getItem('user')
}

onMounted(checkLoginStatus)
watch(() => route.path, checkLoginStatus)

const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    localStorage.removeItem('user')
    isLoggedIn.value = false
    alert("로그아웃 되었습니다.")
    router.push('/login')
  }
}
</script>

<style scoped>
.navbar {
  display: grid;
  /* 2. 중앙 정렬을 위해 좌(1fr) - 중(auto) - 우(1fr) 구조 유지 */
  grid-template-columns: 1fr auto 1fr; 
  align-items: center;
  padding: 15px 50px;
  background-color: white;
  border-bottom: 1px solid #eee;
}

/* 3. 로고 컨테이너가 중앙 열(2번째 열)에 오도록 설정 */
.logo-container {
  grid-column: 2;
  display: flex;
  justify-content: center;
  align-items: center;
}

.logo-text {
  color: #629f62; 
  font-family: 'Arial Round', 'Nanum Gothic', sans-serif;
  font-weight: 900;
  font-size: 28px;
  letter-spacing: -0.5px;
  text-decoration: none;
  display: inline-block;
  transform: scaleY(1.1);
}

.logo-text:hover {
  color: #4e824e;
}

.nav-links {
  grid-column: 3;
  justify-self: end;
  display: flex;
  gap: 20px;
}

.nav-links a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  cursor: pointer;
}

.nav-links a:hover {
  color: #4AAE9B;
}

.logout-link {
  color: #333 !important;
}
</style>