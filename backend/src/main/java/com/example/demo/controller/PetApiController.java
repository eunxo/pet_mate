package com.example.demo.controller;

import com.example.demo.entity.Pet; //
import com.example.demo.service.PetService; //
import lombok.RequiredArgsConstructor; //
import org.springframework.http.ResponseEntity; //
import org.springframework.web.bind.annotation.*; //

import java.util.List; //
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") //
public class PetApiController {

    private final PetService petService; //

    /**
     * 반려동물 등록 실행
     * @RequestBody로 PetDto를 바로 받습니다.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerPet(@RequestBody Map<String, Object> request) {
        try {
            // 프론트엔드에서 보낸 데이터 중 userId를 추출합니다.
            Long userId = Long.valueOf(request.get("userId").toString());

            // 전송된 데이터를 DTO 객체로 변환합니다.
            PetDto petDto = new PetDto();
            petDto.setType((String) request.get("type"));
            petDto.setName((String) request.get("name"));
            petDto.setRegistrationNo((String) request.get("registrationNo"));
            petDto.setTags((String) request.get("tags"));
            // 계획서에 따라 상세 설명을 추가로 받을 수 있습니다.
            if (request.containsKey("description")) {
                petDto.setDescription((String) request.get("description"));
            }

            petService.registerPet(userId, petDto); //
            return ResponseEntity.ok("반려동물 등록이 성공적으로 완료되었습니다!"); //
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage()); //
        }
    }

    /**
     * 특정 유저의 반려동물 목록 조회
     * 마이페이지 등에서 실시간 데이터를 확인할 때 사용합니다.
     */
    @GetMapping("/list")
    public ResponseEntity<List<Pet>> getMyPets(@RequestParam(name = "userId") Long userId) {
        try {
            List<Pet> myPets = petService.getMyPetsByUserId(userId);
            return ResponseEntity.ok(myPets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}