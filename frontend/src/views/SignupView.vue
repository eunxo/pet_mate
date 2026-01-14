<template>
  <div class="signup-container">
    <h2>반려인 커뮤니티 가입 🐾</h2>
    <form @submit.prevent="handleSignup">
      <div class="form-group">
        <label>이메일</label>
        <input type="email" v-model="user.email" placeholder="example@email.com" required>
      </div>

      <div class="form-group">
        <label>비밀번호</label>
        <input type="password" v-model="user.password" required>
      </div>

      <div class="form-group">
        <label>닉네임</label>
        <input type="text" v-model="user.nickname" required>
      </div>

      <div class="form-group">
        <label>우리 동네 (주소)</label>
        <input type="text" v-model="user.address" placeholder="예: 서울시 강남구 역삼동" required>
      </div>

      <div class="form-group">
        <label>반려동물 등록번호 (선택)</label>
        <input type="text" v-model="user.registrationNo" placeholder="15자리 숫자 입력">
        <small>등록번호를 입력하면 인증 뱃지가 부여됩니다!</small>
      </div>

      <button type="submit" class="signup-btn">가입하기</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const user = ref({
  email: '',
  password: '',
  nickname: '',
  address: '',
  registrationNo: ''
})

const handleSignup = async () => {
  try {
    // 백엔드 스프링 서버(8080)로 데이터 전송
    const response = await axios.post('http://localhost:8080/api/users/signup', user.value)
    alert(response.data) // "회원가입 성공!" 메시지 출력
  } catch (error) {
    console.error('가입 중 오류 발생:', error)
    alert('가입에 실패했습니다.')
  }
}
// 위치 정보를 가져오는 함수
const getLocation = () => {
  if (!navigator.geolocation) {
    alert("브라우저가 GPS를 지원하지 않습니다.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      // 위도와 경도를 추출해서 user 객체에 담습니다.
      user.value.latitude = position.coords.latitude;
      user.value.longitude = position.coords.longitude;
      
      alert(`위치 인증 성공! (위도: ${user.value.latitude}, 경도: ${user.value.longitude})`);
      console.log("좌표 정보:", user.value.latitude, user.value.longitude);
    },
    (error) => {
      alert("위치 정보를 가져오는 데 실패했습니다: " + error.message);
    }
  );
};
</script>

<style scoped>
.signup-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; }
.form-group { margin-bottom: 15px; }
.form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.form-group input { width: 100%; padding: 8px; box-sizing: border-box; }
.signup-btn { width: 100%; padding: 10px; background-color: #4AAE9B; color: white; border: none; border-radius: 5px; cursor: pointer; }
.signup-btn:hover { background-color: #f7f7f7; }
</style>