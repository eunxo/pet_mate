package com.example.demo.repository;

import com.example.demo.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // 특정 유저의 모든 반려동물 목록을 가져오는 메서드 (나중에 마이페이지에서 사용)
    List<Pet> findByUserId(Long userId);
}