package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원가입
     */
    @Transactional
    public void join(User user) {
        // 1. 중복 회원 검증 (이메일 기준)
        validateDuplicateUser(user);

        // 2. 초기 가입 시 기본 설정 (필요 시)
        // 예: user.setLocationVerified(true);

        // 3. DB 저장
        userRepository.save(user);
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 가입된 이메일입니다.");
                });
    }

    /**
     * 로그인 확인
     */
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        // 1. 이메일로 회원 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 비밀번호 비교
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    /**
     * 반려동물 정보 및 성향 태그 업데이트
     */
    @Transactional
    public void updatePetInfo(Long userId, String petTags, String petName, String petType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setPetTags(petTags);
        user.setPetName(petName);
        user.setPetType(petType); // DOG or CAT
        // JPA의 변경 감지(Dirty Checking) 기능으로 자동 저장됩니다.
    }

    /**
     * [당근마켓 방식] 우리 동네 유저 목록 가져오기
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByAddress(String address) {
        // 주소에 해당 키워드(예: "역삼동")가 포함된 유저들을 리스트로 반환
        return userRepository.findByAddressContaining(address);
    }
}