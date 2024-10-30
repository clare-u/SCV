package com.scv.domain.model.domain;

import com.scv.domain.data.domain.Data;
import com.scv.domain.user.domain.User;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.global.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "model")
@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Model extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id", nullable = false)
    private Data data;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModelVersion> modelVersions;

    @Column(name = "model_name", length = 20, nullable = false)
    private String name;

    @Column(name = "model_latest", nullable = false)
    private int latestVersion;

    /**
     * 최신 버전 갱신
     */
    public void updateLatestVersion() {
        this.latestVersion++;
    }
}
