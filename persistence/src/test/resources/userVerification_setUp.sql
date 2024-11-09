/* Image */
INSERT INTO image (id, content) VALUES
    (100, CAST('beef' AS BYTEA)),
    (101, CAST('beef' AS BYTEA)),
    (102, CAST('beef' AS BYTEA)),
    (103, CAST('beef' AS BYTEA));

/* Users */
INSERT INTO cuser (id, username, email, password, verified, moderator, img_id) VALUES
    (200,'Dummy', 'dummy@example.com', 'dummy123',false, false, 100),
    (201,'Dummy1', 'dummy1@example.com', 'dummy123', false, false, 101),
    (202,'Dummy2', 'dummy2@example.com', 'dummy123', false, false,102),
    (203,'Dummy3', 'dummy3@example.com', 'dummy123', false, false, 103);

/* Active verification codes */
INSERT INTO verification (id, user_id, code, expire_date, vtype) VALUES
    (800, 200, 'valid_code_1', '2025-01-01 10:00:00', 'check_email'),
    (801, 201, 'valid_code_2', '2025-01-01 10:00:00', 'forgot_password'),
    (802, 202, 'expired_code', '2020-01-01 10:00:00', 'check_email'),
    (803, 203, 'valid_code_3', '2025-01-01 10:00:00', 'general');