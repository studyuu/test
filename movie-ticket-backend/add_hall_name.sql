USE movie_ticket;

ALTER TABLE movie_schedule
ADD COLUMN hall_name VARCHAR(50) DEFAULT NULL AFTER hall_id;
