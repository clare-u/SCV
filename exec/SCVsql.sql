CREATE TABLE `data` (
  `data_id` int NOT NULL AUTO_INCREMENT,
  `data_epoch_cnt` int DEFAULT NULL,
  `data_label_cnt` int DEFAULT NULL,
  `data_name` enum('CIFAR10','EMNIST','Fashion','MNIST','SVHN') DEFAULT NULL,
  `data_test_cnt` int DEFAULT NULL,
  `data_train_cnt` int DEFAULT NULL,
  PRIMARY KEY (`data_id`),
  KEY `idx_data_name` (`data_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `model` (
  `model_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `model_latest_accuracy` double DEFAULT NULL,
  `model_latest` int DEFAULT NULL,
  `model_name` varchar(20) NOT NULL,
  `data_id` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`model_id`),
  KEY `FKl73a1kwbg7jr3ewiykjmxlstl` (`data_id`),
  KEY `FKrmdvh2mj4uuwrlpjnwgev2lf` (`user_id`),
  KEY `idx_model_name` (`model_name`),
  CONSTRAINT `FKl73a1kwbg7jr3ewiykjmxlstl` FOREIGN KEY (`data_id`) REFERENCES `data` (`data_id`),
  CONSTRAINT `FKrmdvh2mj4uuwrlpjnwgev2lf` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `model_version` (
  `model_version_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_working_on` tinyint(1) NOT NULL,
  `version_layer_at` json DEFAULT NULL,
  `version_no` int NOT NULL,
  `model_id` bigint NOT NULL,
  PRIMARY KEY (`model_version_id`),
  KEY `FKdbga23c8lhs8cb1ob9r4cv2m7` (`model_id`),
  CONSTRAINT `FKdbga23c8lhs8cb1ob9r4cv2m7` FOREIGN KEY (`model_id`) REFERENCES `model` (`model_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `result` (
  `model_version_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `activation_maximization` json DEFAULT NULL,
  `code_view` json DEFAULT NULL,
  `confusion_matrix` json DEFAULT NULL,
  `example_img` json DEFAULT NULL,
  `feature_activation` json DEFAULT NULL,
  `layer_params` json DEFAULT NULL,
  `test_accuracy` double DEFAULT NULL,
  `test_loss` double DEFAULT NULL,
  `total_params` int DEFAULT NULL,
  `train_info` json DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`model_version_id`),
  CONSTRAINT `FKmbihos3rmlxhx8aqe3ssiuqb5` FOREIGN KEY (`model_version_id`) REFERENCES `model_version` (`model_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `user_created_at` datetime(6) NOT NULL,
  `user_email` varchar(100) NOT NULL,
  `user_image_url` varchar(100) NOT NULL,
  `user_is_deleted` bit(1) NOT NULL,
  `user_nickname` varchar(50) NOT NULL,
  `user_repo` varchar(50) DEFAULT NULL,
  `user_updated_at` datetime(6) NOT NULL,
  `user_uuid` varchar(36) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UKj09k2v8lxofv2vecxu2hde9so` (`user_email`),
  UNIQUE KEY `UKgpwo3hv4nrhr788k8th3gcem4` (`user_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE INDEX idx_data_name ON data(data_name);

INSERT INTO data (data_id, data_epoch_cnt, data_label_cnt, data_name, data_test_cnt, data_train_cnt) VALUES
(1, 2, 10, 'MNIST', 10000, 50000),
(2, 2, 10, 'Fashion', 10000, 50000),
(3, 2, 10, 'CIFAR10', 10000, 50000),
(4, 10, 10, 'SVHN', 10000, 50000),
(5, 2, 26, 'EMNIST', 10000, 50000);
CREATE INDEX idx_model_name ON model(model_name);
