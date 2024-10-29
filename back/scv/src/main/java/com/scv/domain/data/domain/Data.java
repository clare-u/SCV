package com.scv.domain.data.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "data")
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

    @Column(name = "data_name")
    private String name;

    @Column(name = "data_train_cnt")
    private int trainCnt;

    @Column(name = "data_test_cnt")
    private int testCnt;

    @Column(name = "data_label_cnt")
    private int labelCnt;

    @Column(name = "data_epock_cnt")
    private int epochCnt;

//    model과 단방향 연결
//    @OneToOne(mappedBy = "data", fetch = FetchType.LAZY)
//    private Model model;
}
