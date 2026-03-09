-- 만화책 대여점 DB 스키마
-- MySQL 8.0+ 에서 실행
--
-- 로컬 실행 방법:
--   mysql -u root -p < schema.sql
--   또는 MySQL Workbench에서 이 파일 열어서 전체 실행
-- 적용 후 config.properties 의 db.url 에 DB 이름이 comic_rental 인지 확인하세용

CREATE DATABASE IF NOT EXISTS comic_rental
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE comic_rental;

-- 만화책
CREATE TABLE comic (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(200) NOT NULL,
  volume      INT NOT NULL DEFAULT 1,
  author      VARCHAR(100) NOT NULL,
  is_rented   TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0:대여가능, 1:대여중',
  reg_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 회원
CREATE TABLE member (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  phone       VARCHAR(50),
  reg_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 대여 내역
CREATE TABLE rental (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  comic_id    INT NOT NULL,
  member_id   INT NOT NULL,
  rental_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  return_date DATETIME NULL COMMENT 'NULL이면 미반납',
  CONSTRAINT fk_rental_comic  FOREIGN KEY (comic_id)  REFERENCES comic(id),
  CONSTRAINT fk_rental_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE INDEX idx_rental_comic_id  ON rental(comic_id);
CREATE INDEX idx_rental_member_id ON rental(member_id);
CREATE INDEX idx_rental_return_date ON rental(return_date);
