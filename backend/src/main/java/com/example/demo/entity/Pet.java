package com.example.demo.entity;

import com.example.demo.entity.User;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user; // 주인 ID (FK)

    private String type; // DOG or CAT
    private String name;
    private String registrationNo;
    private String tags; // 성향 태그
    private String photoUrl;
}