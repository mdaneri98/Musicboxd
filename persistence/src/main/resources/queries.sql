INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484452', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484453', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484454', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484455', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484456', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484457', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484458', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D49484459', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D4948445A', 'hex'));
INSERT INTO image (content) VALUES (decode('89504E470D0A1A0A0000000D4948445B', 'hex'));


INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('johndoe', 'johndoe@example.com', 'password123', 'John Doe', 'Music enthusiast', 1);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('janedoe', 'janedoe@example.com', 'password456', 'Jane Doe', 'Loves classic rock', 2);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('freddiem', 'freddiem@example.com', 'queenfan', 'Freddie Mercury', 'Frontman of Queen', 3);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('paulmcc', 'paulmcc@example.com', 'beatlesfan', 'Paul McCartney', 'Member of The Beatles', 4);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('davidb', 'davidb@example.com', 'starman', 'David Bowie', 'The Thin White Duke', 5);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('eltonj', 'eltonj@example.com', 'rocketman', 'Elton John', 'Rocket Man', 6);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('mickj', 'mickj@example.com', 'stonesfan', 'Mick Jagger', 'Lead singer of The Rolling Stones', 7);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('ringostarr', 'ringostarr@example.com', 'beatlesdrums', 'Ringo Starr', 'Drummer of The Beatles', 8);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('brians', 'brians@example.com', 'queenrocks', 'Brian May', 'Guitarist of Queen', 9);
INSERT INTO cuser (username, email, password, name, bio, img_id) VALUES ('keithr', 'keithr@example.com', 'rocknroll', 'Keith Richards', 'Guitarist of The Rolling Stones', 10);


INSERT INTO artist (name, bio, img_id) VALUES ('Queen', 'Legendary British rock band', 3);
INSERT INTO artist (name, bio, img_id) VALUES ('The Beatles', 'Iconic British rock band', 4);
INSERT INTO artist (name, bio, img_id) VALUES ('David Bowie', 'Innovative British musician', 5);
INSERT INTO artist (name, bio, img_id) VALUES ('Elton John', 'Renowned British singer and pianist', 6);
INSERT INTO artist (name, bio, img_id) VALUES ('The Rolling Stones', 'British rock band', 7);
INSERT INTO artist (name, bio, img_id) VALUES ('Led Zeppelin', 'Pioneers of hard rock and heavy metal', 8);
INSERT INTO artist (name, bio, img_id) VALUES ('Pink Floyd', 'Progressive rock band', 9);
INSERT INTO artist (name, bio, img_id) VALUES ('Fleetwood Mac', 'British-American rock band', 10);
INSERT INTO artist (name, bio, img_id) VALUES ('The Who', 'English rock band', 1);
INSERT INTO artist (name, bio, img_id) VALUES ('Nirvana', 'American grunge band', 2);


INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('A Night at the Opera', 'Rock', '1975-11-21', 1, 1);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Abbey Road', 'Rock', '1969-09-26', 2, 2);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Rise and Fall of Ziggy Stardust', 'Rock', '1972-06-16', 3, 3);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Goodbye Yellow Brick Road', 'Pop/Rock', '1973-10-05', 4, 4);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Let It Bleed', 'Rock', '1969-12-05', 5, 5);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Led Zeppelin IV', 'Hard Rock', '1971-11-08', 6, 6);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Dark Side of the Moon', 'Progressive Rock', '1973-03-01', 7, 7);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Rumours', 'Rock', '1977-02-04', 8, 8);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Whos Next', 'Rock', '1971-08-14', 9, 9);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Nevermind', 'Grunge', '1991-09-24', 10, 10);

INSERT INTO song (title, duration, track_number, album_id) VALUES ('Bohemian Rhapsody', '00:05:55', 11, 1);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Come Together', '00:04:20', 1, 2);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Starman', '00:04:13', 4, 3);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Candle in the Wind', '00:03:50', 1, 4);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Gimme Shelter', '00:04:30', 1, 5);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Stairway to Heaven', '00:08:02', 4, 6);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Money', '00:06:22', 6, 7);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Go Your Own Way', '00:03:38', 7, 8);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Baba ORiley', '00:05:00', 1, 9);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Smells Like Teen Spirit', '00:05:01', 1, 10);


INSERT INTO song_artist (song_id, artist_id) VALUES (1, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (2, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (3, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (4, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (5, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (6, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (7, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (8,8);
INSERT INTO song_artist (song_id, artist_id) VALUES (9,9);
INSERT INTO song_artist (song_id, artist_id) VALUES (10,10);
