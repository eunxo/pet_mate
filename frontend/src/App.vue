<template>
  <header>
    <nav class="navbar">
      <div class="logo">
        <router-link to="/">🐾 Pet Mate</router-link>
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

// 로그인 상태 체크 함수
const checkLoginStatus = () => {
  isLoggedIn.value = !!localStorage.getItem('user')
}

// 페이지가 로드될 때와 경로가 바뀔 때마다 로그인 상태 확인
onMounted(checkLoginStatus)
watch(() => route.path, checkLoginStatus)

// 로그아웃 처리 함수
const handleLogout = () => {
  if (confirm("로그아웃 하시겠습니까?")) {
    localStorage.removeItem('user') // 저장된 유저 정보 삭제
    isLoggedIn.value = false
    alert("로그아웃 되었습니다.")
    router.push('/login') // 로그인 페이지로 이동
  }
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 50px;
  background-color: white;
  border-bottom: 1px solid #eee;
}
.logo a {
  font-size: 24px;
  font-weight: bold;
  color: #4AAE9B;
  text-decoration: none;
}
.nav-links {
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
  color: #333 !important; /* 로그아웃은 빨간색으로 강조 */
}
</style>