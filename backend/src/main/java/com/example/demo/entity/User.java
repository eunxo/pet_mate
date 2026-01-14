package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email; // 로그인 ID로 사용

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String address; // 텍스트 주소 (예: 역삼동)

    // --- [위치 정보: 실시간 매칭용] ---
    private Double latitude;  // 위도 (예: 37.123456)
    private Double longitude; // 경도 (예: 127.123456)

    // --- [반려동물 정보: 고유 기능용] ---
    private String petType;       // DOG(강아지), CAT(고양이)
    private String petName;       // 반려동물 이름
    private String registrationNo; // 반려동물 등록번호 (공공데이터 API 조회용)

    @Column(length = 500)
    private String petTags;       // 성향 태그 (예: #겁쟁이, #에너자이저)

    // --- [인증 상태: 신뢰 시스템용] ---
    private boolean isLocationVerified = false; // GPS 인증 여부
    private boolean isPetVerified = false;      // 동물등록 인증 여부
}