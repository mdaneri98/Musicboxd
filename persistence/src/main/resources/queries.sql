INSERT INTO cuser (username, email, password, name, bio) VALUES
                                                             ('rock_fan', 'rockfan@example.com', 'password123', 'John Doe', 'Amante del rock clásico y las bandas legendarias.'),
                                                             ('queen_lover', 'queenlover@example.com', 'password123', 'Jane Smith', 'Fanática de Queen y Freddie Mercury.'),
                                                             ('acdc_rocks', 'acdclover@example.com', 'password123', 'Mark Johnson', 'AC/DC es mi banda favorita de todos los tiempos.');

INSERT INTO artist (name, bio, img_src) VALUES
                                            ('The Beatles', 'Legendary rock band from Liverpool.', 'beatles.jpg'),
                                            ('Led Zeppelin', 'Pioneers of hard rock and heavy metal.', 'led_zeppelin.jpg'),
                                            ('The Rolling Stones', 'Iconic British rock band with a long history.', 'rolling_stones.jpg'),
                                            ('Pink Floyd', 'Progressive rock band known for their concept albums.', 'pink_floyd.jpg'),
                                            ('David Bowie', 'Influential singer, songwriter, and actor.', 'david_bowie.jpg');

INSERT INTO album (title, genre, release_date, img_src, artist_id) VALUES
                                                                       ('Abbey Road', 'Rock', '1969-09-26', 'abbey_road.jpg', 1),  -- The Beatles
                                                                       ('Led Zeppelin IV', 'Hard Rock', '1971-11-08', 'led_zeppelin_iv.jpg', 2),  -- Led Zeppelin
                                                                       ('Sticky Fingers', 'Rock', '1971-04-23', 'sticky_fingers.jpg', 3),  -- The Rolling Stones
                                                                       ('The Dark Side of the Moon', 'Progressive Rock', '1973-03-01', 'dark_side_of_the_moon.jpg', 4),  -- Pink Floyd
                                                                       ('The Rise and Fall of Ziggy Stardust', 'Glam Rock', '1972-06-16', 'ziggy_stardust.jpg', 5);  -- David Bowie

INSERT INTO song (title, duration, track_number, album_id, artist_id) VALUES
                                                                          ('Come Together', '00:04:19', 1, 1, 1),  -- Abbey Road, The Beatles
                                                                          ('Black Dog', '00:04:55', 1, 2, 2),  -- Led Zeppelin IV, Led Zeppelin
                                                                          ('Brown Sugar', '00:03:50', 1, 3, 3),  -- Sticky Fingers, The Rolling Stones
                                                                          ('Money', '00:06:22', 6, 4, 4),  -- The Dark Side of the Moon, Pink Floyd
                                                                          ('Starman', '00:04:13', 1, 5, 5);  -- Ziggy Stardust, David Bowie

INSERT INTO song_artist (song_id, artist_id) VALUES
                                                 (1, 1),  -- 'Come Together' by The Beatles
                                                 (2, 2),  -- 'Black Dog' by Led Zeppelin
                                                 (3, 3),  -- 'Brown Sugar' by The Rolling Stones
                                                 (4, 4),  -- 'Money' by Pink Floyd
                                                 (5, 5);  -- 'Starman' by David Bowie