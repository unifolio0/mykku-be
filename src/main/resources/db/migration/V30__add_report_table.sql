-- V30: 콘텐츠 신고 기능
-- target_type/target_id 다형 대상 + target_member_id 스냅샷(대상 삭제 후에도 작성자 조치 가능)
-- 회원 FK는 V19에서 member.id가 BIGINT로 전환되었으므로 BIGINT여야 한다

CREATE TABLE report
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id      BIGINT       NOT NULL,
    target_type      VARCHAR(20)  NOT NULL,
    target_id        BIGINT       NOT NULL,
    target_member_id BIGINT       NULL,
    reason           VARCHAR(30)  NOT NULL,
    detail           VARCHAR(500) NULL,
    status           VARCHAR(20)  NOT NULL,
    processed_at     DATETIME(6)  NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    CONSTRAINT uk_report_reporter_target UNIQUE (reporter_id, target_type, target_id),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT fk_report_target_member FOREIGN KEY (target_member_id) REFERENCES member (id) ON DELETE SET NULL
);

-- uk_report_reporter_target의 leftmost가 reporter_id라서 관리자 큐 정렬과 대상별 조회를 타지 못한다
CREATE INDEX idx_report_status_created ON report (status, created_at);
CREATE INDEX idx_report_target ON report (target_type, target_id);
