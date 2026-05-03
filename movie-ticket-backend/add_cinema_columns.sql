USE movie_ticket;

ALTER TABLE movie_schedule
ADD COLUMN cinema_id BIGINT(20) DEFAULT NULL AFTER movie_id,
ADD COLUMN cinema_name VARCHAR(100) DEFAULT NULL AFTER cinema_id;
