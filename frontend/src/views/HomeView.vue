<template>
  <div class="home-container">
    <div v-if="currentUser">
      <h1>
        🏠 {{ currentUser.address }} 근처 소식
        <span v-if="currentUser.petVerified" class="verify-badge">✅</span>
      </h1>
      
      <div class="mode-switch">
        <button @click="changeMode('DOG')" :class="{active: mode === 'DOG'}">🐶 멍멍이</button>
        <button @click="changeMode('CAT')" :class="{active: mode === 'CAT'}">🐱 냥이</button>
      </div>

      <div class="feed-list">
        <div v-if="neighbors.length > 0" class="card-grid">
          <div v-for="neighbor in neighbors" :key="neighbor.id" class="pet-card">
            <div class="card-header">
              <span class="pet-name">{{ neighbor.petName }}</span>
              <span v-if="neighbor.petVerified" class="badge">인증됨</span>
            </div>
            <p class="pet-tags">{{ neighbor.petTags }}</p>
            <div class="card-footer">
              <span>👤 {{ neighbor.nickname }}</span>
              <span class="location-status" :class="{verified: neighbor.locationVerified}">
                {{ neighbor.locationVerified ? '📍 인증됨' : '📍 미인증' }}
              </span>
            </div>
          </div>
        </div>
        <div v-else class="empty-msg">
          아직 주변에 등록된 {{ mode === 'DOG' ? '강아지' : '고양이' }} 친구가 없어요.
        </div>
      </div>
    </div>
    
    <div v-else class="welcome-box">
      <h1>반려인들의 소중한 모임, Pet Mate 🐾</h1>
      <p>동네 인증을 하고 주변 이웃들을 만나보세요.</p>
      <router-link to="/login"><button class="login-btn">시작하기</button></router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const currentUser = ref(null)
const mode = ref('DOG')
const neighbors = ref([])

const fetchNeighbors = async () => {
  if (!currentUser.value) return
  try {
    const response = await axios.get('http://localhost:8080/api/users/neighbor-list', {
      params: {
        address: currentUser.value.address,
        petType: mode.value
      }
    })
    neighbors.value = response.data
  } catch (error) {
    console.error("이웃 목록 로드 실패:", error)
  }
}

const changeMode = (newMode) => {
  mode.value = newMode
  fetchNeighbors() // 모드 변경 시 데이터 다시 불러오기
}

onMounted(() => {
  const savedUser = localStorage.getItem('user')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
    fetchNeighbors() // 초기 로드 시 데이터 불러오기
  }
})
</script>

<style scoped>
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  width: 100%;
}
.pet-card {
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 15px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.pet-name { font-size: 1.2rem; font-weight: bold; }
.badge { background: #4AAE9B; color: white; padding: 2px 8px; border-radius: 10px; font-size: 0.8rem; }
.pet-tags { color: #666; margin-bottom: 15px; font-size: 0.9rem; }
.card-footer { border-top: 1px solid #eee; padding-top: 10px; display: flex; justify-content: space-between; font-size: 0.85rem; }
.location-status.verified { color: #4AAE9B; font-weight: bold; }
/* 기존 스타일 유지... */
</style>