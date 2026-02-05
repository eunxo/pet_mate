package com.example.demo.controller;

import com.example.demo.entity.Pet; //
import com.example.demo.service.PetService; //
import com.example.demo.controller.PetDto;
import lombok.RequiredArgsConstructor; //
import org.springframework.http.ResponseEntity; //
import org.springframework.web.bind.annotation.*; //
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> registerPet(
            @RequestParam("userId") Long userId,
            @RequestParam("type") String type,
            @RequestParam("name") String name,
            @RequestParam(value = "registrationNo", required = false) String registrationNo,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "file", required = false) MultipartFile file) { // 2. 사진 파일 파라미터
        try {
            // DTO 객체 생성 및 데이터 매핑
            PetDto petDto = new PetDto();
            petDto.setType(type);
            petDto.setName(name);
            petDto.setRegistrationNo(registrationNo);
            petDto.setTags(tags);
            petDto.setDescription(description);

            // 3. 서비스 호출 시 파일(file)을 세 번째 인자로 추가하여 전달
            petService.registerPet(userId, petDto, file);

            return ResponseEntity.ok("반려동물 등록이 성공적으로 완료되었습니다!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage());
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