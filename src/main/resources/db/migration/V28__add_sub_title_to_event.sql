-- V28: 이벤트 부제목 추가
ALTER TABLE event ADD COLUMN sub_title VARCHAR(255) NULL AFTER title;
