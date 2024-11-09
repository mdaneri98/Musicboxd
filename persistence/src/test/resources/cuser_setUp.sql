/* Image */
INSERT INTO image (id, content) VALUES
    (100, CAST('beef' AS BYTEA)),
    (101, CAST('beef' AS BYTEA)),
    (102, CAST('beef' AS BYTEA)),
    (103, CAST('beef' AS BYTEA)),
    (104, CAST('beef' AS BYTEA)),
    (105, CAST('beef' AS BYTEA)),
    (106, CAST('beef' AS BYTEA)),
    (107, CAST('beef' AS BYTEA)),
    (108, CAST('beef' AS BYTEA)),
    (109, CAST('beef' AS BYTEA)),
    (110, CAST('beef' AS BYTEA)),
    (111, CAST('beef' AS BYTEA)),
    (112, CAST('beef' AS BYTEA)),
    (113, CAST('beef' AS BYTEA)),
    (114, CAST('beef' AS BYTEA)),
    (115, CAST('beef' AS BYTEA)),
    (116, CAST('beef' AS BYTEA)),
    (1000, CAST('beef' AS BYTEA));

/* Users */
INSERT INTO cuser (id, username, email, password, verified, moderator, img_id) VALUES
    (200,'Dummy', 'dummy@example.com', 'dummy123',false, false, 100),
    (201,'Dummy1', 'dummy1@example.com', 'dummy123', false, false, 101),
    (202,'Dummy2', 'dummy2@example.com', 'dummy123', false, false,102),
    (203,'Dummy3', 'dummy3@example.com', 'dummy123', false, false, 103),
    (204,'Dumy4', 'dummy4@example.com', 'dummy456', false, false,104);

/* User(id = 200) following User(id = 201) */
INSERT INTO follower (user_id, following) VALUES
    (200, 201);

/* User(id = 201) followings  */
INSERT INTO follower (user_id, following) VALUES
    (201, 202),
    (201, 203),
    (201, 204);

/* User(id = 200) followers */
INSERT INTO follower (user_id, following) VALUES
    (202, 200),
    (203, 204);

/* Artists */
INSERT INTO artist (id, name, bio, rating_amount, avg_rating, img_id) VALUES
    (300,'DummyName1', 'DummyBio',0, 0,105),
    (301,'DummyName2', 'DummyBio',0, 0,106),
    (302,'DummyName3', 'DummyBio',0, 0,107),
    (303,'DummyName4', 'DummyBio',0, 0,108),
    (304,'DummyName5', 'DummyBio',0, 0,109),
    (305,'DummyName6', 'DummyBio',0, 0,110);

/* Albums */
INSERT INTO album (id, title, genre, release_date, img_id, artist_id, rating_amount, avg_rating) VALUES
    (500, 'DummyTitle1', 'DummyGenre', '2000-10-10', 111, 300,0,0),
    (501, 'DummyTitle2', 'DummyGenre', '2000-10-10', 112, 301,0,0),
    (502, 'DummyTitle3', 'DummyGenre', '2000-10-10', 113, 301,0,0),
    (503, 'DummyTitle4', 'DummyGenre', '2000-10-10', 114, 301,0,0),
    (504, 'DummyTitle5', 'DummyGenre', '2000-10-10', 115, 301,0,0),
    (505, 'DummyTitle6', 'DummyGenre', '2000-10-10', 116, 301,0,0);

/* Songs */
INSERT INTO song (id, title, duration, track_number, album_id, rating_amount, avg_rating) VALUES
    (600, 'DummySong1', '1:45', 1, 500,0,0),
    (601, 'DummySong2', '1:45', 1, 501,0,0),
    (602, 'DummySong3', '1:45', 1, 501,0,0),
    (603, 'DummySong4', '1:45', 1, 501,0,0),
    (604, 'DummySong5', '1:45', 1, 501,0,0),
    (605, 'DummySong6', '1:45', 1, 501,0,0);

/* User(id = 200) favorite Artist(id = 300) */
INSERT INTO favorite_artist (user_id, artist_id) VALUES
    (200, 300);

/* User(id = 200) favorite Album(id = 500) */
INSERT INTO favorite_album (user_id, album_id) VALUES
    (200, 500);

/* User(id = 200) favorite Song(id = 600) */
INSERT INTO favorite_song (user_id, song_id) VALUES
    (200, 600);

/* User(id = 201) favorite Artists */
INSERT INTO favorite_artist (user_id, artist_id) VALUES
    (201, 301),
    (201, 302),
    (201, 303),
    (201, 304),
    (201, 305);

/* User(id = 201) favorite Albums */
INSERT INTO favorite_album (user_id, album_id) VALUES
    (201, 501),
    (201, 502),
    (201, 503),
    (201, 504),
    (201, 505);

/* User(id = 201) favorite Songs */
INSERT INTO favorite_song (user_id, song_id) VALUES
    (201, 601),
    (201, 602),
    (201, 603),
    (201, 604),
    (201, 605);

/* User(id = 200) reviews */
INSERT INTO review (id, user_id, title, description, rating, likes, created_at, isblocked) VALUES
    (400, 200, 'DummyTitle', 'DummyDescription', 3, 0, NOW(), false),
    (401, 200, 'DummyTitle', 'DummyDescription', 3, 2, NOW(), false),
    (402, 200, 'DummyTitle', 'DummyDescription', 3, 0, NOW(), true);

INSERT INTO artist_review (review_id, artist_id) VALUES
    (400, 300);

INSERT INTO album_review (review_id, album_id) VALUES
    (401, 500);

INSERT INTO song_review (review_id, song_id) VALUES
    (402, 600);