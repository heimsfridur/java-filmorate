DELETE FROM films cascade;
DELETE FROM users cascade;
DELETE FROM reviews cascade;

INSERT INTO films (film_name, film_description, film_releaseDate, film_duration, film_mpa)
VALUES ('Film One', 'Description of Film One', '2024-06-21', 120, 1),
       ('Film Two', 'Description of Film Two', '2024-06-22', 110, 2);

INSERT INTO users (user_email, user_login, user_name, user_birthday)
VALUES ('user1@example.com', 'user1', 'User One', '1990-01-01'),
       ('user2@example.com', 'user2', 'User Two', '1995-05-15'),
       ('user3@example.com', 'user3', 'User Three', '1991-05-15');

INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
VALUES ('Cool!', true, 1, 1, 0);