-- V26: 칭호 획득을 프론트 직접 호출 방식으로 전환하면서 활동 카운터 테이블 제거
-- 칭호(role) 시드는 V22에서 등록된 것을 그대로 유지합니다.

DROP TABLE IF EXISTS member_activity_count;
