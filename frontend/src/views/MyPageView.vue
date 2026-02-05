<template>
  <div class="mypage-wrapper">
    <div class="mypage-container" style="display: flex; flex-direction: column; gap: 25px; width: 100%; max-width: 900px;">
      
      <div class="profile-card card-shadow">
        <h2 class="card-title">Profile card</h2>
        <div class="profile-main-box">
          <div class="user-info-row">
            <div class="avatar-circle">
              <img v-if="currentUser?.photoUrl" :src="`http://localhost:8080${currentUser.photoUrl}`" alt="profile" />
              <div v-else class="default-avatar-icon">👤</div>
            </div>
            <div class="user-text-details">
              <div style="display: flex; align-items: center; gap: 15px;">
                <h3 style="margin: 0;">{{ currentUser?.nickname || '사용자' }}님, 반가워요!</h3>
                <div class="manner-tag-simple">
                  <span style="font-size: 0.8rem; color: #666;">매너 온도</span>
                  <span style="color: #629f62; font-weight: bold; margin-left: 5px;">{{ currentUser?.mannerTemperature || 36.5 }}°C</span>
                </div>
              </div>
              <span class="verify-badge" style="display: inline-block; margin-top: 8px;">● {{ currentUser?.isLocationVerified ? '지역 인증 완료' : '지역 인증 필요' }}</span>
            </div>
          </div>
          <div class="user-address-box">
            
            <div class="address-details">
              <p class="label">user details</p>
              <p class="val">address: {{ currentUser?.address || '주소 정보가 없습니다.' }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="pets-grid-card card-shadow">
        <div class="section-header-row">
          <h3 class="section-title">🐾 내 반려동물 ({{ myPets.length }})</h3>
          <button @click="showModal = true" class="reg-btn-green">+ 반려동물 등록하기</button>
        </div>
        <div class="pet-cards-grid" v-if="myPets.length > 0">
          <div v-for="pet in myPets" :key="pet.id" class="pet-item-box">
            <div class="pet-img-placeholder">
              <img v-if="pet.photoUrl" :src="`http://localhost:8080${pet.photoUrl}`" alt="pet" />
              <div v-else class="default-pet-avatar">🐾</div>
            </div>
            <div class="pet-simple-info">
              <span class="type-tag" :class="pet.type">{{ pet.type }}</span>
              <p class="name">{{ pet.name }}</p>
              <p class="tags">{{ pet.tags }}</p>
            </div>
          </div>
        </div>
        <div v-else class="empty-msg-box"><p>등록된 아이가 없습니다.</p></div>
      </div>

      <div class="activity-card card-shadow">
        <h3 class="card-title">Activity history</h3>
        <div class="activity-tabs">
          <button class="act-tab active">전체 목록</button>
          <button class="act-tab">산책 대기</button>
          <button class="act-tab">완료</button>
        </div>
        <div class="history-content-area">
          <div class="history-placeholder"><p>활동 내역이 표시됩니다.</p></div>
        </div>
      </div>

    </div>
    </div>
</template><script setup>

import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const showModal = ref(false)
const currentUser = ref(null)
const myPets = ref([])
const petData = ref({ type: 'DOG', name: '', registrationNo: '', tags: '', description: '' })

// 1. 유저 정보를 가져오는 함수를 독립적으로 분리하여 관리합니다.
const loadData = async () => {
  const storedUser = localStorage.getItem('user');
  
  if (!storedUser || storedUser === "undefined") {
    router.push('/login');
    return;
  }

  try {
    const user = JSON.parse(storedUser);
    
    // API 호출 시 params를 명시적으로 사용하여 쿼리 스트링 에러를 방지합니다.
    const [userRes, petRes] = await Promise.all([
      axios.get(`http://localhost:8080/api/users/${user.id}`),
      axios.get(`http://localhost:8080/api/pets/list`, { params: { userId: user.id } })
    ]);
    
    currentUser.value = userRes.data;
    myPets.value = petRes.data;
    
    // 로컬 스토리지의 유저 정보도 최신화합니다.
    localStorage.setItem('user', JSON.stringify(userRes.data));
  } catch (e) {
    console.error("데이터 로드 실패", e);
  }
}

const selectedFile = ref(null);
const imagePreview = ref(null);

// 파일 선택 시 실행되는 함수
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
  }
};

// 2. 반려동물 등록 로직: 백엔드 DB 외래 키 설정을 위해 userId 전송을 강화합니다.
const submitPetInfo = async () => {
  if (!petData.value.name) {
    alert("이름을 입력해주세요.");
    return;
  }

  // 1) localStorage에서 최신 유저 정보를 가져와 ID를 확보합니다.
  const storedUser = JSON.parse(localStorage.getItem('user'));
  // currentUser가 null이더라도 storedUser에서 ID를 찾아 에러를 방지합니다.
  const currentUserId = currentUser.value?.id || storedUser?.id;

  if (!currentUserId) {
    alert("사용자 정보를 확인할 수 없습니다. 다시 로그인해주세요.");
    router.push('/login');
    return;
  }
// 3. 파일 포함을 위한 FormData 구성
  const formData = new FormData();
  formData.append('userId', currentUserId);
  formData.append('type', petData.value.type);
  formData.append('name', petData.value.name);
  formData.append('registrationNo', petData.value.registrationNo || '');
  formData.append('tags', petData.value.tags || '');
  formData.append('description', petData.value.description || '');
  
  if (selectedFile.value) {
    formData.append('file', selectedFile.value); // 사진 파일 추가
  }

  try {
    // 4. 멀티파트 헤더와 함께 전송
    await axios.post('http://localhost:8080/api/pets/register', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    alert("사진과 함께 등록되었습니다!");
    showModal.value = false;
    
    // 5. 상태 초기화 및 목록 갱신
    imagePreview.value = null;
    selectedFile.value = null;
    petData.value = { type: 'DOG', name: '', registrationNo: '', tags: '', description: '' };
    await loadData(); 
  } catch (e) {
    console.error("등록 에러 상세:", e.response?.data);
    alert("등록 실패: " + (e.response?.data || "서버 에러"));
  }
}
onMounted(loadData)
</script>

<style scoped>
.mypage-wrapper {
  background-color: #e9f0eb;
  min-height: 100vh;
  padding: 40px;
  display: flex;
  justify-content: center;
}

.mypage-container {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  grid-template-rows: auto auto;
  gap: 25px;
  width: 100%;
  max-width: 1200px;
}

.card-shadow {
  background: white;
  border-radius: 30px;
  padding: 30px;
  box-shadow: 0 10px 20px rgba(0,0,0,0.02);
}

.card-title { font-size: 1.8rem; font-weight: 800; margin-bottom: 25px; color: #333; }

/* Profile Card */
.profile-main-box {
  background: #f8fcf9;
  border-radius: 20px;
  padding: 20px;
}
.user-info-row { display: flex; align-items: center; gap: 20px; margin-bottom: 25px; }
.avatar-circle { width: 80px; height: 80px; border-radius: 50%; border: 3px solid #629f62; overflow: hidden; }
.avatar-circle img { width: 100%; height: 100%; object-fit: cover; }
.verify-badge { background: #629f62; color: white; padding: 4px 12px; border-radius: 20px; font-size: 0.8rem; }

.user-address-box { display: flex; align-items: center; gap: 15px; border-top: 1px solid #eee; padding-top: 15px; }
.small-avatar { width: 45px; height: 45px; border-radius: 50%; overflow: hidden; background: #ddd; }
.small-avatar img { width: 100%; height: 100%; object-fit: cover; }
.address-details .label { font-size: 0.8rem; color: #999; }

/* Stats Card */
.stat-header { display: flex; justify-content: space-between; font-weight: bold; margin-bottom: 10px; }
.stat-val { color: #629f62; }
.progress-bar-bg { background: #eee; height: 12px; border-radius: 10px; overflow: hidden; }
.progress-bar-fill { background: #629f62; height: 100%; border-radius: 10px; }
.mt-30 { margin-top: 30px; }
.stat-desc { font-size: 0.8rem; color: #888; margin-top: 15px; }

/* Pets Grid */
.section-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.reg-btn-green { background: #629f62; color: white; border: none; padding: 10px 20px; border-radius: 12px; font-weight: bold; cursor: pointer; }
.pet-cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 15px; }
.pet-item-box { background: #f9f9f9; border-radius: 15px; padding: 15px; text-align: center; }
.pet-img-placeholder { width: 100%; aspect-ratio: 1; border-radius: 10px; overflow: hidden; margin-bottom: 10px; }
.pet-img-placeholder img { width: 100%; height: 100%; object-fit: cover; }
.type-tag { font-size: 0.7rem; background: #eee; padding: 2px 6px; border-radius: 5px; }
.type-tag.DOG { background: #eef7ee; color: #629f62; }
.type-tag.CAT { background: #fff0f5; color: #ff69b4; }

/* Activity Card & Modal UI */
.activity-tabs { display: flex; gap: 10px; margin-bottom: 20px; }
.act-tab { padding: 8px 15px; border-radius: 8px; border: none; background: #f0f0f0; cursor: pointer; }
.act-tab.active { background: #629f62; color: white; }

.inner-modal-ui { background: #f9f9f9; border-radius: 20px; padding: 20px; box-shadow: inset 0 2px 5px rgba(0,0,0,0.05); }
.modal-header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.close-x { border: none; background: none; font-size: 20px; cursor: pointer; }

.form-row { margin-bottom: 15px; }
.form-row label { display: block; font-size: 0.9rem; margin-bottom: 5px; font-weight: bold; }
.toggle-group { display: flex; gap: 5px; }
.toggle-group button { flex: 1; padding: 8px; border: 1px solid #ddd; border-radius: 20px; background: white; cursor: pointer; }
.toggle-group button.active { background: #629f62; color: white; border-color: #629f62; }
input, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 10px; box-sizing: border-box; }
.submit-btn-green { width: 100%; padding: 12px; background: #629f62; color: white; border: none; border-radius: 12px; font-weight: bold; cursor: pointer; margin-top: 10px; }

/* 전체 화면 덮기  */
.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(5px); /* 배경 흐림 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

/* 중앙 흰색 박스 */
.modal-window {
  background: white;
  width: 90%;
  max-width: 450px;
  padding: 35px;
  border-radius: 35px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.modal-header h3 { font-size: 1.4rem; font-weight: 800; color: #333; }
.close-btn { border: none; background: none; font-size: 24px; color: #999; cursor: pointer; }

.form-item { margin-bottom: 20px; }
.form-item label { display: block; font-weight: bold; margin-bottom: 8px; color: #555; }

.type-toggles { display: flex; gap: 10px; }
.type-toggles button {
  flex: 1; padding: 10px; border-radius: 12px; border: 1px solid #ddd;
  background: white; cursor: pointer; transition: all 0.2s;
}
.type-toggles button.active { background: #629f62; color: white; border-color: #629f62; }

.modal-submit-btn {
  width: 100%; padding: 15px; background: #629f62; color: white;
  border: none; border-radius: 15px; font-weight: bold; font-size: 1.1rem;
  cursor: pointer; margin-top: 10px;
}
/* 메인 아바타용 이모티콘 스타일 */
.avatar-circle {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f7f0; /* 이모티콘 배경색 */
  overflow: hidden;
}

.default-avatar-icon {
  font-size: 50px; /* 이모티콘 크기 */
  line-height: 1;
}

/* 이미지 크기 고정 */
.avatar-circle img, .small-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-pet-avatar {
  font-size: 50px; /* 이모티콘 크기 */
  line-height: 2;
}


</style>