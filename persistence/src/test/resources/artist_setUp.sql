INSERT INTO image (id, content) VALUES (100, CAST('deadbeef' AS BYTEA));

INSERT INTO cuser (id, username, email, password, img_id) VALUES (200, 'DummyUser', 'Dummy@gmail.com', 'DummyPassword',100);

INSERT INTO artist (id, name, img_id) VALUES (300,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (301,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (302,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (303,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (304,'Dumy',100);

INSERT INTO review (id, user_id, title, description, rating) VALUES (400, 200, 'DummyTitle', 'DummyDescription', 3);

INSERT INTO artist_review (review_id, artist_id) VALUES (400, 300);

INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (500, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 300);

INSERT INTO song (id, title, duration, track_number, album_id) VALUES (600, 'DummySong', '1:45', 1, 500);

INSERT INTO song_artist (song_id, artist_id) VALUES (600, 300);
INSERT INTO song_artist (song_id, artist_id) VALUES (600, 301);