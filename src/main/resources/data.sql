-- Sample data for MYKKU Application

-- Insert sample members
INSERT INTO member (id, nickname, role, profile_image, provider, social_id, email, follower_count, following_count, created_at, updated_at) VALUES
('member1', '마이쿠유저1', 'USER', 'https://example.com/profile1.jpg', 'GOOGLE', 'google123', 'user1@example.com', 100, 50, NOW(), NOW()),
('member2', '마이쿠유저2', 'USER', 'https://example.com/profile2.jpg', 'GOOGLE', 'google456', 'user2@example.com', 200, 75, NOW(), NOW()),
('member3', '마이쿠유저3', 'USER', 'https://example.com/profile3.jpg', 'GOOGLE', 'google789', 'user3@example.com', 150, 80, NOW(), NOW()),
('member4', '마이쿠유저4', 'USER', 'https://example.com/profile4.jpg', 'GOOGLE', 'google321', 'admin@example.com', 500, 200, NOW(), NOW()),
('member5', '마이쿠유저5', 'USER', 'https://example.com/profile5.jpg', 'GOOGLE', 'google654', 'user5@example.com', 300, 120, NOW(), NOW());

-- Insert sample boards
INSERT INTO board (title, logo, created_at, updated_at) VALUES
('일상', 'https://example.com/logo_daily.png', NOW(), NOW()),
('여행', 'https://example.com/logo_travel.png', NOW(), NOW()),
('음식', 'https://example.com/logo_food.png', NOW(), NOW()),
('운동', 'https://example.com/logo_exercise.png', NOW(), NOW()),
('취미', 'https://example.com/logo_hobby.png', NOW(), NOW());

-- Tags are now stored directly in feed_tag and event_tag tables

-- Insert sample feeds
INSERT INTO feed (title, content, like_count, comment_count, board_id, member_id, created_at, updated_at) VALUES
('오늘의 일상', '오늘 하루도 수고 많았어요! 내일도 화이팅해요~', 25, 5, 1, 'member1', NOW(), NOW()),
('제주도 여행 후기', '제주도 3박 4일 여행 너무 좋았어요! 다음에 또 가고 싶네요.', 45, 8, 2, 'member2', NOW(), NOW()),
('맛있는 파스타 만들기', '집에서 크림파스타 만들어봤는데 생각보다 맛있게 나왔어요!', 30, 12, 3, 'member3', NOW(), NOW()),
('오늘의 홈트레이닝', '30분 홈트레이닝 완료! 오늘도 건강하게 마무리합니다.', 20, 3, 4, 'member4', NOW(), NOW()),
('새로운 취미 시작', '요즘 그림 그리기에 푹 빠져있어요. 아직 초보지만 재미있네요!', 35, 7, 5, 'member5', NOW(), NOW());

-- Insert sample feed images
INSERT INTO feed_image (url, width, height, feed_id, created_at, updated_at) VALUES
('https://example.com/daily1.jpg', 800, 600, 1, NOW(), NOW()),
('https://example.com/daily2.jpg', 1024, 768, 1, NOW(), NOW()),
('https://example.com/jeju1.jpg', 1200, 900, 2, NOW(), NOW()),
('https://example.com/jeju2.jpg', 800, 1200, 2, NOW(), NOW()),
('https://example.com/jeju3.jpg', 1600, 1200, 2, NOW(), NOW()),
('https://example.com/pasta1.jpg', 800, 600, 3, NOW(), NOW()),
('https://example.com/exercise1.jpg', 600, 800, 4, NOW(), NOW()),
('https://example.com/hobby1.jpg', 1000, 800, 5, NOW(), NOW()),
('https://example.com/hobby2.jpg', 800, 1000, 5, NOW(), NOW());

-- Insert sample feed tags
INSERT INTO feed_tag (feed_id, title, created_at, updated_at) VALUES
(1, '일상', NOW(), NOW()),
(1, '휴식', NOW(), NOW()),
(2, '여행', NOW(), NOW()),
(2, '사진', NOW(), NOW()),
(3, '맛집', NOW(), NOW()),
(3, '사진', NOW(), NOW()),
(4, '운동', NOW(), NOW()),
(5, '취미', NOW(), NOW()),
(5, '사진', NOW(), NOW());

-- Insert sample feed comments
INSERT INTO feed_comment (content, like_count, feed_id, parent_comment_id, member_id, created_at, updated_at) VALUES
('정말 멋진 하루였네요!', 5, 1, NULL, 'member2', NOW(), NOW()),
('저도 오늘 힘들었는데 위로가 되네요', 3, 1, NULL, 'member3', NOW(), NOW()),
('제주도 어디 갔었나요?', 2, 2, NULL, 'member1', NOW(), NOW()),
('성산일출봉이랑 한라산 다녀왔어요!', 4, 2, 3, 'member2', NOW(), NOW()),
('파스타 레시피 공유해주세요!', 8, 3, NULL, 'member1', NOW(), NOW()),
('홈트레이닝 어떤 걸 하시나요?', 1, 4, NULL, 'member5', NOW(), NOW()),
('그림 실력이 늘고 있는 것 같아요!', 6, 5, NULL, 'member1', NOW(), NOW());

-- Insert sample daily messages
INSERT INTO daily_message (title, content, date, created_at, updated_at) VALUES
('오늘의 한줄', '매일 조금씩 성장하는 나를 믿어요', CURDATE(), NOW(), NOW()),
('어제의 한줄', '작은 변화가 큰 차이를 만들어요', DATE_SUB(CURDATE(), INTERVAL 1 DAY), NOW(), NOW()),
('내일의 한줄', '새로운 하루, 새로운 기회가 기다려요', DATE_ADD(CURDATE(), INTERVAL 1 DAY), NOW(), NOW());

-- Insert sample daily message comments
INSERT INTO daily_message_comment (content, like_count, daily_message_id, parent_comment_id, member_id, created_at, updated_at) VALUES
('정말 좋은 말이네요. 감사합니다!', 10, 1, NULL, 'member1', NOW(), NOW()),
('오늘도 힘내서 해봐야겠어요', 5, 1, NULL, 'member2', NOW(), NOW()),
('매일 이런 메시지 보면서 동기부여 받고 있어요', 8, 2, NULL, 'member3', NOW(), NOW()),
('저도 그렇게 생각해요!', 3, 1, 1, 'member4', NOW(), NOW());

-- Insert sample follow relationships
INSERT INTO follow (follower_id, following_id) VALUES
('member1', 'member2'),
('member1', 'member3'),
('member1', 'member4'),
('member2', 'member1'),
('member2', 'member3'),
('member3', 'member1'),
('member3', 'member2'),
('member4', 'member1'),
('member5', 'member1'),
('member5', 'member2');

-- Insert sample saved feeds
INSERT INTO save_feed (member_id, feed_id, created_at, updated_at) VALUES
('member1', 2, NOW(), NOW()),
('member1', 3, NOW(), NOW()),
('member2', 1, NOW(), NOW()),
('member2', 4, NOW(), NOW()),
('member3', 1, NOW(), NOW()),
('member3', 5, NOW(), NOW());

-- Insert sample saved daily messages
INSERT INTO save_daily_message (member_id, daily_message_id, created_at, updated_at) VALUES
('member1', 1, NOW(), NOW()),
('member2', 1, NOW(), NOW()),
('member2', 2, NOW(), NOW()),
('member3', 2, NOW(), NOW());

-- Insert sample feed likes
INSERT INTO like_feed (member_id, feed_id, created_at, updated_at) VALUES
('member1', 2, NOW(), NOW()),
('member1', 3, NOW(), NOW()),
('member1', 5, NOW(), NOW()),
('member2', 1, NOW(), NOW()),
('member2', 3, NOW(), NOW()),
('member2', 4, NOW(), NOW()),
('member3', 1, NOW(), NOW()),
('member3', 2, NOW(), NOW()),
('member3', 5, NOW(), NOW()),
('member4', 1, NOW(), NOW()),
('member4', 2, NOW(), NOW()),
('member5', 1, NOW(), NOW()),
('member5', 3, NOW(), NOW());

-- Insert sample feed comment likes
INSERT INTO like_feed_comment (member_id, feed_comment_id, created_at, updated_at) VALUES
('member1', 1, NOW(), NOW()),
('member1', 4, NOW(), NOW()),
('member2', 2, NOW(), NOW()),
('member2', 5, NOW(), NOW()),
('member3', 1, NOW(), NOW()),
('member3', 7, NOW(), NOW()),
('member4', 3, NOW(), NOW()),
('member5', 6, NOW(), NOW());

-- Insert sample daily message comment likes
INSERT INTO like_daily_message_comment (member_id, daily_message_comment_id, created_at, updated_at) VALUES
('member1', 3, NOW(), NOW()),
('member2', 1, NOW(), NOW()),
('member2', 4, NOW(), NOW()),
('member3', 2, NOW(), NOW()),
('member4', 1, NOW(), NOW()),
('member5', 2, NOW(), NOW());

-- Insert sample board likes
INSERT INTO like_board (member_id, board_id) VALUES
('member1', 1),
('member1', 2),
('member2', 1),
('member2', 3),
('member3', 2),
('member3', 4),
('member4', 1),
('member4', 5),
('member5', 3),
('member5', 5);

-- Insert sample events
INSERT INTO event (title, is_contest, expired_at, created_at, updated_at) VALUES
('2024 여름 사진 콘테스트', TRUE, '2024-08-31 23:59:59', NOW(), NOW()),
('가을 여행기 공모전', TRUE, '2024-11-30 23:59:59', NOW(), NOW()),
('일반 이벤트', FALSE, '2024-12-31 23:59:59', NOW(), NOW());

-- Insert sample event images
INSERT INTO event_image (url, order_index, event_id, created_at, updated_at) VALUES
('https://example.com/contest_banner1.jpg', 1, 1, NOW(), NOW()),
('https://example.com/contest_banner2.jpg', 2, 1, NOW(), NOW()),
('https://example.com/travel_contest1.jpg', 1, 2, NOW(), NOW()),
('https://example.com/general_event1.jpg', 1, 3, NOW(), NOW());

-- Insert sample event tags
INSERT INTO event_tag (title, event_id, created_at, updated_at) VALUES
('사진', 1, NOW(), NOW()),
('콘테스트', 1, NOW(), NOW()),
('여름', 1, NOW(), NOW()),
('여행', 2, NOW(), NOW()),
('가을', 2, NOW(), NOW()),
('콘테스트', 2, NOW(), NOW()),
('이벤트', 3, NOW(), NOW()),
('일반', 3, NOW(), NOW());

-- Insert sample contest winners
INSERT INTO contest_winner (winner_rank, description, acceptance_speech, image, event_id, created_at, updated_at) VALUES
(1, '여름 바다 사진으로 1등 수상', '멋진 콘테스트에 참여할 수 있어서 영광이었습니다!', 'https://example.com/winner1.jpg', 1, NOW(), NOW()),
(2, '산 정상에서 찍은 일출 사진', '좋은 작품들과 함께 할 수 있어서 기뻤습니다.', 'https://example.com/winner2.jpg', 1, NOW(), NOW()),
(3, '도시 야경 사진으로 3등 수상', '다음에도 더 좋은 작품으로 참여하겠습니다!', 'https://example.com/winner3.jpg', 1, NOW(), NOW());
