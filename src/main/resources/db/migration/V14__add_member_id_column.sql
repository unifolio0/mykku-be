-- member 테이블에 member_id 컬럼 추가
ALTER TABLE member ADD COLUMN member_id VARCHAR(16) NOT NULL UNIQUE;

-- 인덱스 추가
CREATE INDEX idx_member_member_id ON member(member_id);
