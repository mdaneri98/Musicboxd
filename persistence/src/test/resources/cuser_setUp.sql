/* Image */
INSERT INTO image (id, content) VALUES (100, CAST('deadbeef' AS BYTEA));

/* Users */
INSERT INTO cuser (id, username, email, password, img_id) VALUES (200,'Dummy', 'dummy@example.com', 'dummy123', 100); -- id = 1
INSERT INTO cuser (id, username, email, password, img_id) VALUES (201,'Dummy1', 'dummy1@example.com', 'dummy123', 100); -- id = 1
INSERT INTO cuser (id, username, email, password, img_id) VALUES (202,'Dummy2', 'dummy2@example.com', 'dummy123', 100); -- id = 1
INSERT INTO cuser (id, username, email, password, img_id) VALUES (203,'Dummy3', 'dummy3@example.com', 'dummy123', 100); -- id = 1
INSERT INTO cuser (id, username, email, password, img_id) VALUES (204,'Dumy4', 'dummy4@example.com', 'dummy456', 100); -- id = 1

/* User(id = 200) following User(id = 201) */
INSERT INTO follower (user_id, following) VALUES (200, 201);

/* User(id = 201) followings  */
INSERT INTO follower (user_id, following) VALUES (201, 202);
INSERT INTO follower (user_id, following) VALUES (201, 203);
INSERT INTO follower (user_id, following) VALUES (201, 204);

/* User(id = 200) followers */
INSERT INTO follower (user_id, following) VALUES (202, 200);
INSERT INTO follower (user_id, following) VALUES (203, 200);
INSERT INTO follower (user_id, following) VALUES (204, 200);

/* Artists */
INSERT INTO artist (id, name, img_id) VALUES (300,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (301,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (302,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (303,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (304,'Dummy',100);
INSERT INTO artist (id, name, img_id) VALUES (305,'Dummy',100);

/* Albums */
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (500, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 300);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (501, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (502, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (503, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (504, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);
INSERT INTO album (id, title, genre, release_date, img_id, artist_id) VALUES (505, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 301);

/* Songs */
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (600, 'DummySong', '1:45', 1, 500);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (601, 'DummySong', '1:45', 1, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (602, 'DummySong', '1:45', 1, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (603, 'DummySong', '1:45', 1, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (604, 'DummySong', '1:45', 1, 501);
INSERT INTO song (id, title, duration, track_number, album_id) VALUES (605, 'DummySong', '1:45', 1, 501);

/* User(id = 200) favorite Artist(id = 300) */
INSERT INTO favorite_artist (user_id, artist_id) VALUES (200, 300);

/* User(id = 200) favorite Album(id = 500) */
INSERT INTO favorite_album (user_id, album_id) VALUES (200, 500);

/* User(id = 200) favorite Song(id = 600) */
INSERT INTO favorite_song (user_id, song_id) VALUES (200, 600);

/* User(id = 201) favorite Artists */
INSERT INTO favorite_artist (user_id, artist_id) VALUES (201, 301);
INSERT INTO favorite_artist (user_id, artist_id) VALUES (201, 302);
INSERT INTO favorite_artist (user_id, artist_id) VALUES (201, 303);
INSERT INTO favorite_artist (user_id, artist_id) VALUES (201, 304);
INSERT INTO favorite_artist (user_id, artist_id) VALUES (201, 305);

/* User(id = 201) favorite Albums */
INSERT INTO favorite_album (user_id, album_id) VALUES (201, 501);
INSERT INTO favorite_album (user_id, album_id) VALUES (201, 502);
INSERT INTO favorite_album (user_id, album_id) VALUES (201, 503);
INSERT INTO favorite_album (user_id, album_id) VALUES (201, 504);
INSERT INTO favorite_album (user_id, album_id) VALUES (201, 505);

/* User(id = 201) favorite Songs */
INSERT INTO favorite_song (user_id, song_id) VALUES (201, 601);
INSERT INTO favorite_song (user_id, song_id) VALUES (201, 602);
INSERT INTO favorite_song (user_id, song_id) VALUES (201, 603);
INSERT INTO favorite_song (user_id, song_id) VALUES (201, 604);
INSERT INTO favorite_song (user_id, song_id) VALUES (201, 605);

/* User(id = 200) reviews */
INSERT INTO review (id, user_id, title, description, rating) VALUES (400, 200, 'DummyTitle', 'DummyDescription', 3);
INSERT INTO review (id, user_id, title, description, rating) VALUES (401, 200, 'DummyTitle', 'DummyDescription', 3);
INSERT INTO review (id, user_id, title, description, rating) VALUES (402, 200, 'DummyTitle', 'DummyDescription', 3);

INSERT INTO artist_review (review_id, artist_id) VALUES (400, 300);
INSERT INTO album_review (review_id, album_id) VALUES (401, 500);
INSERT INTO song_review (review_id, song_id) VALUES (402, 600);