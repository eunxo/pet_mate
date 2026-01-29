package com.example.demo.controller;

import com.example.demo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PetApiController {

    private final PetService petService;

    @PostMapping("/register")
    public ResponseEntity<String> registerPet(@RequestBody Map<String, Object> payload) {
        try {
            petService.registerPet(payload);
            return ResponseEntity.ok("반려동물 등록이 성공적으로 완료되었습니다!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage());
        }
    }
}