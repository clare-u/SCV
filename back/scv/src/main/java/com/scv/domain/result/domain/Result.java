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

    @Lob
    @Column(name = "train_result", nullable = false)
    private String trainResult;

    @Lob
    @Column(name = "layer_result", nullable = false)
    private String layerResult;

    @Lob
    @Column(name = "confusion_matrix", nullable = false)
    private String confusionMatrix;

    @Lob
    @Column(name = "incorrect_img", nullable = false)
    private String incorrectImages;
}
