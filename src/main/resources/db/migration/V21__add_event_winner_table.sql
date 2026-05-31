CREATE TABLE event_winner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    participation_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_event_winner_event
        FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_winner_participation
        FOREIGN KEY (participation_id) REFERENCES event_participation(id) ON DELETE CASCADE,
    CONSTRAINT uk_event_winner_participation UNIQUE (participation_id)
);
