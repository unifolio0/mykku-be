-- V9: Event와 Contest 분리 및 description 컬럼 추가

-- 1. contest 테이블 생성
CREATE TABLE contest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    expired_at DATETIME(6) NOT NULL,
    scrap_count INT DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- 2. contest_image 테이블 생성
CREATE TABLE contest_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    contest_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE
);

-- 3. contest_tag 테이블 생성
CREATE TABLE contest_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(20) NOT NULL,
    contest_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE
);

-- 4. contest_winner 테이블 FK 변경 (event_id → contest_id)
ALTER TABLE contest_winner DROP FOREIGN KEY contest_winner_ibfk_1;
ALTER TABLE contest_winner CHANGE event_id contest_id BIGINT NOT NULL;
ALTER TABLE contest_winner ADD FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE;

-- 5. event 테이블에 description 컬럼 추가
ALTER TABLE event ADD COLUMN description VARCHAR(500);

-- 6. event 테이블에서 is_contest 컬럼 제거
ALTER TABLE event DROP COLUMN is_contest;

-- 7. save_contest 테이블 생성 (Scrap 기능)
CREATE TABLE save_contest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    contest_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE
);

-- 8. 인덱스 생성
CREATE INDEX idx_contest_image_contest_id ON contest_image(contest_id);
CREATE INDEX idx_contest_tag_contest_id ON contest_tag(contest_id);
CREATE INDEX idx_contest_winner_contest_id ON contest_winner(contest_id);
CREATE INDEX idx_save_contest_member_id ON save_contest(member_id);
CREATE INDEX idx_save_contest_contest_id ON save_contest(contest_id);
