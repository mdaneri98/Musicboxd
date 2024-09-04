-- Inserción en la tabla 'image'

-- Inserción en la tabla 'cuser'
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('johndoe', 'johndoe@example.com', 'password123', 'John Doe', 'Music enthusiast', 1); -- id = 1
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('janedoe', 'janedoe@example.com', 'password456', 'Jane Doe', 'Loves classic rock', 1); -- id = 2
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('freddiem', 'freddiem@example.com', 'queenfan', 'Freddie Mercury', 'Frontman of Queen', 1); -- id = 3
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('paulmcc', 'paulmcc@example.com', 'beatlesfan', 'Paul McCartney', 'Member of The Beatles', 1); -- id = 4
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('davidb', 'davidb@example.com', 'starman', 'David Bowie', 'The Thin White Duke', 1); -- id = 5
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('eltonj', 'eltonj@example.com', 'rocketman', 'Elton John', 'Rocket Man', 1); -- id = 6
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('mickj', 'mickj@example.com', 'stonesfan', 'Mick Jagger', 'Lead singer of The Rolling Stones', 1); -- id = 7
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('ringostarr', 'ringostarr@example.com', 'beatlesdrums', 'Ringo Starr', 'Drummer of The Beatles', 1); -- id = 8
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('brians', 'brians@example.com', 'queenrocks', 'Brian May', 'Guitarist of Queen', 1); -- id = 9
INSERT INTO cuser (username, email, password, name, bio, img_id)
VALUES ('keithr', 'keithr@example.com', 'rocknroll', 'Keith Richards', 'Guitarist of The Rolling Stones', 1); -- id = 10

-- Inserción en la tabla 'artist'
INSERT INTO artist (name, bio, img_id) VALUES ('Queen', 'Legendary British rock band', 2); -- id = 1
INSERT INTO artist (name, bio, img_id) VALUES ('The Beatles', 'Iconic British rock band', 3); -- id = 2
INSERT INTO artist (name, bio, img_id) VALUES ('David Bowie', 'Innovative British musician', 4); -- id = 3
INSERT INTO artist (name, bio, img_id) VALUES ('Elton John', 'Renowned British singer and pianist', 5); -- id = 4
INSERT INTO artist (name, bio, img_id) VALUES ('The Rolling Stones', 'British rock band', 6); -- id = 5
INSERT INTO artist (name, bio, img_id) VALUES ('Led Zeppelin', 'Pioneers of hard rock and heavy metal', 7); -- id = 6
INSERT INTO artist (name, bio, img_id) VALUES ('Pink Floyd', 'Progressive rock band', 8); -- id = 7
INSERT INTO artist (name, bio, img_id) VALUES ('Fleetwood Mac', 'British-American rock band', 9); -- id = 8
INSERT INTO artist (name, bio, img_id) VALUES ('The Who', 'English rock band', 10); -- id = 9
INSERT INTO artist (name, bio, img_id) VALUES ('Nirvana', 'American grunge band', 11); -- id = 10

-- Inserción en la tabla 'album'
-- Queen
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('A Night at the Opera', 'Rock', '1975-11-21', 12, 1); -- id = 1
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('News of the World', 'Rock', '1977-10-28', 13, 1); -- id = 2
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Sheer Heart Attack', 'Rock', '1974-11-08', 14, 1); -- id = 3
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Game', 'Rock', '1980-06-30', 15, 1); -- id = 4
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Innuendo', 'Rock', '1991-02-05', 16, 1); -- id = 5

-- David Bowie
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('The Rise and Fall of Ziggy Stardust', 'Rock', '1972-06-16', 1, 3); -- id = 6
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Heroes', 'Rock', '1977-10-14', 1, 3); -- id = 7
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Hunky Dory', 'Rock', '1971-12-17', 1, 3); -- id = 8
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Aladdin Sane', 'Rock', '1973-04-13', 1, 3); -- id = 9
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Low', 'Art Rock', '1977-01-14', 1, 3); -- id = 10

-- Elton John
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Goodbye Yellow Brick Road', 'Pop/Rock', '1973-10-05', 1, 4); -- id = 11
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Captain Fantastic and the Brown Dirt Cowboy', 'Pop/Rock', '1975-05-19', 1, 4); -- id = 12
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Madman Across the Water', 'Pop/Rock', '1971-11-05', 1, 4); -- id = 13
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Honky Château', 'Pop/Rock', '1972-05-19', 1, 4); -- id = 14
INSERT INTO album (title, genre, release_date, img_id, artist_id) VALUES ('Dont Shoot Me Im Only the Piano Player', 'Pop/Rock', '1973-01-26', 1, 4); -- id = 15

-- Inserción en la tabla 'song'
-- Asociando canciones con álbumes de Queen
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Bohemian Rhapsody', '5:55', 1, 1); -- id = 1
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Love of My Life', '3:39', 1, 1); -- id = 2
INSERT INTO song (title, duration, track_number, album_id) VALUES ('We Will Rock You', '2:01', 1, 2); -- id = 3
INSERT INTO song (title, duration, track_number, album_id) VALUES ('We Are the Champions', '2:59', 1, 2); -- id = 4
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Radio Ga Ga', '5:48', 1, 5); -- id = 5

-- Asociando canciones con álbumes de David Bowie
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Ziggy Stardust', '3:13', 1, 6); -- id = 6
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Starman', '4:16', 1, 6); -- id = 7
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Heroes', '6:07', 1, 7); -- id = 8
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Changes', '3:37', 1, 8); -- id = 9
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Life on Mars?', '3:55', 1, 8); -- id = 10

-- Asociando canciones con álbumes de Elton John
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Goodbye Yellow Brick Road', '3:14', 1, 11); -- id = 11
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Rocket Man', '4:41', 1, 14); -- id = 12
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Tiny Dancer', '6:17', 1, 13); -- id = 13
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Daniel', '3:54', 1, 15); -- id = 14
INSERT INTO song (title, duration, track_number, album_id) VALUES ('Bennie and the Jets', '5:23', 1, 11); -- id = 15
