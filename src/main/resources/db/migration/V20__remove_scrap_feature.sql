-- Remove scrap-related tables
DROP TABLE IF EXISTS save_contest;
DROP TABLE IF EXISTS save_fan_note;
DROP TABLE IF EXISTS save_event;
DROP TABLE IF EXISTS save_feed;
DROP TABLE IF EXISTS save_daily_message;
DROP TABLE IF EXISTS folder;

-- Remove scrap_count columns
ALTER TABLE event DROP COLUMN IF EXISTS scrap_count;
ALTER TABLE contest DROP COLUMN IF EXISTS scrap_count;
