package com.scv.domain.user.repository;

import com.scv.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserEmail(String userEmail);

    @Modifying
    @Query("UPDATE User u SET u.userRepo = :userRepo WHERE u.userId = :userId")
    void updateUserRepoById(Long userId, String userRepo);
}
