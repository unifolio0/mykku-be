-- 장르 취향 테이블
CREATE TABLE member_genre_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    genre_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_genre_preference_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT uk_genre_preference_member_type UNIQUE (member_id, genre_type)
);

-- 굿즈 취향 테이블
CREATE TABLE member_goods_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    goods_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_preference_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT uk_goods_preference_member_type UNIQUE (member_id, goods_type)
);

-- 분위기 취향 테이블
CREATE TABLE member_mood_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    mood_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mood_preference_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT uk_mood_preference_member_type UNIQUE (member_id, mood_type)
);

-- 인덱스 생성
CREATE INDEX idx_genre_preference_member ON member_genre_preference(member_id);
CREATE INDEX idx_goods_preference_member ON member_goods_preference(member_id);
CREATE INDEX idx_mood_preference_member ON member_mood_preference(member_id);
