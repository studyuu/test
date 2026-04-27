-- 删除旧表（如果存在）
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS movie_comment;
DROP TABLE IF EXISTS movie_favorite;
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS movie_schedule;
DROP TABLE IF EXISTS hall;
DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS cinema;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

-- 创建影院表
CREATE TABLE cinema (
    cinema_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    cinema_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    logo VARCHAR(255) DEFAULT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    rating DOUBLE DEFAULT NULL,
    is_hot TINYINT(1) DEFAULT NULL,
    PRIMARY KEY (cinema_id),
    INDEX idx_status (status),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建放映厅表
CREATE TABLE hall (
    hall_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    cinema_id BIGINT(20) NOT NULL,
    hall_name VARCHAR(50) NOT NULL,
    row_count INT(11) NOT NULL,
    col_count INT(11) NOT NULL,
    seat_layout TEXT DEFAULT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (hall_id),
    INDEX idx_cinema_id (cinema_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建电影表
CREATE TABLE movie (
    movie_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    movie_name VARCHAR(255) NOT NULL,
    poster VARCHAR(255) DEFAULT NULL,
    movie_type VARCHAR(100) DEFAULT NULL,
    region VARCHAR(50) DEFAULT NULL,
    duration INT(11) DEFAULT NULL,
    director VARCHAR(255) DEFAULT NULL,
    actors TEXT DEFAULT NULL,
    synopsis TEXT DEFAULT NULL,
    release_date DATE DEFAULT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1: 上映中, 2: 即将上映, 3: 已下线',
    rating DOUBLE DEFAULT NULL,
    english_title VARCHAR(255) DEFAULT NULL,
    price DOUBLE DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (movie_id),
    INDEX idx_status (status),
    INDEX idx_release_date (release_date),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建电影排期表
CREATE TABLE movie_schedule (
    schedule_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    movie_id BIGINT(20) NOT NULL,
    hall_id BIGINT(20) NOT NULL,
    show_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (schedule_id),
    INDEX idx_movie_id (movie_id),
    INDEX idx_hall_id (hall_id),
    INDEX idx_show_time (show_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户表
CREATE TABLE sys_user (
    user_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    avatar VARCHAR(255) DEFAULT NULL,
    role VARCHAR(20) DEFAULT 'user',
    status TINYINT(1) NOT NULL DEFAULT 1,
    last_login_time DATETIME DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone),
    INDEX idx_status (status),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建角色表
CREATE TABLE sys_role (
    role_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    role_id BIGINT(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建权限表
CREATE TABLE sys_permission (
    permission_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    permission_name VARCHAR(50) NOT NULL,
    permission_code VARCHAR(100) NOT NULL,
    resource_type VARCHAR(20) NOT NULL,
    parent_id BIGINT(20) DEFAULT 0,
    path VARCHAR(255) DEFAULT NULL,
    component VARCHAR(255) DEFAULT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    sort_order INT(11) NOT NULL DEFAULT 0,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (permission_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建角色权限关联表
CREATE TABLE sys_role_permission (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    role_id BIGINT(20) NOT NULL,
    permission_id BIGINT(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建订单表
CREATE TABLE `order` (
    order_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    order_no VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    pay_type TINYINT(1) DEFAULT NULL,
    pay_time DATETIME DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建订单详情表
CREATE TABLE order_detail (
    detail_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    order_id BIGINT(20) NOT NULL,
    seat_id VARCHAR(20) NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (detail_id),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建电影评论表
CREATE TABLE movie_comment (
    comment_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    movie_id BIGINT(20) NOT NULL,
    content TEXT NOT NULL,
    rating TINYINT(1) NOT NULL,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (comment_id),
    INDEX idx_user_id (user_id),
    INDEX idx_movie_id (movie_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建电影收藏表
CREATE TABLE movie_favorite (
    favorite_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    movie_id BIGINT(20) NOT NULL,
    type TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_user_movie (user_id, movie_id),
    INDEX idx_user_id (user_id),
    INDEX idx_movie_id (movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建评论表
CREATE TABLE comment (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    movie_id BIGINT(20) NOT NULL,
    user_id BIGINT(20) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    avatar VARCHAR(255) DEFAULT NULL,
    rating INT(11) NOT NULL,
    content TEXT NOT NULL,
    images TEXT DEFAULT NULL,
    likes INT(11) NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_movie_id (movie_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建评论点赞表
CREATE TABLE comment_like (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    comment_id BIGINT(20) NOT NULL,
    user_id BIGINT(20) NOT NULL,
    liked TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_comment_id (comment_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_comment_user (comment_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建轮播图表
CREATE TABLE banner (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    image VARCHAR(255) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    movie_id BIGINT(20) DEFAULT NULL,
    order_num INT(11) NOT NULL DEFAULT 0,
    status TINYINT(1) NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_order_num (order_num),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;