-- MPA -----------------------------------------------------------------------------------------------------------------
MERGE INTO mpa KEY (rating_id) VALUES (1, 'G');
MERGE INTO mpa KEY (rating_id) VALUES (2, 'PG');
MERGE INTO mpa KEY (rating_id) VALUES (3, 'PG-13');
MERGE INTO mpa KEY (rating_id) VALUES (4, 'R');
MERGE INTO mpa KEY (rating_id) VALUES (5, 'NC-17');

-- Genre ---------------------------------------------------------------------------------------------------------------
MERGE INTO genre KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO genre KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO genre KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genre KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO genre KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO genre KEY (genre_id) VALUES (6, 'Боевик');

-- Director ------------------------------------------------------------------------------------------------------------
--MERGE INTO director KEY (director_id) values (1, 'Rowney Cartmell');
--MERGE INTO director KEY (director_id) values (2, 'Titos Silcox');
--MERGE INTO director KEY (director_id) values (3, 'Gonzalo Petrello');
--MERGE INTO director KEY (director_id) values (4, 'Casey Luddy');
--MERGE INTO director KEY (director_id) values (5, 'Nanine Hargrove');

-- Films ---------------------------------------------------------------------------------------------------------------
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('S.F.W.', '2002-10-13', 136, 'Donec dapibus. Duis at velit eu est congue elementum.', 3);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Bon Voyage, Charlie Brown (and Don''t Come Back!)', '1953-02-15', 81, 'Etiam faucibus cursus urna. Ut tellus.', 5);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('The Heart of the World', '1982-03-14', 46, 'Nulla justo. Aliquam quis turpis eget elit sodales scelerisque. Mauris sit amet eros.', 2);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Futuresport', '1953-07-19', 101, 'Morbi vestibulum, velit id pretium iaculis, diam erat fermentum justo, nec condimentum neque sapien.', 1);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Shooting Fish', '2012-05-09', 42, 'Phasellus in felis. Donec semper sapien a libero. Nam dui. Proin leo odio, porttitor id, consequat', 5);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Travellers and Magicians', '1988-06-02', 178, 'Aliquam quis turpis eget elit sodales scelerisque. Mauris sit amet eros. Suspendisse ', 5);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Star Trek: Insurrection', '1985-12-05', 56, 'Aenean lectus. Pellentesque eget nunc. Donec quis orci eget orci vehicula condimentum.', 3);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Body Fat Index of Love', '1983-11-30', 116, 'Vivamus metus arcu, adipiscing molestie, hendrerit at, vulputate vitae, nisl. Aenean lectus.', 2);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Brooklyn''s Finest', '1954-09-21', 44, 'Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus. Phasellus in felis.', 4);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Mostly Unfabulous Social Life of Ethan Green, The', '1992-03-19', 172, 'Nullam porttitor lacus at turpis. Donec posuere metus vitae ipsum. Aliquam non mauris.', 2);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Three Stars', '2000-12-18', 76, 'Maecenas ut massa quis augue luctus tincidunt. Nulla mollis molestie lorem. Quisque ut erat. Curabitur gravida nisi at nibh.', 1);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Night at the Opera, A', '1977-03-10', 55, 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl.', 3);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Camp Rock 2: The Final Jam', '2007-09-16', 120, 'Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo. In blandit ultrices enim. Lorem ipsum dolor.', 1);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Date and Switch', '2010-12-17', 169, 'Ut tellus. Nulla ut erat id mauris vulputate elementum. Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate.', 3);
--INSERT INTO film (name, release_date, duration, description, mpa) VALUES ('Promises', '1961-08-06', 68, 'Ut tellus. Nulla ut erat id mauris vulputate elementum. Nullam varius. Nulla facilisi. Cras non velit nec nisi vulputate nonummy.', 5);

-- Films ---------------------------------------------------------------------------------------------------------------
--insert into users (login, birthday, email, name) values ('bphripp0', '1980-12-25', 'bruxton0@chicagotribune.com', 'Brett');
--insert into users (login, birthday, email, name) values ('dmccrone1', '1984-01-21', 'dskellon1@columbia.edu', 'Dru');
--insert into users (login, birthday, email, name) values ('svaszoly2', '1979-03-20', 'swalcher2@newsvine.com', 'Sarina');
--insert into users (login, birthday, email, name) values ('jwood3', '1978-03-24', 'jfletham3@yolasite.com', 'Jard');
--insert into users (login, birthday, email, name) values ('rlevington4', '1979-09-09', 'rclough4@seattletimes.com', 'Randi');
--insert into users (login, birthday, email, name) values ('nmartel5', '1965-12-04', 'ngosswell5@pen.io', 'Nichols');
--insert into users (login, birthday, email, name) values ('ccrosen6', '1960-09-11', 'cgerman6@live.com', 'Clarie');
--insert into users (login, birthday, email, name) values ('wcolbron7', '1975-06-07', 'wrobardey7@cbsnews.com', 'Wesley');
--insert into users (login, birthday, email, name) values ('ldunstall8', '1981-07-20', 'lhiseman8@home.pl', 'Lazare');
--insert into users (login, birthday, email, name) values ('ahurley9', '1995-11-22', 'aboneham9@wiley.com', 'Alick');


--INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (1,6);
--INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (1,8);
--INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (1,23);
--INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (2,5);
--INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (2,8);
--INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (2,18);
------------------------------------------------------------------------------------------------------------------------
--INSERT INTO likes (FILM_ID, USER_ID) VALUES (2,16);
--INSERT INTO likes (FILM_ID, USER_ID) VALUES (2,22);
--INSERT INTO likes (FILM_ID, USER_ID) VALUES (2,20);
--INSERT INTO likes (FILM_ID, USER_ID) VALUES (1,18);
--INSERT INTO likes (FILM_ID, USER_ID) VALUES (1,24);
--INSERT INTO likes (FILM_ID, USER_ID) VALUES (1,23);