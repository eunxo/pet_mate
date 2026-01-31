<template>
  <div class="home-container" :class="mode.toLowerCase()">
    <section class="hero-section">
      <div class="hero-content">
        <h1 v-if="mode === 'DOG'">함께 걸을 때 더 행복한, <br>댕댕이들의 산책 메이트 🐶</h1>
        <h1 v-else>집사가 부재중일 때도 안심, <br>냥이들을 위한 방문 돌봄 🐱</h1>
        <p>우리 동네 인증을 통해 믿을 수 있는 이웃을 만나보세요.</p>
        
        <div class="mode-switch-wrapper">
          <button @click="changeMode('DOG')" :class="{ active: mode === 'DOG' }">🐶 멍멍이</button>
          <button @click="changeMode('CAT')" :class="{ active: mode === 'CAT' }">🐱 냥이</button>
        </div>
      </div>
      
      <div class="hero-image" :style="{ backgroundImage: `url(${heroImage})` }"></div>
    </section>

    <section class="feed-section">
      <div class="feed-header">
        <h2 v-if="currentUser">🏠 {{ currentUser.address }} 근처 소식</h2>
        <h2 v-else>🐾 우리 동네 반려동물 친구들</h2>
      </div>

      <div class="card-grid" v-if="neighbors.length > 0">
        <div v-for="neighbor in neighbors" :key="neighbor.id" class="pet-card">
          <div class="card-img-wrapper">
             <img :src="neighbor.photoUrl || defaultPetImg" alt="반려동물 사진">
             <span v-if="neighbor.petVerified" class="verified-badge">인증됨</span>
          </div>
          
          <div class="card-body">
            <div class="card-title">
              <h3>{{ neighbor.petName }}</h3>
              <span class="user-nickname">👤 {{ neighbor.nickname }}</span>
            </div>
            <p class="pet-tags">{{ neighbor.petTags }}</p>
            
            <div class="card-footer">
              <span class="location-status" :class="{ verified: neighbor.locationVerified }">
                {{ neighbor.locationVerified ? '📍 위치 인증됨' : '📍 위치 미인증' }}
              </span>
              <button class="more-btn">상세보기</button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-msg">
        주변에 등록된 {{ mode === 'DOG' ? '강아지' : '고양이' }} 친구가 아직 없어요.
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const currentUser = ref(null)
const mode = ref('DOG')
const neighbors = ref([])

// 기본 이미지 설정
const defaultPetImg = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500" // 강아지 예시
const dogHeroImg = "https://images.unsplash.com/photo-1534361960057-19889db9621e?q=80&w=1200"
const catHeroImg = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=1200"

const heroImage = computed(() => mode.value === 'DOG' ? dogHeroImg : catHeroImg)

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
  fetchNeighbors()
}

onMounted(() => {
  const savedUser = localStorage.getItem('user')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
    fetchNeighbors()
  }
})
</script>

<style scoped>
/* 배경색 테마 */
.home-container.dog { --theme-color: #4AAE9B; }
.home-container.cat { --theme-color: #ff99cc; }

/* 히어로 섹션 스타일 */
.hero-section {
  display: flex;
  height: 60vh;
  align-items: center;
  background-color: #f8f9fa;
  overflow: hidden;
}

.hero-content {
  flex: 1;
  padding: 0 10%;
  z-index: 2;
}

.hero-content h1 {
  font-size: 3rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 20px;
}

.hero-image {
  flex: 1.2;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: background-image 0.5s ease-in-out;
}

/* 모드 스위치 */
.mode-switch-wrapper {
  margin-top: 30px;
  background: white;
  display: inline-flex;
  padding: 8px;
  border-radius: 50px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.mode-switch-wrapper button {
  padding: 10px 25px;
  border: none;
  background: none;
  border-radius: 50px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.3s;
}

.mode-switch-wrapper button.active {
  background-color: var(--theme-color);
  color: white;
}

/* 피드 카드 스타일 */
.feed-section {
  padding: 60px 10%;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
}

.pet-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

.pet-card:hover { transform: translateY(-10px); }

.card-img-wrapper {
  height: 200px;
  position: relative;
}

.card-img-wrapper img { width: 100%; height: 100%; object-fit: cover; }

.verified-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: var(--theme-color);
  color: white;
  padding: 5px 12px;
  border-radius: 50px;
  font-size: 0.8rem;
  font-weight: bold;
}

.card-body { padding: 20px; }

.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.pet-tags { color: #888; font-size: 0.9rem; margin-bottom: 20px; }

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.more-btn {
  background: none;
  border: 1px solid var(--theme-color);
  color: var(--theme-color);
  padding: 5px 15px;
  border-radius: 5px;
  cursor: pointer;
}

.more-btn:hover {
  background-color: var(--theme-color);
  color: white;
}
</style>