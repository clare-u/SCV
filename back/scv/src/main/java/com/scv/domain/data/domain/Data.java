package com.scv.domain.data.domain;

import com.scv.domain.data.enums.DataSet;
import com.scv.domain.model.domain.Model;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "data", indexes = {
        @Index(name = "idx_data_name", columnList = "data_name")
})
@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Data {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id", nullable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_name")
    private DataSet name;

    @Column(name = "data_train_cnt")
    private int trainCnt;

    @Column(name = "data_test_cnt")
    private int testCnt;

    @Column(name = "data_label_cnt")
    private int labelCnt;

    @Column(name = "data_epoch_cnt")
    private int epochCnt;

    @OneToMany(mappedBy = "data", fetch = FetchType.LAZY)
    private List<Model> model;
}
