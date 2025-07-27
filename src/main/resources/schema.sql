-- MySQL Schema for MYKKU Application

-- Create Member table
CREATE TABLE IF NOT EXISTS member (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    nickname VARCHAR(10) NOT NULL,
    role VARCHAR(255) NOT NULL,
    profile_image VARCHAR(255) NOT NULL,
    provider ENUM('GOOGLE', 'KAKAO', 'NAVER', 'APPLE') NOT NULL,
    social_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    follower_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- Create Board table
CREATE TABLE IF NOT EXISTS board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(16) NOT NULL,
    logo VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- Create Feed table
CREATE TABLE IF NOT EXISTS feed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    board_id BIGINT NOT NULL,
    member_id VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (board_id) REFERENCES board(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- Create FeedImage table
CREATE TABLE IF NOT EXISTS feed_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    feed_id BIGINT NOT NULL,
    FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE
);

-- Create Tag table
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- Create FeedTag table (Many-to-Many relationship between Feed and Tag)
CREATE TABLE IF NOT EXISTS feed_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feed_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- Create FeedComment table
CREATE TABLE IF NOT EXISTS feed_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    like_count INT DEFAULT 0,
    feed_id BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    member_id VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES feed_comment(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- Create DailyMessage table
CREATE TABLE IF NOT EXISTS daily_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(42) NOT NULL,
    date DATE NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- Create DailyMessageComment table
CREATE TABLE IF NOT EXISTS daily_message_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    like_count INT DEFAULT 0,
    daily_message_id BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    member_id VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (daily_message_id) REFERENCES daily_message(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES daily_message_comment(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- Create Follow table
CREATE TABLE IF NOT EXISTS follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id VARCHAR(255) NOT NULL,
    following_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (follower_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE KEY unique_follow (follower_id, following_id)
);

-- Create SaveFeed table
CREATE TABLE IF NOT EXISTS save_feed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    feed_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE,
    UNIQUE KEY unique_save_feed (member_id, feed_id)
);

-- Create SaveDailyMessage table
CREATE TABLE IF NOT EXISTS save_daily_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    daily_message_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (daily_message_id) REFERENCES daily_message(id) ON DELETE CASCADE,
    UNIQUE KEY unique_save_daily_message (member_id, daily_message_id)
);

-- Create LikeFeed table
CREATE TABLE IF NOT EXISTS like_feed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    feed_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (feed_id) REFERENCES feed(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like_feed (member_id, feed_id)
);

-- Create LikeFeedComment table
CREATE TABLE IF NOT EXISTS like_feed_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    feed_comment_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (feed_comment_id) REFERENCES feed_comment(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like_feed_comment (member_id, feed_comment_id)
);

-- Create LikeDailyMessageComment table
CREATE TABLE IF NOT EXISTS like_daily_message_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    daily_message_comment_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (daily_message_comment_id) REFERENCES daily_message_comment(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like_daily_message_comment (member_id, daily_message_comment_id)
);

-- Create LikeBoard table
CREATE TABLE IF NOT EXISTS like_board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    board_id BIGINT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like_board (member_id, board_id)
);

-- Create Event table
CREATE TABLE IF NOT EXISTS event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    expired_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

-- Create EventImage table
CREATE TABLE IF NOT EXISTS event_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    event_id BIGINT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);

-- Create ContestWinner table
CREATE TABLE IF NOT EXISTS contest_winner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    winner_rank INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    acceptance_speech VARCHAR(255) NOT NULL,
    image VARCHAR(255) NOT NULL,
    event_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_feed_board_id ON feed(board_id);
CREATE INDEX idx_feed_member_id ON feed(member_id);
CREATE INDEX idx_feed_comment_feed_id ON feed_comment(feed_id);
CREATE INDEX idx_feed_comment_member_id ON feed_comment(member_id);
CREATE INDEX idx_daily_message_comment_daily_message_id ON daily_message_comment(daily_message_id);
CREATE INDEX idx_daily_message_comment_member_id ON daily_message_comment(member_id);
CREATE INDEX idx_follow_follower_id ON follow(follower_id);
CREATE INDEX idx_follow_following_id ON follow(following_id);
CREATE INDEX idx_daily_message_date ON daily_message(date);
