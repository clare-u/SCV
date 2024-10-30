package com.scv.domain.data.enums;

import com.scv.domain.data.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Dataset {

    MNIST(1, "MNIST"),
    FASHION(2, "Fashion"),
    CIFAR10(3, "CIFAR-10"),
    SVHN(4, "SVHN"),
    EMNIST(5, "EMNIST");

    private final int id;
    private final String name;

    public static Dataset fromId(int id) {
        for (Dataset dataset : Dataset.values()) {
            if (dataset.getId() == id) {
                return dataset;
            }
        }
        throw new DataNotFoundException();
    }
}
