
-- **************************************** TABLE START ****************************************
-- User Table
CREATE TABLE `user` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `email` VARCHAR(255),
    `name` VARCHAR(255),
    `role` VARCHAR(255),
    `sub` VARCHAR(255),
    CONSTRAINT `unique_sub` UNIQUE (`sub`)
) ENGINE=InnoDB;

-- RefreshToken Table
CREATE TABLE `refresh_token` (
     `refresh_token_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
     `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
     `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
     `raw_token` VARCHAR(255) NOT NULL,
     `user_id` BIGINT
) ENGINE=InnoDB;

-- Engdu Table
CREATE TABLE `engdu` (
    `engdu_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `is_all_solved` BIT(1) NOT NULL,
    `solved_count` INT NOT NULL,
    `like_status` VARCHAR(255),
    `title` VARCHAR(255),
    `topic` VARCHAR(255),
    `user_id` BIGINT
) ENGINE=InnoDB;

-- Article Table
CREATE TABLE `article` (
    `article_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `engdu_id` BIGINT NOT NULL
) ENGINE=InnoDB;

-- ArticleChunk Table
CREATE TABLE `article_chunk` (
    `article_chunk_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `en` VARCHAR(2000),
    `kor` VARCHAR(2000),
    `article_id` BIGINT NOT NULL
) ENGINE=InnoDB;

-- Question Table
CREATE TABLE `question` (
    `question_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `answer` TINYINT NOT NULL,
    `category` VARCHAR(255),
    `content` VARCHAR(255),
    `is_corrected` BIT(1) NOT NULL,
    `engdu_id` BIGINT NOT NULL
) ENGINE=InnoDB;

-- Choice Table
CREATE TABLE `choice` (
    `choice_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `created_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `modified_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `content` VARCHAR(255),
    `explanation` VARCHAR(255),
    `seq` TINYINT,
    `question_id` BIGINT NOT NULL
) ENGINE=InnoDB;
-- **************************************** TABLE END****************************************

-- **************************************** INDEX START ****************************************
CREATE INDEX idx_article_engdu_id ON `article` (`engdu_id`);
CREATE INDEX idx_article_chunk_article_id ON `article_chunk` (`article_id`);
CREATE INDEX idx_question_engdu_id ON `question` (`engdu_id`);
CREATE INDEX idx_choice_question_id ON `choice` (`question_id`);
-- **************************************** INDEX END ****************************************
