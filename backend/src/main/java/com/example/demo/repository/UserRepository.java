package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 이메일 중복 체크를 위해 필요한 메서드
    Optional<User> findByEmail(String email);

    List<User> findByAddressContaining(String address);

    // 2. 주소와 펫 타입을 동시에 검색하기 위한 메서드 (@Query 사용)
    @Query("SELECT DISTINCT u FROM User u JOIN u.pets p WHERE u.address LIKE %:address% AND p.type = :petType")
    List<User> findUsersByAddressAndPetType(@Param("address") String address, @Param("petType") String petType);
    @EntityGraph(attributePaths = {"pets"})
    Optional<User> findWithPetsById(Long id);
}