package com.scv.domain.data.enums;

import com.scv.domain.data.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataSet {

    MNIST(1, "MNIST"),
    Fashion(2, "Fashion"),
    CIFAR10(3, "CIFAR10"),
    SVHN(4, "SVHN"),
    EMNIST(5, "EMNIST");

    private final int id;
    private final String name;

    public static DataSet fromId(int id) {
        for (DataSet dataset : DataSet.values()) {
            if (dataset.getId() == id) {
                return dataset;
            }
        }
        throw new DataNotFoundException();
    }
}
