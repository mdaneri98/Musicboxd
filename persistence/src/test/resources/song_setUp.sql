/* Image */
INSERT INTO image (id, content) VALUES (100, CAST('deadbeef' AS BYTEA));

/* User */
INSERT INTO cuser (id, username, email, password, img_id) VALUES (200, 'DummyUser', 'Dummy@gmail.com', 'DummyPassword',100);

/* Review */
INSERT INTO review (id, user_id, title, description, rating) VALUES (400, 200, 'DummyTitle', 'DummyDescription', 3);

/* Artists */
INSERT INTO artist (id, name, img_id) VALUES (300,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (301,'Dummy',100);

/* Albums */
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (500, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 300);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (501, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);

/* Songs */
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (600, 'DummySong', '1:45', 1, 500);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (601, 'DummySong', '1:34', 2, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (602, 'DummySong', '2:17', 3, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (603, 'DummySong', '3:21', 4, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (604, 'DummySong', '2:33', 5, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (605, 'DummySong', '1:56', 6, 501);

/* Song-Artist */
INSERT INTO song_artist (song_id, artist_id) VALUES (600, 300);
INSERT INTO song_artist (song_id, artist_id) VALUES (600, 301);
INSERT INTO song_artist (song_id, artist_id) VALUES (601, 301);
INSERT INTO song_artist (song_id, artist_id) VALUES (602, 301);

/* Song Review */
INSERT INTO song_review (review_id, song_id) VALUES (400, 600);