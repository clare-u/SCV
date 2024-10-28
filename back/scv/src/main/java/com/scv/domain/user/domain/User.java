package com.scv.domain.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_uuid", unique = true, nullable = false, length = 36)
    private String userUuid;

    @Column(name = "user_email", unique = true, nullable = false, length = 100)
    private String userEmail;

    @Column(name = "user_image_url", nullable = false, length = 100)
    private String userImageUrl;

    @Column(name = "user_nickname", nullable = false, length = 30)
    private String userNickname;

    @Column(name = "user_created_at", nullable = false)
    private LocalDateTime userCreatedAt;

    @Column(name = "user_updated_at", nullable = false)
    private LocalDateTime userUpdatedAt;

    @Column(name = "user_is_deleted", nullable = false)
    private boolean userIsDeleted = false;
}
