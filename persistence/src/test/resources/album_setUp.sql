/* Image */
INSERT INTO image (id, content) VALUES (100, CAST('deadbeef' AS BYTEA));

/* User */
INSERT INTO cuser (id, username, email, password, img_id) VALUES (200, 'DummyUser', 'Dummy@gmail.com', 'DummyPassword',100);

/* Artists */
INSERT INTO artist (id, name, img_id) VALUES (300,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (301,'Dummy',100);

/* Review */
INSERT INTO review (id, user_id, title, description, rating) VALUES (400, 200, 'DummyTitle', 'DummyDescription', 3);

/* Albums */
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (500, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 300);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (501, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (502, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (503, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (504, 'DummyXTitle', 'DummyGenre', '2000-10-10', 100, 301);

/* Album Review */
INSERT INTO album_review (review_id, album_id) VALUES (400, 500);