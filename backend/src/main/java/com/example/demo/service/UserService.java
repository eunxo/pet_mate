package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${api.pet.service-key}")
    private String petServiceKey;


    /**
     * 카카오 API 기반 동네 인증 (좌표 -> 주소 변환)
     */
    public boolean verifyLocationWithKakao(Double lat, Double lon, String userInputAddress) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                    .queryParam("x", lon) // 경도
                    .queryParam("y", lat) // 위도
                    .toUriString();

            Map<String, Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");

            if (documents != null && !documents.isEmpty()) {
                Map<String, Object> addressMap = (Map<String, Object>) documents.get(0).get("address");
                String regionName = (String) addressMap.get("region_3depth_name");

                return userInputAddress.contains(regionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 회원가입
     */
    @Transactional
    public void join(User user) {
        validateDuplicateUser(user);
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }



    /**
     * 우리 동네 유저 목록 가져오기
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByAddress(String address) {

        return userRepository.findByAddressContaining(address);
    }
    // src/main/java/com/example/demo/service/UserService.java

    @Transactional(readOnly = true)
    public List<User> getUsersByAddressAndPetType(String address, String petType) {
        // 주소 키워드를 포함하고, 지정된 반려동물 타입(DOG/CAT)을 키우는 유저 검색
        return userRepository.findUsersByAddressAndPetType(address, petType);
    }
}