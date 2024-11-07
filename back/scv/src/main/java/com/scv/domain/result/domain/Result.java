package com.scv.domain.result.domain;

import com.scv.domain.version.domain.ModelVersion;
import com.scv.global.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "result")
@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Result extends BaseEntity {

    @Id
    @Column(name = "model_version_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "model_version_id", nullable = false)
    private ModelVersion modelVersion;

    @Column(name = "code_view", columnDefinition = "JSON")
    private String code;

    @Column(name = "test_accuracy")
    private Double testAccuracy;

    @Column(name = "test_loss")
    private Double testLoss;

    @Column(name = "train_info", columnDefinition = "JSON")
    private String trainInfo;

    @Column(name = "confusion_matrix", columnDefinition = "JSON")
    private String confusionMatrix;

    @Column(name = "example_img", columnDefinition = "JSON")
    private String exampleImg;

    @Column(name = "total_params")
    private int totalParams;

    @Column(name = "params", columnDefinition = "JSON")
    private String params;
//
//    @Column(name = "layer_params", columnDefinition = "JSON")
//    private String layerParams;

    @Column(name = "feature_activation", columnDefinition = "JSON")
    private String featureActivation;

    @Column(name = "activation_maximization", columnDefinition = "JSON")
    private String activationMaximization;


}
