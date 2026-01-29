package com.example.demo.entity;

import com.example.demo.entity.Pet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String address;
    private Double latitude;
    private Double longitude;
    private boolean isLocationVerified;
    private boolean isPetVerified;

    // 펫과의 연결 (ERD 반영)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();

}