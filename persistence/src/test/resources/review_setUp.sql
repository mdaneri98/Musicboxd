
-- Insertar imágenes
INSERT INTO image (id, content) VALUES (1, CAST('deadbeef' AS BYTEA));
INSERT INTO image (id, content) VALUES (2, CAST('deadbeef' AS BYTEA));
INSERT INTO image (id, content) VALUES (3, CAST('deadbeef' AS BYTEA));

-- Insertar usuarios
INSERT INTO cuser (id, username, email, password, name, img_id, verified, moderator)
VALUES (1, 'user1', 'user1@example.com', 'password1', 'User One', 1, true, false);

INSERT INTO cuser (id, username, email, password, name, img_id, verified, moderator)
VALUES (2, 'user2', 'user2@example.com', 'password2', 'User Two', 1, true, false);

-- Insertar artistas
INSERT INTO artist (id, name, bio, img_id)
VALUES (1, 'Artist One', 'Bio for Artist One', 2);

-- Insertar álbumes
INSERT INTO album (id, title, genre, release_date, img_id, artist_id)
VALUES (1, 'Album One', 'Rock', '2023-01-01', 3, 1);

-- Insertar canciones
INSERT INTO song (id, title, duration, album_id)
VALUES (1, 'Song One', '3:30', 1);

-- Insertar reseñas
INSERT INTO review (id, user_id, title, description, rating, created_at, likes, isblocked)
VALUES (1, 1, 'Great Artist', 'This artist is amazing!', 5, '2023-06-01 10:00:00', 10, false);

INSERT INTO review (id, user_id, title, description, rating, created_at, likes, isblocked)
VALUES (2, 1, 'Awesome Album', 'This album rocks!', 4, '2023-06-02 11:00:00', 5, false);

INSERT INTO review (id, user_id, title, description, rating, created_at, likes, isblocked)
VALUES (3, 2, 'Nice Song', 'I love this song!', 5, '2023-06-03 12:00:00', 3, false);

-- Asociar reseñas
INSERT INTO artist_review (review_id, artist_id) VALUES (1, 1);
INSERT INTO album_review (review_id, album_id) VALUES (2, 1);
INSERT INTO song_review (review_id, song_id) VALUES (3, 1);

-- Insertar algunos "likes"
INSERT INTO review_like (user_id, review_id) VALUES (1, 2);
INSERT INTO review_like (user_id, review_id) VALUES (2, 1);