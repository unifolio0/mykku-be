-- Add participation_id column to contest_winner table
ALTER TABLE contest_winner ADD COLUMN participation_id BIGINT;

-- Add foreign key constraint
ALTER TABLE contest_winner ADD CONSTRAINT fk_contest_winner_participation
    FOREIGN KEY (participation_id) REFERENCES contest_participation(id) ON DELETE CASCADE;

-- Drop image column (no longer needed, using participation's feed image)
ALTER TABLE contest_winner DROP COLUMN image;

-- Make description and acceptance_speech nullable with default empty string
ALTER TABLE contest_winner MODIFY COLUMN description VARCHAR(255) DEFAULT '';
ALTER TABLE contest_winner MODIFY COLUMN acceptance_speech VARCHAR(1000) DEFAULT '';
