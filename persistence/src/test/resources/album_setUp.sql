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
    (1000, CAST('beef' AS BYTEA));

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
    (500, 'DummyTitle', 'DummyGenre', '2000-10-10', 100, 300, 0, 0),
    (501, 'DummyTitle', 'DummyGenre', '2000-10-10', 104, 301,0,0),
    (502, 'DummyTitle', 'DummyGenre', '2000-10-10', 105, 301,0,0),
    (503, 'DummyTitle', 'DummyGenre', '2000-10-10', 106, 301,0,0),
    (504, 'DummyXTitle', 'DummyGenre', '2000-10-10', 107, 301,0,0);

/* Album Review */
INSERT INTO album_review (review_id, album_id) VALUES
    (400, 500);