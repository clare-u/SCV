package com.scv.domain.model.domain;

import com.scv.domain.data.domain.Data;
import com.scv.domain.user.domain.User;
import com.scv.domain.version.domain.ModelVersion;
import com.scv.global.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.Comparator;
import java.util.List;

@Table(name = "model", indexes = {
        @Index(name = "idx_model_name", columnList = "name")
})
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
    @BatchSize(size = 10)
    private List<ModelVersion> modelVersions;

    @Column(name = "model_name", length = 20, nullable = false)
    private String name;

    @Column(name = "model_latest")
    private Integer latestVersion;

    @Column(name = "model_latest_accuracy")
    private Double accuracy;

    /**
     * 최신 버전 번호 설정
     *
     * @param versionNo 새로운 최신 버전 번호
     */
    public void setLatestVersion(int versionNo) {
        this.latestVersion = versionNo;
    }

    /**
     * 최신 버전 정확도 설정 설정
     *
     * @param accuracy 새로운 최신 버전 정확도
     */
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * 이름 변경
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * 최신 버전의 ID 가져오기
     * @return latestVersionId
     */
    public Long getLatestVersionId() {
        return modelVersions.stream()
                .filter(version -> !version.isDeleted())
                .max(Comparator.comparingInt(ModelVersion::getVersionNo))
                .map(ModelVersion::getId)
                .orElse(null);
    }


}
