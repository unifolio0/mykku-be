-- V22: 회원×활동유형 누적 카운터 테이블 + 19개 칭호(role) 시드
-- (이벤트 참여 3종, 북마크 3종은 이번 범위에서 제외)

SET NAMES utf8mb4;

CREATE TABLE member_activity_count
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id     BIGINT      NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    count         BIGINT      NOT NULL DEFAULT 0,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT uk_member_activity UNIQUE (member_id, activity_type),
    CONSTRAINT fk_member_activity_count_member
        FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE INDEX idx_member_activity_member ON member_activity_count (member_id);

INSERT IGNORE INTO role (name, description, created_at, updated_at) VALUES
  ('첫 만남', '로그인 하면 무조건 줌', NOW(6), NOW(6)),
  ('처음의 설레임', '콘테스트 1회 참여', NOW(6), NOW(6)),
  ('적극적인 덕후', '콘테스트 5회 참여', NOW(6), NOW(6)),
  ('마이꾸 고인물', '콘테스트 10회 참여', NOW(6), NOW(6)),
  ('초보 오타쿠', '덕질노트 1회 열람', NOW(6), NOW(6)),
  ('중수 오타쿠', '덕질노트 15회 열람', NOW(6), NOW(6)),
  ('고수 오타쿠', '덕질노트 30회 열람', NOW(6), NOW(6)),
  ('행운은 나의 것!', '하루덕담 1회 열람', NOW(6), NOW(6)),
  ('덕질의 가호', '하루덕담 15회 열람', NOW(6), NOW(6)),
  ('콘텐츠 섭렵', '하루덕담 30회 열람', NOW(6), NOW(6)),
  ('이 몸 등장', '게시글 1회 업로드', NOW(6), NOW(6)),
  ('영역전개', '게시글 5회 업로드', NOW(6), NOW(6)),
  ('무한 기록자', '게시글 10회 업로드', NOW(6), NOW(6)),
  ('안녕하세요!', '댓글 1회 작성', NOW(6), NOW(6)),
  ('리액션천재', '댓글 5회 작성', NOW(6), NOW(6)),
  ('너 내 동료가 돼라', '댓글 10회 작성', NOW(6), NOW(6)),
  ('여름이었다', '좋아요 1회 누르기', NOW(6), NOW(6)),
  ('찍먹 천재', '좋아요 15회 누르기', NOW(6), NOW(6)),
  ('사랑하는게 너무 많아', '좋아요 30회 누르기', NOW(6), NOW(6));
