package com.scv.domain.result.dto;

import java.util.List;

public record TrainInfoDTO(
        List<Double> trainLoss,
        List<Double> trainAccuracy
) {
}
