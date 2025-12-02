-- V10: Participation 테이블 생성 및 Event/Contest에 status, started_at 컬럼 추가

-- 1. EventParticipation 테이블 생성
CREATE TABLE event_participation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    event_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_event_participation_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_participation_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT uk_event_participation_member_event UNIQUE (member_id, event_id)
);

-- 2. ContestParticipation 테이블 생성
CREATE TABLE contest_participation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    contest_id BIGINT NOT NULL,
    feed_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_contest_participation_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_contest_participation_contest FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE,
    CONSTRAINT fk_contest_participation_feed FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE,
    CONSTRAINT uk_contest_participation_member_contest_feed UNIQUE (member_id, contest_id, feed_id)
);

-- 3. Event 테이블에 status, started_at 컬럼 추가
ALTER TABLE event ADD COLUMN status VARCHAR(50) NOT NULL;
ALTER TABLE event ADD COLUMN started_at DATETIME(6) NOT NULL;

-- 4. Contest 테이블에 status, started_at 컬럼 추가
ALTER TABLE contest ADD COLUMN status VARCHAR(50) NOT NULL;
ALTER TABLE contest ADD COLUMN started_at DATETIME(6) NOT NULL;

-- 5. 인덱스 생성
CREATE INDEX idx_event_participation_member_id ON event_participation(member_id);
CREATE INDEX idx_event_participation_event_id ON event_participation(event_id);
CREATE INDEX idx_contest_participation_member_id ON contest_participation(member_id);
CREATE INDEX idx_contest_participation_contest_id ON contest_participation(contest_id);
CREATE INDEX idx_event_started_at ON event(started_at);
CREATE INDEX idx_contest_started_at ON contest(started_at);
CREATE INDEX idx_event_status ON event(status);
CREATE INDEX idx_contest_status ON contest(status);
