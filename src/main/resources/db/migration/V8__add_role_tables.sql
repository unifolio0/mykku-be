-- V8: Add Role and MemberRole tables

CREATE TABLE role
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL
);

CREATE TABLE member_role
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  VARCHAR(255) NOT NULL,
    role_id    BIGINT       NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role (id),
    UNIQUE KEY unique_member_role (member_id, role_id)
);

CREATE INDEX idx_member_role_member ON member_role (member_id);

ALTER TABLE member
    ADD COLUMN role_id BIGINT;

ALTER TABLE member
    ADD FOREIGN KEY (role_id) REFERENCES role (id);

ALTER TABLE member
    DROP COLUMN role;
