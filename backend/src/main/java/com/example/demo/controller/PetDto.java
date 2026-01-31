package com.example.demo.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetDto {
    private String type;           // DOG or CAT
    private String name;           // 반려동물 이름
    private String registrationNo; // 등록 번호
    private String tags;           // 성향 태그
    private String photoUrl;       // 사진 경로
    private String description;    // 상세 설명 (사료, 배변 습관 등) - 추가
}