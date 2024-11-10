/* Image */
INSERT INTO image (id, content) VALUES
    (100, CAST('beef' AS BYTEA)),
    (101, CAST('beef' AS BYTEA)),
    (102, CAST('beef' AS BYTEA)),
    (103, CAST('beef' AS BYTEA)),
    (104, CAST('beef' AS BYTEA)),
    (105, CAST('beef' AS BYTEA)),
    (106, CAST('beef' AS BYTEA)),
    (107, CAST('beef' AS BYTEA));

/* Users */
INSERT INTO cuser (id, username, email, password, verified, moderator, img_id) VALUES
    (200,'Dummy', 'dummy@example.com', 'dummy123',false, false, 100),
    (201,'Dummy1', 'dummy1@example.com', 'dummy123', false, false, 101),
    (202,'Dummy2', 'dummy2@example.com', 'dummy123', false, false,102),
    (203,'Dummy3', 'dummy3@example.com', 'dummy123', false, false, 103);

/* Artists */
INSERT INTO artist (id, name, bio, rating_amount, avg_rating, img_id) VALUES
    (300,'DummyName1', 'DummyBio',0, 0,104),
    (301,'DummyName2', 'DummyBio',0, 0,105);

/* Albums */
INSERT INTO album (id, title, genre, release_date, img_id, artist_id, rating_amount, avg_rating) VALUES
    (500, 'DummyTitle1', 'DummyGenre', '2000-10-10', 106, 300,0,0),
    (501, 'DummyTitle2', 'DummyGenre', '2000-10-10', 107, 301,0,0);

/* Songs */
INSERT INTO song (id, title, duration, track_number, album_id, rating_amount, avg_rating) VALUES
    (600, 'DummySong1', '1:45', 1, 500,0,0),
    (601, 'DummySong2', '1:45', 1, 501,0,0);

/* Reviews */
INSERT INTO review (id, user_id, title, description, rating, likes, created_at, isblocked, comment_amount) VALUES
    (400, 200, 'Artist Review 1', 'Great artist', 5, 3, '2024-01-01 10:00:00', false, 2),
    (401, 200, 'Album Review 1', 'Amazing album', 4, 2, '2024-01-02 10:00:00', false, 1),
    (402, 200, 'Song Review 1', 'Nice song', 3, 1, '2024-01-03 10:00:00', false, 0),
    (403, 201, 'Artist Review 2', 'Decent artist', 3, 0, '2024-01-04 10:00:00', true, 0),
    (404, 201, 'Album Review 2', 'Good album', 4, 5, '2024-01-05 10:00:00', false, 3),
    (405, 201, 'Song Review 2', 'Great song', 5, 4, '2024-01-06 10:00:00', false, 1);

/* Review Types */
INSERT INTO artist_review (review_id, artist_id) VALUES
    (400, 300),
    (403, 301);

INSERT INTO album_review (review_id, album_id) VALUES
    (401, 500),
    (404, 501);

INSERT INTO song_review (review_id, song_id) VALUES
    (402, 600),
    (405, 601);

/* Comments */
INSERT INTO comment (id, user_id, review_id, content, created_at) VALUES
    (700, 201, 400, 'Great review!', '2024-01-01 11:00:00'),
    (701, 202, 400, 'I agree!', '2024-01-01 12:00:00'),
    (702, 201, 401, 'Nice perspective', '2024-01-02 11:00:00'),
    (703, 201, 404, 'Well written', '2024-01-05 11:00:00'),
    (704, 202, 404, 'Totally agree', '2024-01-05 12:00:00'),
    (705, 203, 404, 'Great points', '2024-01-05 13:00:00'),
    (706, 201, 405, 'Love this review', '2024-01-06 11:00:00');