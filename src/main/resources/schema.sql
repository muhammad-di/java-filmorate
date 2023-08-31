-- creating a film and film related tables
CREATE TABLE IF NOT EXISTS mpa
(
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name      VARCHAR(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    release_date DATE NOT NULL,
    duration     INTEGER NOT NULL,
    description  VARCHAR(200) NOT NULL,
    mpa          INTEGER REFERENCES mpa (rating_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR(20) NOT NULL
);


CREATE TABLE IF NOT EXISTS film_genre
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  INTEGER REFERENCES film (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre (genre_id) ON DELETE CASCADE
);

-- creating a user and user related tables

CREATE TABLE IF NOT EXISTS users
(
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login    VARCHAR(50) NOT NULL,
    birthday DATE NOT NULL,
    email    VARCHAR(50) NOT NULL,
    name     VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS likes
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  INTEGER REFERENCES film (film_id) ON DELETE CASCADE,
    user_id  INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status BOOLEAN DEFAULT FALSE NOT NULL,
    user_id  INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id  INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);