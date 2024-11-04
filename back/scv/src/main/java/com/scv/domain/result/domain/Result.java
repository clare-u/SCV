package com.scv.domain.result.domain;

import com.scv.domain.version.domain.ModelVersion;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "result")
@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id", nullable = false)
    private ModelVersion modelVersion;

    @Column(name = "test_loss", nullable = false)
    private double testLoss;

    @Column(name = "test_accuracy", nullable = false)
    private double testAccuracy;

    @Column(name = "train_result", columnDefinition = "JSON")
    private String trainResult;

    @Column(name = "layer_result", columnDefinition = "JSON")
    private String layerResult;

    @Column(name = "confusion_matrix", columnDefinition = "JSON")
    private String confusionMatrix;

    @Column(name = "incorrect_img", columnDefinition = "JSON")
    private String incorrectImages;
}
