<template>
  <div class="home-container">
    <div v-if="currentUser">
      <h1>🏠 {{ currentUser.address }} 근처 소식</h1>
      <p>{{ currentUser.nickname }}님의 동네 친구들을 찾아보세요!</p>
      
      <div class="mode-switch">
        <button @click="mode = 'DOG'" :class="{active: mode === 'DOG'}">🐶 멍멍이</button>
        <button @click="mode = 'CAT'" :class="{active: mode === 'CAT'}">🐱 냥이</button>
      </div>

      <div class="feed-list">
        <div class="empty-msg">아직 우리 동네에 등록된 친구가 없어요.</div>
      </div>
    </div>
    
    <div v-else>
      <h1>반려인들의 소중한 모임, Pet Mate 🐾</h1>
      <p>동네 인증을 하고 주변 이웃들을 만나보세요.</p>
      <router-link to="/login"><button class="login-btn">시작하기</button></router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const currentUser = ref(null)
const mode = ref('DOG')

onMounted(() => {
  const savedUser = localStorage.getItem('user')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
  }
})
</script>

<style scoped>
.home-container { padding: 20px; }
.mode-switch { margin: 20px 0; display: flex; justify-content: center; gap: 10px; }
.mode-switch button { padding: 10px 20px; border-radius: 20px; border: 1px solid #ddd; cursor: pointer; }
.mode-switch button.active { background: #4AAE9B; color: white; border-color: #4AAE9B; }
.feed-list { margin-top: 30px; min-height: 200px; border: 1px dashed #ccc; display: flex; align-items: center; justify-content: center; }
</style>