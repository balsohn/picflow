-- Picflow 데이터베이스 생성 및 초기 설정
-- MySQL 8.0+ 기준으로 작성
DROP DATABASE picflow

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS picflow
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 사용
USE picflow;

-- 1. 사용자 테이블 (users)
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       username VARCHAR(10) NOT NULL,
                       profile_image TEXT,
                       bio TEXT,
                       deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       INDEX idx_email (email),
                       INDEX idx_username (username),
                       INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 게시물 테이블 (posts)
CREATE TABLE posts (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       title VARCHAR(100) NOT NULL,
                       content TEXT NOT NULL,
                       image_url TEXT,
                       user_id BIGINT NOT NULL,
                       like_count INT NOT NULL DEFAULT 0,
                       comment_count INT NOT NULL DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       INDEX idx_user_id (user_id),
                       INDEX idx_created_at (created_at),
                       INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 팔로우 테이블 (follows)
CREATE TABLE follows (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         follower_id BIGINT NOT NULL,
                         following_id BIGINT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (id),
                         FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
                         UNIQUE KEY uk_follower_following (follower_id, following_id),
                         INDEX idx_follower_id (follower_id),
                         INDEX idx_following_id (following_id),
                         CONSTRAINT chk_no_self_follow CHECK (follower_id != following_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 댓글 테이블 (comments)
CREATE TABLE comments (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          content TEXT NOT NULL,
                          post_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          INDEX idx_post_id (post_id),
                          INDEX idx_user_id (user_id),
                          INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 좋아요 테이블 (likes)
CREATE TABLE likes (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       post_id BIGINT NOT NULL,
                       user_id BIGINT NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       UNIQUE KEY uk_post_user (post_id, user_id),
                       INDEX idx_post_id (post_id),
                       INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 좋아요 수 업데이트 트리거 (게시물에 좋아요 추가 시)
DELIMITER //

CREATE TRIGGER tr_likes_insert_update_count
    AFTER INSERT ON likes
    FOR EACH ROW
BEGIN
    UPDATE posts
    SET like_count = like_count + 1
    WHERE id = NEW.post_id;
END //

CREATE TRIGGER tr_likes_delete_update_count
    AFTER DELETE ON likes
    FOR EACH ROW
BEGIN
    UPDATE posts
    SET like_count = like_count - 1
    WHERE id = OLD.post_id;
END //

-- 7. 댓글 수 업데이트 트리거 (댓글 추가/삭제 시)
CREATE TRIGGER tr_comments_insert_update_count
    AFTER INSERT ON comments
    FOR EACH ROW
BEGIN
    UPDATE posts
    SET comment_count = comment_count + 1
    WHERE id = NEW.post_id;
END //

CREATE TRIGGER tr_comments_delete_update_count
    AFTER DELETE ON comments
    FOR EACH ROW
BEGIN
    UPDATE posts
    SET comment_count = comment_count - 1
    WHERE id = OLD.post_id;
END //

DELIMITER ;

-- 8. 샘플 데이터 삽입 (테스트용)
-- 비밀번호는 모두 "Password123!" (BCrypt 암호화 필요)

-- 샘플 사용자들
INSERT INTO users (email, password, username, bio) VALUES
                                                       ('admin@picflow.com', '12341234#', 'admin', 'Picflow 관리자입니다.'),
                                                       ('alice@example.com', '$2a$10$N8Rt.Qb0QLtfKZRhG2CzxuQ7Vj5kP8m1B4xKj3L9vWnEf2DcS5IqG', 'alice', '안녕하세요! 사진 찍는 것을 좋아해요 📸'),
                                                       ('bob@example.com', '$2a$10$N8Rt.Qb0QLtfKZRhG2CzxuQ7Vj5kP8m1B4xKj3L9vWnEf2DcS5IqG', 'bob', '개발자입니다. 코딩과 커피를 사랑해요 ☕'),
                                                       ('charlie@example.com', '$2a$10$N8Rt.Qb0QLtfKZRhG2CzxuQ7Vj5kP8m1B4xKj3L9vWnEf2DcS5IqG', 'charlie', '여행을 좋아하는 자유로운 영혼 ✈️'),
                                                       ('diana@example.com', '$2a$10$N8Rt.Qb0QLtfKZRhG2CzxuQ7Vj5kP8m1B4xKj3L9vWnEf2DcS5IqG', 'diana', '디자이너입니다. 아름다운 것들을 만들어요 🎨');

-- 샘플 팔로우 관계
INSERT INTO follows (follower_id, following_id) VALUES
                                                    (2, 3), -- alice가 bob을 팔로우
                                                    (2, 4), -- alice가 charlie를 팔로우
                                                    (3, 2), -- bob이 alice를 팔로우
                                                    (3, 5), -- bob이 diana를 팔로우a
                                                    (4, 2), -- charlie가 alice를 팔로우
                                                    (4, 5), -- charlie가 diana를 팔로우
                                                    (5, 2), -- diana가 alice를 팔로우
                                                    (5, 3); -- diana가 bob을 팔로우

-- 샘플 게시물들
INSERT INTO posts (title, content, user_id) VALUES
                                                ('Picflow에 오신 것을 환영합니다!', 'Picflow는 사진이 흐르는 아름다운 소셜 플랫폼입니다. 여러분의 소중한 순간들을 공유해보세요!', 1),
                                                ('오늘의 커피 한잔 ☕', '새로운 카페에서 맛있는 라떼를 마셨어요. 라떼아트가 정말 예뻐서 사진으로 남겼답니다!', 2),
                                                ('새 프로젝트 시작!', '오늘부터 새로운 웹 개발 프로젝트를 시작합니다. Spring Boot와 React를 사용해서 멋진 애플리케이션을 만들어볼 예정이에요.', 3),
                                                ('부산 여행 후기', '지난 주말에 부산을 다녀왔어요! 해운대 바다가 정말 아름다웠고, 맛있는 해산물도 많이 먹었답니다. 다음에는 더 오래 머물고 싶어요.', 4),
                                                ('새로운 디자인 작업', '클라이언트를 위한 브랜딩 작업을 완료했어요. 깔끔하고 모던한 느낌으로 디자인했는데 어떠신가요?', 5),
                                                ('아침 운동 완료!', '오늘도 아침 6시에 일어나서 조깅을 다녀왔어요. 상쾌한 아침 공기를 마시며 뛰니까 하루가 활기차게 시작되네요!', 2),
                                                ('독서 추천', '최근에 읽은 "클린 코드" 정말 좋았어요. 개발자라면 꼭 한번 읽어보시길 추천합니다!', 3);

-- 샘플 댓글들
INSERT INTO comments (content, post_id, user_id) VALUES
                                                     ('정말 멋진 플랫폼이네요! 잘 사용하겠습니다.', 1, 2),
                                                     ('관리자님 고생 많으셨어요!', 1, 3),
                                                     ('라떼아트 정말 예쁘네요! 어느 카페인지 알려주세요~', 2, 3),
                                                     ('저도 그 카페 가보고 싶어요!', 2, 4),
                                                     ('Spring Boot 좋죠! 저도 요즘 공부하고 있어요.', 3, 2),
                                                     ('React도 함께 배우고 계시는군요. 화이팅!', 3, 5),
                                                     ('부산 여행 사진 더 보여주세요!', 4, 2),
                                                     ('해운대 정말 좋죠. 저도 자주 가요.', 4, 5),
                                                     ('디자인 센스가 정말 좋으시네요!', 5, 2),
                                                     ('저희 회사에서도 브랜딩 작업 부탁드리고 싶어요.', 5, 3),
                                                     ('아침 운동 대단하세요! 저는 못 일어나겠더라고요 ㅠㅠ', 6, 4),
                                                     ('클린 코드 저도 읽었는데 정말 좋은 책이죠!', 7, 2);

-- 샘플 좋아요들
INSERT INTO likes (post_id, user_id) VALUES
                                         (1, 2), (1, 3), (1, 4), (1, 5), -- 첫 번째 게시물에 모든 사용자가 좋아요
                                         (2, 3), (2, 4), (2, 5), -- 두 번째 게시물
                                         (3, 2), (3, 5), -- 세 번째 게시물
                                         (4, 2), (4, 3), (4, 5), -- 네 번째 게시물
                                         (5, 2), (5, 3), (5, 4), -- 다섯 번째 게시물
                                         (6, 3), (6, 4), -- 여섯 번째 게시물
                                         (7, 2), (7, 5); -- 일곱 번째 게시물

-- 9. 유용한 뷰(View) 생성

-- 사용자 통계 뷰
CREATE VIEW user_stats AS
SELECT
    u.id,
    u.username,
    u.email,
    COUNT(DISTINCT p.id) as post_count,
    COUNT(DISTINCT f1.id) as following_count,
    COUNT(DISTINCT f2.id) as follower_count,
    COUNT(DISTINCT l.id) as like_count
FROM users u
         LEFT JOIN posts p ON u.id = p.user_id
         LEFT JOIN follows f1 ON u.id = f1.follower_id
         LEFT JOIN follows f2 ON u.id = f2.following_id
         LEFT JOIN likes l ON u.id = l.user_id
WHERE u.deleted = FALSE
GROUP BY u.id, u.username, u.email;

-- 인기 게시물 뷰 (좋아요 순)
CREATE VIEW popular_posts AS
SELECT
    p.id,
    p.title,
    p.content,
    p.image_url,
    u.username as author,
    p.like_count,
    p.comment_count,
    p.created_at
FROM posts p
         JOIN users u ON p.user_id = u.id
WHERE u.deleted = FALSE
ORDER BY p.like_count DESC, p.created_at DESC;

-- 10. 인덱스 최적화 (추가 성능 향상용)

-- 복합 인덱스들
CREATE INDEX idx_posts_user_created ON posts(user_id, created_at DESC);
CREATE INDEX idx_comments_post_created ON comments(post_id, created_at DESC);
CREATE INDEX idx_likes_user_created ON likes(user_id, created_at DESC);
CREATE INDEX idx_follows_follower_created ON follows(follower_id, created_at DESC);
CREATE INDEX idx_follows_following_created ON follows(following_id, created_at DESC);

-- 전문 검색을 위한 인덱스 (MySQL 5.7+)
CREATE FULLTEXT INDEX idx_posts_title_content ON posts(title, content);
CREATE FULLTEXT INDEX idx_users_username ON users(username);

-- 11. 저장 프로시저 (선택사항)

DELIMITER //

-- 사용자의 뉴스피드 가져오기
CREATE PROCEDURE GetNewsFeed(
    IN user_id BIGINT,
    IN page_offset INT,
    IN page_size INT
)
BEGIN
SELECT
    p.id,
    p.title,
    p.content,
    p.image_url,
    u.username as author,
    u.id as author_id,
    p.like_count,
    p.comment_count,
    p.created_at,
    p.updated_at
FROM posts p
         JOIN users u ON p.user_id = u.id
         JOIN follows f ON p.user_id = f.following_id
WHERE f.follower_id = user_id
  AND u.deleted = FALSE
ORDER BY p.created_at DESC
    LIMIT page_offset, page_size;
END //

-- 사용자 팔로우 추천 (공통 팔로잉이 많은 사용자)
CREATE PROCEDURE GetFollowRecommendations(
    IN user_id BIGINT,
    IN limit_count INT
)
BEGIN
SELECT
    u.id,
    u.username,
    u.bio,
    COUNT(f2.follower_id) as mutual_follows
FROM users u
         JOIN follows f1 ON u.id = f1.following_id
         JOIN follows f2 ON f1.follower_id = f2.follower_id
         JOIN follows f3 ON f2.following_id = f3.following_id
WHERE f3.follower_id = user_id
  AND u.id != user_id
        AND u.deleted = FALSE
        AND NOT EXISTS (
            SELECT 1 FROM follows f4
            WHERE f4.follower_id = user_id AND f4.following_id = u.id
        )
GROUP BY u.id, u.username, u.bio
ORDER BY mutual_follows DESC
    LIMIT limit_count;
END //

DELIMITER ;

-- 12. 관리용 쿼리들

-- 전체 통계 조회
SELECT
    'Users' as entity,
    COUNT(*) as total_count,
    COUNT(CASE WHEN deleted = FALSE THEN 1 END) as active_count
FROM users
UNION ALL
SELECT
    'Posts' as entity,
    COUNT(*) as total_count,
    COUNT(*) as active_count
FROM posts
UNION ALL
SELECT
    'Comments' as entity,
    COUNT(*) as total_count,
    COUNT(*) as active_count
FROM comments
UNION ALL
SELECT
    'Likes' as entity,
    COUNT(*) as total_count,
    COUNT(*) as active_count
FROM likes
UNION ALL
SELECT
    'Follows' as entity,
    COUNT(*) as total_count,
    COUNT(*) as active_count
FROM follows;

-- 데이터베이스 설정 완료 메시지
SELECT 'Picflow 데이터베이스가 성공적으로 생성되었습니다!' as message;