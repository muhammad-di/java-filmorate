
--deleting all tables before each start

DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS user_review CASCADE;
DROP TABLE IF EXISTS film_review CASCADE;
DROP TABLE IF EXISTS reviews_likes CASCADE;
DROP TABLE IF EXISTS review_likes CASCADE;

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

-- creating a review and review related tables

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(250),
    is_positive BOOLEAN NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    film_id INTEGER NOT NULL REFERENCES film (film_id) ON DELETE CASCADE,
    useful INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS user_review (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_review (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id INTEGER REFERENCES film (film_id) ON DELETE CASCADE,
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_likes (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_dislikes (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);
