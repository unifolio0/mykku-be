-- V23: uk_member_activity (member_id, activity_type)의 leftmost-prefix가 member_id 단독 조회와
-- FK(ON DELETE CASCADE) 인덱스 요구를 모두 충족하므로, 중복되는 단일 컬럼 인덱스를 제거한다.

DROP INDEX idx_member_activity_member ON member_activity_count;
