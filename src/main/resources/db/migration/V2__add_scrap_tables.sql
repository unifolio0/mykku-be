-- Folder 테이블 생성
CREATE TABLE folder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_folder_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT uk_folder_member_name UNIQUE (member_id, name)
);

-- SaveFeed 테이블에 folder_id 컬럼 추가
ALTER TABLE save_feed
    ADD COLUMN folder_id BIGINT NULL,
    ADD CONSTRAINT fk_save_feed_folder FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE SET NULL;

-- SaveEvent 테이블 생성
CREATE TABLE save_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    event_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_save_event_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_save_event_event FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT uk_save_event_member_event UNIQUE (member_id, event_id)
);

-- SaveFanNote 테이블 생성
CREATE TABLE save_fan_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    fan_note_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_save_fan_note_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_save_fan_note_fan_note FOREIGN KEY (fan_note_id) REFERENCES fan_note(id) ON DELETE CASCADE,
    CONSTRAINT uk_save_fan_note_member_fan_note UNIQUE (member_id, fan_note_id)
);

-- 인덱스 생성
CREATE INDEX idx_folder_member_id ON folder(member_id);
CREATE INDEX idx_save_feed_folder_id ON save_feed(folder_id);
CREATE INDEX idx_save_event_member_id ON save_event(member_id);
CREATE INDEX idx_save_event_event_id ON save_event(event_id);
CREATE INDEX idx_save_fan_note_member_id ON save_fan_note(member_id);
CREATE INDEX idx_save_fan_note_fan_note_id ON save_fan_note(fan_note_id);
