package com.example.demo.service;

import com.example.demo.controller.PetDto;
import com.example.demo.entity.Pet;
import com.example.demo.entity.User;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // @Value를 위해 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // 1. 누락되었던 petServiceKey 선언
    @Value("${api.pet.service-key}")
    private String petServiceKey;

    /**
     * 반려동물 등록 실행
     */

    // 사진이 저장될 서버의 실제 경로 (예: C:/uploads/pets/ 또는 /home/ubuntu/uploads/)
    private final String uploadPath = "C:/Web/uploads/pets/";

    @Transactional
    public void registerPet(Long userId, PetDto petDto, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Pet pet = new Pet();
        pet.setUser(user);

        pet.setType(petDto.getType());
        pet.setName(petDto.getName());
        pet.setRegistrationNo(petDto.getRegistrationNo());
        pet.setTags(petDto.getTags());
        pet.setDescription(petDto.getDescription());

        petRepository.save(pet);

        //사진 파일 처리 로직
        if (file != null && !file.isEmpty()) {
            // 1. 파일명 중복 방지를 위해 UUID 생성
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File saveFile = new File(uploadPath, fileName);

            // 2. 해당 폴더가 없으면 생성
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }

            // 3. 서버 하드디스크에 파일 저장
            file.transferTo(saveFile);

            // 4. DB에는 접근 가능한 URL 경로를 저장 (예: /uploads/pets/uuid_image.jpg)
            pet.setPhotoUrl("/uploads/pets/" + fileName);
        }

        // 등록번호가 있는 경우 유저의 펫 인증 상태를 true로 변경할 수도 있습니다
        if (pet.getRegistrationNo() != null && !pet.getRegistrationNo().isEmpty()) {
            user.setPetVerified(true);
            userRepository.save(user);
        }
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
    public List<Pet> getMyPetsByUserId(Long userId) {
        return petRepository.findByUserId(userId);
    }
}