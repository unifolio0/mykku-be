-- V8: Add Role and MemberRole tables

-- 1. Create Role table (칭호 마스터)
CREATE TABLE role
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL
);

-- 2. Insert default roles
INSERT INTO role (name, description, created_at, updated_at)
VALUES ('신입 덕후', '처음 시작하는 덕후를 위한 칭호', NOW(6), NOW(6)),
       ('열정 덕후', '활발한 활동을 보이는 덕후', NOW(6), NOW(6)),
       ('베테랑 덕후', '오래된 경력의 덕후', NOW(6), NOW(6));

-- 3. Create MemberRole table (사용자 보유 칭호)
CREATE TABLE member_role
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  VARCHAR(255) NOT NULL,
    role_id    BIGINT       NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE RESTRICT,
    UNIQUE KEY unique_member_role (member_id, role_id)
);

CREATE INDEX idx_member_role_member ON member_role (member_id);

-- 4. Add role_id column to member table
ALTER TABLE member
    ADD COLUMN role_id BIGINT;

ALTER TABLE member
    ADD FOREIGN KEY (role_id) REFERENCES role (id);

-- 5. Assign default role to existing members
-- 5-1. Grant "신입 덕후" to all existing members (member_role)
INSERT INTO member_role (member_id, role_id, created_at, updated_at)
SELECT m.id, r.id, NOW(6), NOW(6)
FROM member m
         CROSS JOIN role r
WHERE r.name = '신입 덕후'
  AND NOT EXISTS (
        SELECT 1
        FROM member_role mr
        WHERE mr.member_id = m.id
          AND mr.role_id = r.id
    );

-- 5-2. Set representative role to "신입 덕후" for all existing members
UPDATE member m
    JOIN role r ON r.name = '신입 덕후'
SET m.role_id = r.id
WHERE m.role_id IS NULL;

-- 6. Make role_id NOT NULL
ALTER TABLE member
    MODIFY COLUMN role_id BIGINT NOT NULL;

-- 7. Drop old role column
ALTER TABLE member
    DROP COLUMN role;
