-- Remove follower/following count columns from member table
ALTER TABLE member DROP COLUMN follower_count;
ALTER TABLE member DROP COLUMN following_count;

-- Remove Follow table
DROP TABLE IF EXISTS follow;
