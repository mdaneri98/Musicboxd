CREATE TABLE IF NOT EXISTS cuser (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    bio TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS artist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    img_src VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS album (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    release_date DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    img_src VARCHAR(100),
    artist_id SERIAL NOT NULL,

    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS song (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    duration INTERVAL,
    track_number INT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    album_id SERIAL,
    artist_id SERIAL,

    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE SET NULL,
    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS song_artist (
    song_id SERIAL NOT NULL,
    artist_id SERIAL NOT NULL,

    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, artist_id)
);
