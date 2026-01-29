package com.example.demo.service;

import com.example.demo.entity.Pet;
import com.example.demo.entity.User;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // @Value를 위해 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository; //
    private final UserRepository userRepository;

    // 1. 누락되었던 petServiceKey 선언
    @Value("${api.pet.service-key}")
    private String petServiceKey;

    /**
     * 반려동물 등록 실행
     */
    @Transactional
    public void registerPet(Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Pet pet = new Pet();
        pet.setUser(user); // ERD에 따른 유저와 펫의 1:N 관계 설정
        pet.setType(payload.get("type").toString());
        pet.setName(payload.get("name").toString());
        pet.setRegistrationNo(payload.get("registrationNo").toString());
        pet.setTags(payload.get("tags").toString());

        petRepository.save(pet); //
    }

    /**
     * 반려동물 등록번호 유효성 검사 (공공데이터 API 호출)
     */
    public boolean verifyPetRegistration(String dogRegNo, String ownerNm) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // 2. petServiceKey를 사용하여 URI 생성
            URI uri = UriComponentsBuilder
                    .fromHttpUrl("http://apis.data.go.kr/1543061/animalInfoSrvc/animalInfo")
                    .queryParam("serviceKey", petServiceKey)
                    .queryParam("dog_reg_no", dogRegNo)
                    .queryParam("owner_nm", ownerNm)
                    .queryParam("_type", "json")
                    .build(true)
                    .toUri();

            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

            // 공공데이터 API 응답 구조 파싱 (JSON 계층 구조 확인)
            Map<String, Object> responseBody = (Map<String, Object>) response.get("response");
            Map<String, Object> body = (Map<String, Object>) responseBody.get("body");

            // 결과 건수가 0보다 크면 유효한 등록번호로 간주
            int totalCount = (int) body.get("totalCount");
            return totalCount > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}