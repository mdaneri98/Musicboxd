CREATE TABLE IF NOT EXISTS image (
    id SERIAL PRIMARY KEY,
    content BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS cuser (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    bio TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    img_id INT,

    FOREIGN KEY (img_id) REFERENCES image(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS artist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    img_id INT NOT NULL,

    FOREIGN KEY (img_id) REFERENCES image(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS album (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    release_date DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    img_id INT NOT NULL,
    artist_id INT NOT NULL,

    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    FOREIGN KEY (img_id) REFERENCES image(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS song (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    duration VARCHAR(10) NOT NULL,
    track_number INT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    album_id INT,
    img_id INT,

    FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS song_artist (
    song_id INT NOT NULL,
    artist_id INT NOT NULL,

    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, artist_id)
);

CREATE TABLE IF NOT EXISTS artist_review (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    artist_id INT NOT NULL,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(300) NOT NULL,
    rating INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    likes INT DEFAULT 0,

    FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES cuser(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS album_review (
     id SERIAL PRIMARY KEY,
     user_id INT NOT NULL,
     album_id INT NOT NULL,
     title VARCHAR(50) NOT NULL,
     description VARCHAR(300) NOT NULL,
     rating INT NOT NULL,
     created_at TIMESTAMP DEFAULT NOW(),
     likes INT DEFAULT 0,

     FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
     FOREIGN KEY (user_id) REFERENCES cuser(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS song_review (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    song_id INT NOT NULL,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(300) NOT NULL,
    rating INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    likes INT DEFAULT 0,

    FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES cuser(id) ON DELETE CASCADE
);






