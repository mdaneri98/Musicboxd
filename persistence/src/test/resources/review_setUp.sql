/* Image */
INSERT INTO image (id, content) VALUES (100, CAST('deadbeef' AS BYTEA));

/* User */
INSERT INTO cuser (id, username, email, password, img_id) VALUES (200,'Dummy', 'dummy@example.com', 'dummy123', 100); -- id = 1

/* Artist */
INSERT INTO artist (id, name, img_id) VALUES (300,'Dummy',100);

/* Review */
INSERT INTO review (id, user_id, title, description, rating) VALUES (400, 200, 'DummyTitle', 'DummyDescription', 3);

/* Artist Review */
INSERT INTO artist_review (review_id, artist_id) VALUES (400, 300);
