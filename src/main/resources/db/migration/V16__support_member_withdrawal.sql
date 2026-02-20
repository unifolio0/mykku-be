-- V16: 회원 탈퇴 지원 - 콘텐츠 테이블 FK를 ON DELETE SET NULL로 변경

-- 1. feed: RESTRICT → SET NULL
ALTER TABLE feed DROP FOREIGN KEY feed_ibfk_2;
ALTER TABLE feed MODIFY COLUMN member_id VARCHAR(255) NULL;
ALTER TABLE feed ADD CONSTRAINT fk_feed_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;

-- 2. feed_comment: RESTRICT → SET NULL
ALTER TABLE feed_comment DROP FOREIGN KEY feed_comment_ibfk_3;
ALTER TABLE feed_comment MODIFY COLUMN member_id VARCHAR(255) NULL;
ALTER TABLE feed_comment ADD CONSTRAINT fk_feed_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;

-- 3. daily_message_comment: RESTRICT → SET NULL
ALTER TABLE daily_message_comment DROP FOREIGN KEY daily_message_comment_ibfk_3;
ALTER TABLE daily_message_comment MODIFY COLUMN member_id VARCHAR(255) NULL;
ALTER TABLE daily_message_comment ADD CONSTRAINT fk_daily_message_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;

-- 4. event_participation: CASCADE → SET NULL
ALTER TABLE event_participation DROP FOREIGN KEY fk_event_participation_member;
ALTER TABLE event_participation MODIFY COLUMN member_id VARCHAR(255) NULL;
ALTER TABLE event_participation ADD CONSTRAINT fk_event_participation_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;

-- 5. contest_participation: CASCADE → SET NULL
ALTER TABLE contest_participation DROP FOREIGN KEY fk_contest_participation_member;
ALTER TABLE contest_participation MODIFY COLUMN member_id VARCHAR(255) NULL;
ALTER TABLE contest_participation ADD CONSTRAINT fk_contest_participation_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;

-- 6. notification sender: CASCADE → SET NULL (sender_id는 이미 nullable)
ALTER TABLE notification DROP FOREIGN KEY notification_ibfk_1;
ALTER TABLE notification ADD CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES member(id) ON DELETE SET NULL;
