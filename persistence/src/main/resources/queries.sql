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


-- Queen
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('A Night at the Opera', 'Rock', '1975-11-21', 1, 1);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('News of the World', 'Rock', '1977-10-28', 1, 1);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Sheer Heart Attack', 'Rock', '1974-11-08', 2, 1);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Game', 'Rock', '1980-06-30', 3, 1);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Innuendo', 'Rock', '1991-02-05', 4, 1);

-- David Bowie
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Rise and Fall of Ziggy Stardust', 'Rock', '1972-06-16', 2, 3);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Heroes', 'Rock', '1977-10-14', 5, 3);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Hunky Dory', 'Rock', '1971-12-17', 6, 3);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Aladdin Sane', 'Rock', '1973-04-13', 7, 3);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Low', 'Art Rock', '1977-01-14', 8, 3);

-- Elton John
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Goodbye Yellow Brick Road', 'Pop/Rock', '1973-10-05', 2, 4);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Captain Fantastic and the Brown Dirt Cowboy', 'Pop/Rock', '1975-05-19', 9, 4);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Madman Across the Water', 'Pop/Rock', '1971-11-05', 10, 4);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Honky Château', 'Pop/Rock', '1972-05-19', 1, 4);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Dont Shoot Me Im Only the Piano Player', 'Pop/Rock', '1973-01-26', 2, 4);

-- The Rolling Stones
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Let It Bleed', 'Rock', '1969-12-05', 2, 5);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Exile on Main St.', 'Rock', '1972-05-12', 3, 5);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Sticky Fingers', 'Rock', '1971-04-23', 4, 5);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Beggars Banquet', 'Rock', '1968-12-06', 5, 5);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Some Girls', 'Rock', '1978-06-09', 6, 5);

-- Led Zeppelin
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Led Zeppelin IV', 'Hard Rock', '1971-11-08', 2, 6);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Physical Graffiti', 'Hard Rock', '1975-02-24', 7, 6);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Houses of the Holy', 'Hard Rock', '1973-03-28', 8, 6);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Led Zeppelin II', 'Hard Rock', '1969-10-22', 9, 6);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Presence', 'Hard Rock', '1976-03-31', 10, 6);

-- Pink Floyd
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Dark Side of the Moon', 'Progressive Rock', '1973-03-01', 2, 7);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Wish You Were Here', 'Progressive Rock', '1975-09-12', 1, 7);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Wall', 'Progressive Rock', '1979-11-30', 2, 7);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Animals', 'Progressive Rock', '1977-01-23', 3, 7);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Meddle', 'Progressive Rock', '1971-10-31', 4, 7);

-- Fleetwood Mac
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Rumours', 'Rock', '1977-02-04', 2, 8);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Fleetwood Mac', 'Rock', '1975-07-11', 5, 8);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Tusk', 'Rock', '1979-10-12', 6, 8);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Mirage', 'Rock', '1982-06-18', 7, 8);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Tango in the Night', 'Rock', '1987-04-13', 8, 8);

-- The Who
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Whos Next', 'Rock', '1971-08-14', 2, 9);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Tommy', 'Rock', '1969-05-23', 9, 9);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Quadrophenia', 'Rock', '1973-10-26', 10, 9);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Who Sell Out', 'Rock', '1967-12-15', 1, 9);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('A Quick One', 'Rock', '1966-12-09', 2, 9);

-- Nirvana
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Nevermind', 'Grunge', '1991-09-24', 2, 10);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('In Utero', 'Grunge', '1993-09-21', 3, 10);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Bleach', 'Grunge', '1989-06-15', 4, 10);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('MTV Unplugged in New York', 'Grunge', '1994-11-01', 5, 10);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Incesticide', 'Grunge', '1992-12-14', 6, 10);

-- The Beatles
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Abbey Road', 'Rock', '1969-09-26', 6, 2);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Sgt. Peppers Lonely Hearts Club Band', 'Rock', '1967-05-26', 7, 2);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Revolver', 'Rock', '1966-08-05', 8, 2);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Beatles (White Album)', 'Rock', '1968-11-22', 9, 2);
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Let It Be', 'Rock', '1970-05-08', 10, 2);

-- Queen
INSERT INTO song (title, duration, track_number, album_id) VALUES ('We Will Rock You', '00:02:02', 1, 1);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('We Are the Champions', '00:02:59', 2, 1);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Killer Queen', '00:02:57', 1, 2);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Stone Cold Crazy', '00:02:16', 2, 2);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Another One Bites the Dust', '00:03:35', 1, 3);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Crazy Little Thing Called Love', '00:02:44', 2, 3);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Innuendo', '00:06:32', 1, 4);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('The Show Must Go On', '00:04:37', 2, 4);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Bohemian Rhapsody', '00:05:55', 3, 4);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Radio Ga Ga', '00:05:48', 4, 3);

-- The Beatles
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Come Together', '00:04:20', 1, 11);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Something', '00:03:03', 2, 11);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Here Comes the Sun', '00:03:06', 1, 12);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Let It Be', '00:04:03', 1, 13);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Hey Jude', '00:07:11', 2, 13);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Yesterday', '00:02:05', 1, 14);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Eleanor Rigby', '00:02:08', 2, 14);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('A Hard Day''s Night', '00:02:34', 1, 15);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Can''t Buy Me Love', '00:02:12', 2, 15);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Help!', '00:02:18', 1, 16);

-- David Bowie
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Heroes', '00:06:11', 1, 5);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Beauty and the Beast', '00:03:32', 2, 5);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Changes', '00:03:33', 1, 6);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Life on Mars?', '00:03:48', 2, 6);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Aladdin Sane', '00:05:06', 1, 7);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('The Jean Genie', '00:04:08', 2, 7);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Sound and Vision', '00:03:03', 1, 8);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Warszawa', '00:06:23', 2, 8);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Ashes to Ashes', '00:04:25', 3, 5);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Space Oddity', '00:05:16', 4, 6);

-- Elton John
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Someone Saved My Life Tonight', '00:06:45', 1, 9);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Captain Fantastic and the Brown Dirt Cowboy', '00:05:46', 2, 9);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Tiny Dancer', '00:06:15', 1, 10);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Levon', '00:05:22', 2, 10);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Rocket Man', '00:04:41', 1, 11);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Honky Cat', '00:05:13', 2, 11);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Crocodile Rock', '00:03:56', 1, 12);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Daniel', '00:03:54', 2, 12);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Bennie and the Jets', '00:05:23', 3, 12);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Your Song', '00:04:02', 4, 9);

--The Rolling Stones
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Tumbling Dice', '00:03:45', 1, 13);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Rocks Off', '00:04:32', 2, 13);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Brown Sugar', '00:03:49', 1, 14);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Wild Horses', '00:05:42', 2, 14);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Sympathy for the Devil', '00:06:18', 1, 15);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Street Fighting Man', '00:03:15', 2, 15);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Miss You', '00:04:48', 1, 16);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Beast of Burden', '00:04:25', 2, 16);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Paint It Black', '00:03:45', 3, 14);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Gimme Shelter', '00:04:30', 4, 13);

-- Led Zeppelin
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Kashmir', '00:08:37', 1, 17);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Trampled Under Foot', '00:05:35', 2, 17);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('The Ocean', '00:04:31', 1, 18);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Over the Hills and Far Away', '00:04:50', 2, 18);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Whole Lotta Love', '00:05:33', 1, 19);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Ramble On', '00:04:23', 2, 19);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Achilles Last Stand', '00:10:25', 1, 20);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Nobody''s Fault but Mine', '00:06:15', 2, 20);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Immigrant Song', '00:02:25', 3, 19);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Stairway to Heaven', '00:08:02', 4, 19);

-- Pink Floyd
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Shine On You Crazy Diamond', '00:13:32', 1, 21);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Wish You Were Here', '00:05:34', 2, 21);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Comfortably Numb', '00:06:22', 1, 22);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Another Brick in the Wall, Pt. 2', '00:03:59', 2, 22);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Dogs', '00:17:04', 1, 23);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Pigs (Three Different Ones)', '00:11:28', 2, 23);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Echoes', '00:23:31', 1, 24);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('One of These Days', '00:05:57', 2, 24);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Money', '00:06:23', 3, 22);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Time', '00:06:53', 4, 22);

-- Fleetwood Mac
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Rhiannon', '00:04:11', 1, 25);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Landslide', '00:03:19', 2, 25);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Tusk', '00:03:37', 1, 26);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Sara', '00:06:27', 2, 26);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Gypsy', '00:04:24', 1, 27);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Hold Me', '00:03:45', 2, 27);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Big Love', '00:03:37', 1, 28);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Little Lies', '00:03:38', 2, 28);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Go Your Own Way', '00:03:43', 3, 25);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('The Chain', '00:04:28', 4, 25);

-- The Who
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Pinball Wizard', '00:03:01', 1, 29);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Tommy Can You Hear Me?', '00:01:36', 2, 29);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('The Real Me', '00:03:20', 1, 30);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Love Reign O''er Me', '00:05:49', 2, 30);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('I Can See for Miles', '00:04:07', 1, 31);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Tattoo', '00:02:47', 2, 31);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Boris the Spider', '00:02:28', 1, 32);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('A Quick One, While He''s Away', '00:09:11', 2, 32);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Substitute', '00:03:47', 3, 30);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('My Generation', '00:03:18', 4, 31);

-- Nirvana
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Smells Like Teen Spirit', '00:05:01', 1, 33);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Come as You Are', '00:03:39', 2, 33);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Heart-Shaped Box', '00:04:39', 1, 34);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Rape Me', '00:02:50', 2, 34);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('About a Girl', '00:02:48', 1, 35);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Love Buzz', '00:03:35', 2, 35);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('All Apologies', '00:04:23', 1, 36);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('The Man Who Sold the World', '00:04:20', 2, 36);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Sliver', '00:02:16', 1, 37);
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Aneurysm', '00:04:35', 2, 37);

-- Queen
INSERT INTO song_artist (song_id, artist_id) VALUES (1, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (2, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (3, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (4, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (5, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (6, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (7, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (8, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (9, 1);
INSERT INTO song_artist (song_id, artist_id) VALUES (10, 1);

-- The Beatles
INSERT INTO song_artist (song_id, artist_id) VALUES (11, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (12, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (13, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (14, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (15, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (16, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (17, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (18, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (19, 2);
INSERT INTO song_artist (song_id, artist_id) VALUES (20, 2);

-- David Bowie
INSERT INTO song_artist (song_id, artist_id) VALUES (21, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (22, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (23, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (24, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (25, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (26, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (27, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (28, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (29, 3);
INSERT INTO song_artist (song_id, artist_id) VALUES (30, 3);

-- Elton John
INSERT INTO song_artist (song_id, artist_id) VALUES (31, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (32, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (33, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (34, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (35, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (36, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (37, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (38, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (39, 4);
INSERT INTO song_artist (song_id, artist_id) VALUES (40, 4);

-- The Rolling Stones
INSERT INTO song_artist (song_id, artist_id) VALUES (41, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (42, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (43, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (44, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (45, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (46, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (47, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (48, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (49, 5);
INSERT INTO song_artist (song_id, artist_id) VALUES (50, 5);

-- Led Zeppelin
INSERT INTO song_artist (song_id, artist_id) VALUES (51, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (52, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (53, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (54, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (55, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (56, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (57, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (58, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (59, 6);
INSERT INTO song_artist (song_id, artist_id) VALUES (60, 6);

-- Pink Floyd
INSERT INTO song_artist (song_id, artist_id) VALUES (61, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (62, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (63, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (64, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (65, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (66, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (67, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (68, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (69, 7);
INSERT INTO song_artist (song_id, artist_id) VALUES (70, 7);

-- Fleetwood Mac
INSERT INTO song_artist (song_id, artist_id) VALUES (71, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (72, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (73, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (74, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (75, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (76, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (77, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (78, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (79, 8);
INSERT INTO song_artist (song_id, artist_id) VALUES (80, 8);

-- The Who
INSERT INTO song_artist (song_id, artist_id) VALUES (81, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (82, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (83, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (84, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (85, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (86, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (87, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (88, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (89, 9);
INSERT INTO song_artist (song_id, artist_id) VALUES (90, 9);

-- Nirvana
INSERT INTO song_artist (song_id, artist_id) VALUES (91, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (92, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (93, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (94, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (95, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (96, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (97, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (98, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (99, 10);
INSERT INTO song_artist (song_id, artist_id) VALUES (100, 10);



