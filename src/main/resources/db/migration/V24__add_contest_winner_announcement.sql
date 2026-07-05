CREATE TABLE contest_winner_announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contest_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    announced_at DATE NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_contest_winner_announcement_contest
        FOREIGN KEY (contest_id) REFERENCES contest(id) ON DELETE CASCADE,
    CONSTRAINT uk_contest_winner_announcement_contest UNIQUE (contest_id)
);
