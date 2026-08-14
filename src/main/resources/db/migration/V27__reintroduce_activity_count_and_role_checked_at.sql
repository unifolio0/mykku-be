-- V27: 칭호 자동 획득(이벤트 기반) 재도입 + 신규 획득 칭호 확인 상태 추가
SET NAMES utf8mb4;

CREATE TABLE member_activity_count
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT      NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    count         BIGINT      NOT NULL DEFAULT 0,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT uk_member_activity UNIQUE (member_id, activity_type),
    CONSTRAINT fk_member_activity_count_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

ALTER TABLE member_role ADD COLUMN checked_at DATETIME(6) NULL;
UPDATE member_role SET checked_at = created_at;
