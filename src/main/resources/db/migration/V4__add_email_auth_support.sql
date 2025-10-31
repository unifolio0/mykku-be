-- Add email authentication support to member table
ALTER TABLE member
    MODIFY COLUMN provider ENUM('GOOGLE', 'KAKAO', 'NAVER', 'APPLE', 'EMAIL') NULL,
    MODIFY COLUMN social_id VARCHAR(255) NULL,
    ADD COLUMN password VARCHAR(255) NULL,
    ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;

-- Create email verification code table
CREATE TABLE email_verification_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    purpose ENUM('SIGNUP', 'PASSWORD_RESET') NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    expires_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    INDEX idx_email_code (email, code),
    INDEX idx_expires_at (expires_at)
);
