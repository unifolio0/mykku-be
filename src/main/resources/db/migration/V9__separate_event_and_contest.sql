-- V9: Event와 Contest 분리

-- 1. Contest 테이블 생성
CREATE TABLE contest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    expired_at DATETIME(6) NOT NULL,
    scrap_count INT DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- 2. ContestImage 테이블 생성
CREATE TABLE contest_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    contest_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE
);

-- 3. ContestTag 테이블 생성 (event_tag를 대체)
CREATE TABLE contest_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(20) NOT NULL,
    contest_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE
);

-- 4. event_tag 테이블 삭제 (더 이상 사용하지 않음, 태그는 Contest에만 적용)
DROP TABLE IF EXISTS event_tag;

-- 5. contest_winner 테이블의 FK를 contest로 변경
-- 기존 FK 제거
ALTER TABLE contest_winner DROP FOREIGN KEY contest_winner_ibfk_1;
-- event_id 컬럼을 contest_id로 변경
ALTER TABLE contest_winner CHANGE COLUMN event_id contest_id BIGINT NOT NULL;
-- 새로운 FK 추가
ALTER TABLE contest_winner ADD CONSTRAINT fk_contest_winner_contest
    FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE;

-- 6. Event 테이블 수정
-- is_contest 컬럼 삭제
ALTER TABLE event DROP COLUMN is_contest;
-- description 컬럼 추가
ALTER TABLE event ADD COLUMN description TEXT AFTER title;

-- 7. SaveContest 테이블 생성
CREATE TABLE save_contest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    contest_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_save_contest_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_save_contest_contest FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE,
    CONSTRAINT uk_save_contest_member_contest UNIQUE (member_id, contest_id)
);

-- 8. 인덱스 생성
CREATE INDEX idx_contest_expired_at ON contest(expired_at);
CREATE INDEX idx_contest_image_contest_id ON contest_image(contest_id);
CREATE INDEX idx_contest_tag_contest_id ON contest_tag(contest_id);
CREATE INDEX idx_contest_tag_title ON contest_tag(title);
CREATE INDEX idx_save_contest_member_id ON save_contest(member_id);
CREATE INDEX idx_save_contest_contest_id ON save_contest(contest_id);
