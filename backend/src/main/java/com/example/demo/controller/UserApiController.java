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

    // 1. 회원가입 (기존과 동일)
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody User user) {
        try {
            userService.join(user);
            return ResponseEntity.ok("회원가입 및 동네 설정 완료!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("가입 실패: " + e.getMessage());
        }
    }

    // 2. 로그인 (성공 시 유저의 동네 정보 포함 반환)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            User user = userService.login(loginData.get("email"), loginData.get("password"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("로그인 정보가 일치하지 않습니다.");
        }
    }

    // 3. 당근마켓식 '우리 동네' 사용자 목록 가져오기
    // 실시간 좌표가 아니라, 가입 시 저장된 address(동네)가 같은 유저들을 찾습니다.
    @GetMapping("/neighbor-list")
    public ResponseEntity<List<User>> getNeighbors(@RequestParam String address) {
        // 주소에서 '역삼동' 같은 동 단위만 추출해서 검색하는 로직이 서비스에 필요합니다.
        List<User> neighbors = userService.getUsersByAddress(address);
        return ResponseEntity.ok(neighbors);
    }
}