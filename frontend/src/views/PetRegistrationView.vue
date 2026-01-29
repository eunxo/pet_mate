<template>
  <div class="registration-container">
    <h2>반려동물을 등록해주세요 🐾</h2>
    <form @submit.prevent="submitPetInfo">
      <div class="form-group">
        <label>반려동물 종류</label>
        <select v-model="petData.type">
          <option value="DOG">강아지</option>
          <option value="CAT">고양이</option>
        </select>
      </div>
      <div class="form-group">
        <label>이름</label>
        <input type="text" v-model="petData.name" required>
      </div>
      <div class="form-group">
        <label>동물등록번호</label>
        <input type="text" v-model="petData.registrationNo">
      </div>
      <div class="form-group">
        <label>성향 태그</label>
        <input type="text" v-model="petData.tags" placeholder="#활발함 #조용함">
      </div>
      <button type="submit">등록 완료</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const petData = ref({
  type: 'DOG',
  name: '',
  registrationNo: '',
  tags: '',
  photoUrl: ''
})

// 오류의 원인이었던 함수 정의
const submitPetInfo = async () => {
  try {
    // 로컬 스토리지에서 로그인한 유저 정보를 가져와 ID를 함께 보냅니다.
    const user = JSON.parse(localStorage.getItem('user'))
    const payload = {
      ...petData.value,
      userId: user.id // ERD의 user_id 외래키와 연결
    }

    const response = await axios.post('http://localhost:8080/api/pets/register', payload)
    alert(response.data)
    router.push('/') // 등록 완료 후 메인 페이지로 이동
  } catch (error) {
    alert('등록 실패: ' + (error.response?.data || error.message))
  }
}
</script>