INSERT INTO user_entity (
    id, username, password, phone_number, student_id, email, role, position, is_staff,
    created_date, modified_date
) VALUES
      (1, 'admin01', '$2a$10$Un2PhzrfgQJrvuTnrZ846en1.w80e6J47GX7pkrdVHaTcUjlP9Giq', '01000000001', '2025000001', 'admin01@gmail.com', 'ROLE_ADMIN', '관리자', false, NOW(), NOW()),
      (2, 'comit01', '$2a$10$N2ibIoZ.edxpKAOY0cwibuqhkHEfxutZ/1BK1jxPO1l9FjjW/dcy6', '01010000001', '2026000001', 'comit01@gmail.com', 'ROLE_VERIFIED', '일반부원', false, NOW(), NOW()),
      (3, 'comit02', '$2a$10$N2ibIoZ.edxpKAOY0cwibuqhkHEfxutZ/1BK1jxPO1l9FjjW/dcy6', '01010000002', '2026000002', 'comit02@gmail.com', 'ROLE_VERIFIED', '운영진', true, NOW(), NOW()),
      (4, 'comit03', '$2a$10$N2ibIoZ.edxpKAOY0cwibuqhkHEfxutZ/1BK1jxPO1l9FjjW/dcy6', '01010000003', '2026000003', 'comit03@gmail.com', 'ROLE_VERIFIED', '일반부원', false, NOW(), NOW());

-- Study 데이터
INSERT INTO study_entity (
    id, title, image_src, start_time, end_time, description, is_recruiting, semester, year, tags,
    created_date, modified_date, user_id, day, level, campus
) VALUES
      (1, '스프링1', 'study1.jpg', '09:00', '16:00', '아오 힘들어', true, 'Spring', 2024, '["java", "spring", "jpa"]', NOW(), NOW(), 2, '월', '중급', '율전'),
      (2, '스프링2', 'study2.jpg', '09:00', '16:00', '아오 힘들어', true, 'Spring', 2024, '["java", "spring", "jpa"]', NOW(), NOW(), 3, '토', '중급', '율전');

-- Created Study 데이터
INSERT INTO created_study_entity (
    id, user_id, study_id, is_leader, state
) VALUES
      (1, 2, 1, true, 'Accept'),  -- comit01이 스프링1 스터디 생성
      (2, 3, 2, true, 'Reject');  -- comit02가 스프링2 스터디 생성

-- Event 데이터
INSERT INTO event_entity (
    id, title, image_src, start_time, end_time, description, is_recruiting, semester, year, tags,
    created_date, modified_date, location
) VALUES
      (1, 'COMIT 해커톤', 'event1.jpg', '10:00', '18:00', 'COMIT 연합 해커톤', true, 'Spring', 2024, '["Hackathon", "Programming"]', NOW(), NOW(), '성균관대학교 수원캠퍼스'),
      (2, 'COMIT 정기 세미나', 'event2.jpg', '14:00', '16:00', '월간 정기 세미나', false, 'Spring', 2024, '["Seminar", "Tech"]', NOW(), NOW(), '성균관대학교 서울캠퍼스');

-- Post 데이터
INSERT INTO post_entity (
    id, group_id, group_type, user_id, title, content, image_src, like_count, created_date, modified_date
) VALUES
      (1, 1, 'STUDY', 2, 'Spring Boot 스터디 1주차 정리', '오늘은 환경 세팅 공부했습니다.', 'post1.jpg', 5, NOW(), NOW()),
      (2, 1, 'EVENT', 3, '해커톤 참가 신청', 'COMIT 해커톤에 참가하고 싶으신 분들...', 'post2.jpg', 10, NOW(), NOW());

-- Comment 데이터
INSERT INTO comment_entity (
    id, post_id, user_id, content, created_date, modified_date
) VALUES
      (1, 1, 3, '저도 참여하고 싶습니다!', NOW(), NOW()),
      (2, 1, 4, '다음 모임은 언제인가요?', NOW(), NOW()),
      (3, 2, 2, '참가 신청했습니다!', NOW(), NOW());
