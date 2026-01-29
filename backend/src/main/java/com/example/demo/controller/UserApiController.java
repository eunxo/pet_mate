package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserApiController {

    private final UserService userService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody User user) {
        try {
            if (user.getLatitude() != null && user.getLongitude() != null) {
                boolean isVerified = userService.verifyLocationWithKakao(
                        user.getLatitude(), user.getLongitude(), user.getAddress()
                );
                user.setLocationVerified(isVerified);
            }
            userService.join(user);
            return ResponseEntity.ok("회원가입 완료! 펫 등록 페이지로 이동합니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("가입 실패: " + e.getMessage());
        }
    }

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            User user = userService.login(loginData.get("email"), loginData.get("password"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("로그인 정보가 일치하지 않습니다.");
        }
    }

    // 3. 이웃 목록 조회 (수정 완료)
    @GetMapping("/neighbor-list")
    public ResponseEntity<List<User>> getNeighbors(
            @RequestParam(name = "address") String address,
            @RequestParam(name = "petType") String petType) {

        // 주소와 반려동물 종류가 일치하는 이웃 목록 조회
        List<User> neighbors = userService.getUsersByAddressAndPetType(address, petType);
        return ResponseEntity.ok(neighbors);
    }
}