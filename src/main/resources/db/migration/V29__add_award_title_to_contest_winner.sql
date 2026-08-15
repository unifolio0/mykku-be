-- V29: 콘테스트 수상 이름(최우수상 등) 추가
SET NAMES utf8mb4;

ALTER TABLE contest_winner ADD COLUMN award_title VARCHAR(50) NULL AFTER winner_rank;
