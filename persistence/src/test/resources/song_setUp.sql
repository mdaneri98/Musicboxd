/* Image */
INSERT INTO image (id, content) VALUES
    (101, CAST('beef' AS BYTEA)),
    (102, CAST('beef' AS BYTEA)),
    (103, CAST('beef' AS BYTEA)),
    (104, CAST('beef' AS BYTEA)),
    (105, CAST('beef' AS BYTEA));

/* User */
INSERT INTO cuser (id, username, email, password, verified, moderator, img_id) VALUES
    (200, 'DummyUser', 'Dummy@gmail.com', 'DummyPassword',false, false, 101);

/* Review */
INSERT INTO review (id, user_id, title, description, rating, likes, created_at, isblocked) VALUES
    (400, 200, 'DummyTitle', 'DummyDescription', 3, 0, NOW(), false);

/* Artists */
INSERT INTO artist (id, name, bio, rating_amount, avg_rating, img_id) VALUES
    (300,'DummyName', 'DummyBio',0, 0, 102),
    (301,'DummyName', 'DummyBio',0, 0, 103);

/* Albums */
INSERT INTO album (id, title, genre, release_date, img_id, artist_id, rating_amount, avg_rating) VALUES
    (500, 'DummyTitle', 'DummyGenre', '2000-10-10', 104, 300,0,0),
    (501, 'DummyTitle', 'DummyGenre', '2000-10-10', 105, 301,0,0);

/* Songs */
INSERT INTO song (id, title, duration, track_number, album_id, rating_amount, avg_rating) VALUES
    (600, 'DummySong', '1:45', 1, 500,0,0),
    (601, 'DummySong', '1:34', 2, 501,0,0),
    (602, 'DummySong', '2:17', 3, 501,0,0),
    (603, 'DummySong', '3:21', 4, 501,0,0),
    (604, 'DummyXSong', '2:33', 5, 501,0,0),
    (605, 'DummyXSong', '1:56', 6, 501,0,0);

/* Song-Artist */
INSERT INTO song_artist (song_id, artist_id) VALUES
    (600, 300),
    (600, 301),
    (601, 301),
    (602, 301);

/* Song Review */
INSERT INTO song_review (review_id, song_id) VALUES
    (400, 600);