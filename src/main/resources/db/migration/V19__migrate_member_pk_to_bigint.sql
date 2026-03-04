SET FOREIGN_KEY_CHECKS = 0;

-- 1. Drop all FK constraints referencing member(id)

-- feed (named in V16)
ALTER TABLE feed DROP FOREIGN KEY fk_feed_member;

-- feed_comment (named in V16)
ALTER TABLE feed_comment DROP FOREIGN KEY fk_feed_comment_member;

-- daily_message_comment (named in V16)
ALTER TABLE daily_message_comment DROP FOREIGN KEY fk_daily_message_comment_member;

-- save_feed (auto-named from V1)
ALTER TABLE save_feed DROP FOREIGN KEY save_feed_ibfk_1;

-- save_daily_message (auto-named from V1)
ALTER TABLE save_daily_message DROP FOREIGN KEY save_daily_message_ibfk_1;

-- like_feed (auto-named from V1)
ALTER TABLE like_feed DROP FOREIGN KEY like_feed_ibfk_1;

-- like_feed_comment (auto-named from V1)
ALTER TABLE like_feed_comment DROP FOREIGN KEY like_feed_comment_ibfk_1;

-- like_daily_message_comment (auto-named from V1)
ALTER TABLE like_daily_message_comment DROP FOREIGN KEY like_daily_message_comment_ibfk_1;

-- like_board (auto-named from V1)
ALTER TABLE like_board DROP FOREIGN KEY like_board_ibfk_1;

-- folder (named in V2)
ALTER TABLE folder DROP FOREIGN KEY fk_folder_member;

-- save_event (named in V2)
ALTER TABLE save_event DROP FOREIGN KEY fk_save_event_member;

-- save_fan_note (named in V2)
ALTER TABLE save_fan_note DROP FOREIGN KEY fk_save_fan_note_member;

-- save_contest (named in V9)
ALTER TABLE save_contest DROP FOREIGN KEY fk_save_contest_member;

-- member_genre_preference (named in V3)
ALTER TABLE member_genre_preference DROP FOREIGN KEY fk_genre_preference_member;

-- member_goods_preference (named in V3)
ALTER TABLE member_goods_preference DROP FOREIGN KEY fk_goods_preference_member;

-- member_mood_preference (named in V3)
ALTER TABLE member_mood_preference DROP FOREIGN KEY fk_mood_preference_member;

-- notification sender (named in V16)
ALTER TABLE notification DROP FOREIGN KEY fk_notification_sender;

-- notification receiver (auto-named from V5)
ALTER TABLE notification DROP FOREIGN KEY notification_ibfk_2;

-- fcm_token (auto-named from V5)
ALTER TABLE fcm_token DROP FOREIGN KEY fcm_token_ibfk_1;

-- notification_setting (auto-named from V5)
ALTER TABLE notification_setting DROP FOREIGN KEY notification_setting_ibfk_1;

-- member_role (auto-named from V8)
ALTER TABLE member_role DROP FOREIGN KEY member_role_ibfk_1;

-- member_block blocker (auto-named from V13)
ALTER TABLE member_block DROP FOREIGN KEY member_block_ibfk_1;

-- member_block blocked (auto-named from V13)
ALTER TABLE member_block DROP FOREIGN KEY member_block_ibfk_2;

-- keyword_block (auto-named from V13)
ALTER TABLE keyword_block DROP FOREIGN KEY keyword_block_ibfk_1;

-- event_participation (named in V16)
ALTER TABLE event_participation DROP FOREIGN KEY fk_event_participation_member;

-- contest_participation (named in V16)
ALTER TABLE contest_participation DROP FOREIGN KEY fk_contest_participation_member;

-- 2. Drop UNIQUE constraints that include member_id columns
ALTER TABLE save_feed DROP KEY unique_save_feed;
ALTER TABLE save_daily_message DROP KEY unique_save_daily_message;
ALTER TABLE like_feed DROP KEY unique_like_feed;
ALTER TABLE like_feed_comment DROP KEY unique_like_feed_comment;
ALTER TABLE like_daily_message_comment DROP KEY unique_like_daily_message_comment;
ALTER TABLE like_board DROP KEY unique_like_board;
ALTER TABLE folder DROP KEY uk_folder_member_name;
ALTER TABLE save_event DROP KEY uk_save_event_member_event;
ALTER TABLE save_fan_note DROP KEY uk_save_fan_note_member_fan_note;
ALTER TABLE save_contest DROP KEY uk_save_contest_member_contest;
ALTER TABLE member_genre_preference DROP KEY uk_genre_preference_member_type;
ALTER TABLE member_goods_preference DROP KEY uk_goods_preference_member_type;
ALTER TABLE member_mood_preference DROP KEY uk_mood_preference_member_type;
ALTER TABLE fcm_token DROP KEY uk_member_device;
ALTER TABLE notification_setting DROP KEY uk_member_type;
ALTER TABLE member_role DROP KEY unique_member_role;
ALTER TABLE member_block DROP KEY uk_blocker_blocked;
ALTER TABLE keyword_block DROP KEY uk_member_keyword;
ALTER TABLE event_participation DROP KEY uk_event_participation_member_event;
ALTER TABLE contest_participation DROP KEY uk_contest_participation_member_contest_feed;

-- 3. Drop indexes on member FK columns
DROP INDEX idx_feed_member_id ON feed;
DROP INDEX idx_feed_comment_member_id ON feed_comment;
DROP INDEX idx_daily_message_comment_member_id ON daily_message_comment;
DROP INDEX idx_save_contest_member_id ON save_contest;
DROP INDEX idx_save_event_member_id ON save_event;
DROP INDEX idx_save_fan_note_member_id ON save_fan_note;
DROP INDEX idx_folder_member_id ON folder;
DROP INDEX idx_event_participation_member_id ON event_participation;
DROP INDEX idx_contest_participation_member_id ON contest_participation;
DROP INDEX idx_member_role_member ON member_role;

-- 4. Change member PK from VARCHAR(255) to BIGINT AUTO_INCREMENT
ALTER TABLE member MODIFY COLUMN id BIGINT AUTO_INCREMENT;

-- 5. Change all FK columns from VARCHAR(255) to BIGINT
ALTER TABLE feed MODIFY COLUMN member_id BIGINT NULL;
ALTER TABLE feed_comment MODIFY COLUMN member_id BIGINT NULL;
ALTER TABLE daily_message_comment MODIFY COLUMN member_id BIGINT NULL;
ALTER TABLE save_feed MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE save_daily_message MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE like_feed MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE like_feed_comment MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE like_daily_message_comment MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE like_board MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE folder MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE save_event MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE save_fan_note MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE save_contest MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE member_genre_preference MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE member_goods_preference MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE member_mood_preference MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE notification MODIFY COLUMN sender_id BIGINT NULL;
ALTER TABLE notification MODIFY COLUMN receiver_id BIGINT NOT NULL;
ALTER TABLE fcm_token MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE notification_setting MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE member_role MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE member_block MODIFY COLUMN blocker_id BIGINT NOT NULL;
ALTER TABLE member_block MODIFY COLUMN blocked_id BIGINT NOT NULL;
ALTER TABLE keyword_block MODIFY COLUMN member_id BIGINT NOT NULL;
ALTER TABLE event_participation MODIFY COLUMN member_id BIGINT NULL;
ALTER TABLE contest_participation MODIFY COLUMN member_id BIGINT NULL;

-- 6. Recreate UNIQUE constraints
ALTER TABLE save_feed ADD UNIQUE KEY unique_save_feed (member_id, feed_id);
ALTER TABLE save_daily_message ADD UNIQUE KEY unique_save_daily_message (member_id, daily_message_id);
ALTER TABLE like_feed ADD UNIQUE KEY unique_like_feed (member_id, feed_id);
ALTER TABLE like_feed_comment ADD UNIQUE KEY unique_like_feed_comment (member_id, feed_comment_id);
ALTER TABLE like_daily_message_comment ADD UNIQUE KEY unique_like_daily_message_comment (member_id, daily_message_comment_id);
ALTER TABLE like_board ADD UNIQUE KEY unique_like_board (member_id, board_id);
ALTER TABLE folder ADD UNIQUE KEY uk_folder_member_name (member_id, name);
ALTER TABLE save_event ADD UNIQUE KEY uk_save_event_member_event (member_id, event_id);
ALTER TABLE save_fan_note ADD UNIQUE KEY uk_save_fan_note_member_fan_note (member_id, fan_note_id);
ALTER TABLE save_contest ADD UNIQUE KEY uk_save_contest_member_contest (member_id, contest_id);
ALTER TABLE member_genre_preference ADD UNIQUE KEY uk_genre_preference_member_type (member_id, genre_type);
ALTER TABLE member_goods_preference ADD UNIQUE KEY uk_goods_preference_member_type (member_id, goods_type);
ALTER TABLE member_mood_preference ADD UNIQUE KEY uk_mood_preference_member_type (member_id, mood_type);
ALTER TABLE fcm_token ADD UNIQUE KEY uk_member_device (member_id, device_id);
ALTER TABLE notification_setting ADD UNIQUE KEY uk_member_type (member_id, notification_type);
ALTER TABLE member_role ADD UNIQUE KEY unique_member_role (member_id, role_id);
ALTER TABLE member_block ADD UNIQUE KEY uk_blocker_blocked (blocker_id, blocked_id);
ALTER TABLE keyword_block ADD UNIQUE KEY uk_member_keyword (member_id, keyword);
ALTER TABLE event_participation ADD UNIQUE KEY uk_event_participation_member_event (member_id, event_id);
ALTER TABLE contest_participation ADD UNIQUE KEY uk_contest_participation_member_contest_feed (member_id, contest_id, feed_id);

-- 7. Recreate FK constraints
ALTER TABLE feed ADD CONSTRAINT fk_feed_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;
ALTER TABLE feed_comment ADD CONSTRAINT fk_feed_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;
ALTER TABLE daily_message_comment ADD CONSTRAINT fk_daily_message_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;
ALTER TABLE save_feed ADD CONSTRAINT fk_save_feed_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE save_daily_message ADD CONSTRAINT fk_save_daily_message_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE like_feed ADD CONSTRAINT fk_like_feed_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE like_feed_comment ADD CONSTRAINT fk_like_feed_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE like_daily_message_comment ADD CONSTRAINT fk_like_daily_message_comment_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE like_board ADD CONSTRAINT fk_like_board_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE folder ADD CONSTRAINT fk_folder_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE save_event ADD CONSTRAINT fk_save_event_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE save_fan_note ADD CONSTRAINT fk_save_fan_note_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE save_contest ADD CONSTRAINT fk_save_contest_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE member_genre_preference ADD CONSTRAINT fk_genre_preference_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE member_goods_preference ADD CONSTRAINT fk_goods_preference_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE member_mood_preference ADD CONSTRAINT fk_mood_preference_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE notification ADD CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES member(id) ON DELETE SET NULL;
ALTER TABLE notification ADD CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE fcm_token ADD CONSTRAINT fk_fcm_token_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE notification_setting ADD CONSTRAINT fk_notification_setting_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE member_role ADD CONSTRAINT fk_member_role_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE member_block ADD CONSTRAINT fk_member_block_blocker FOREIGN KEY (blocker_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE member_block ADD CONSTRAINT fk_member_block_blocked FOREIGN KEY (blocked_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE keyword_block ADD CONSTRAINT fk_keyword_block_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;
ALTER TABLE event_participation ADD CONSTRAINT fk_event_participation_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;
ALTER TABLE contest_participation ADD CONSTRAINT fk_contest_participation_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL;

-- 8. Recreate indexes
CREATE INDEX idx_feed_member_id ON feed(member_id);
CREATE INDEX idx_feed_comment_member_id ON feed_comment(member_id);
CREATE INDEX idx_daily_message_comment_member_id ON daily_message_comment(member_id);
CREATE INDEX idx_save_contest_member_id ON save_contest(member_id);
CREATE INDEX idx_save_event_member_id ON save_event(member_id);
CREATE INDEX idx_save_fan_note_member_id ON save_fan_note(member_id);
CREATE INDEX idx_folder_member_id ON folder(member_id);
CREATE INDEX idx_event_participation_member_id ON event_participation(member_id);
CREATE INDEX idx_contest_participation_member_id ON contest_participation(member_id);
CREATE INDEX idx_member_role_member ON member_role(member_id);

SET FOREIGN_KEY_CHECKS = 1;
