-- Notification 테이블
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    sender_id VARCHAR(255),
    receiver_id VARCHAR(255) NOT NULL,
    content VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    related_resource_id BIGINT,
    related_resource_type VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES member(id) ON DELETE CASCADE,
    INDEX idx_receiver_created (receiver_id, created_at DESC),
    INDEX idx_receiver_is_read (receiver_id, is_read)
);

-- FCM Token 테이블
CREATE TABLE fcm_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    device_id VARCHAR(100) NOT NULL,
    device_type VARCHAR(50),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE KEY uk_member_device (member_id, device_id),
    INDEX idx_member (member_id)
);

-- Notification Setting 테이블
CREATE TABLE notification_setting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE KEY uk_member_type (member_id, notification_type),
    INDEX idx_member (member_id)
);
