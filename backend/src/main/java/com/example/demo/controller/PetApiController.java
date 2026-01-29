package com.example.demo.controller;

import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PetApiController {

    private final PetService petService;

    // @RequestBody로 PetDto를 바로 받습니다.
    // 펫 등록 시 주인이 누구인지 알아야 하므로 쿼리 파라미터나 DTO 내부에 userId가 필요합니다.
    @PostMapping("/register")
    public ResponseEntity<String> registerPet(
            @RequestParam(name = "userId") Long userId,
            @RequestBody PetDto petDto) {
        try {
            petService.registerPet(userId, petDto);
            return ResponseEntity.ok("반려동물 등록이 성공적으로 완료되었습니다!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage());
        }
    }
}