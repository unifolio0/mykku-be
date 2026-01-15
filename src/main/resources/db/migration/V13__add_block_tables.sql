-- 사용자 차단 테이블
CREATE TABLE member_block (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id VARCHAR(255) NOT NULL,
    blocked_id VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (blocker_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE KEY uk_blocker_blocked (blocker_id, blocked_id),
    INDEX idx_blocker (blocker_id),
    INDEX idx_blocked (blocked_id)
);

-- 키워드 차단 테이블
CREATE TABLE keyword_block (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    keyword VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE KEY uk_member_keyword (member_id, keyword),
    INDEX idx_member (member_id)
);
