package com.scv.domain.data.dto;

import com.scv.domain.data.domain.Data;
import com.scv.domain.data.enums.DataSet;

public record DataDTO(
        DataSet dataName,
        int trainCnt,
        int testCnt,
        int labelCnt,
        int epochCnt
) {
    public DataDTO(Data data) {
        this(
                data.getName(),
                data.getTrainCnt(),
                data.getTestCnt(),
                data.getLabelCnt(),
                data.getEpochCnt()
        );
    }
}
