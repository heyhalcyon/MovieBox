use moviedb;

DELIMITER $$
DROP PROCEDURE if exists insert_star$$

CREATE PROCEDURE insert_star(IN star_name varchar(100), IN birth_year INT, OUT c varchar(10))

	BEGIN
		DECLARE max_id varchar(10) DEFAULT null;
        DECLARE new_id_num int DEFAULT 0;
		select max(id) from stars into max_id;
		SELECT CAST(SUBSTRING(max_id, 3, 7) AS SIGNED) INTO new_id_num;
        SET new_id_num = new_id_num + 1;
        SELECT CONCAT("nm", new_id_num) into c;
        INSERT INTO stars (id, name, birthYear) values (c, star_name, birth_year);
	END$$

DELIMITER ;


DELIMITER $$
DROP PROCEDURE if exists insert_genre$$

CREATE PROCEDURE insert_genre(IN genre_name varchar(32), OUT c varchar(10))

	BEGIN
		IF (SELECT 1=1 FROM genres WHERE name = genre_name) THEN
			BEGIN
				SELECT id from genres WHERE name = genre_name into c;
            END;
            ELSE
			BEGIN
				DECLARE max_id varchar(10) DEFAULT null;
				DECLARE new_id_num int DEFAULT 0;
				select max(id) from genres into max_id;
				SET new_id_num = max_id + 1;
                SELECT new_id_num into c;
				INSERT INTO genres (id, name) values (new_id_num, genre_name);
            END;
		END IF;
            
            
	END$$

DELIMITER ;
genres_in_movies

DELIMITER $$
DROP PROCEDURE if exists add_movie$$

CREATE PROCEDURE add_movie(IN movie_title varchar(100), IN movie_year INT, IN movie_director varchar(100), IN movie_genre varchar(32), IN movie_star varchar(100), IN star_year INT, OUT already_exists boolean, OUT c varchar(10))

	BEGIN
    
    
		DECLARE movie_id VARCHAR(10);
		DECLARE new_movie_id VARCHAR(10);
		DECLARE genre_id INT(11);
		DECLARE star_id VARCHAR(10);
        DECLARE max_id varchar(10) DEFAULT null;
        DECLARE new_id_num int DEFAULT 0;
        
		IF (SELECT count(*) FROM movies WHERE title=movie_title AND year=movie_year AND director=movie_director)=0 THEN
				SELECT max(id) from movies into max_id;
				SELECT CAST(SUBSTRING(max_id, 3, 7) AS SIGNED) INTO new_id_num;
				SET new_id_num = new_id_num + 1;
                SELECT false into already_exists;
				SELECT CONCAT("tt", new_id_num) into c;
				INSERT INTO movies (id, title, year, director) VALUES (c, movie_title, movie_year, movie_director);
		END IF;
		
		SELECT id FROM movies WHERE title=movie_title and year=movie_year and director=movie_director into c;
		IF movie_star IS NOT NULL THEN
			#star exists
			IF (SELECT 1=1 FROM stars WHERE name = movie_star LIMIT 1) THEN
            
				select id from stars where name = movie_star LIMIT 1 into star_id ;
                select star_id as fu;
				#INSERT INTO stars_in_movies VALUES(movie_star, c);
			#star not exists
            ELSE
				IF star_year IS NOT NULL THEN
					CALL insert_star(movie_star, star_year, star_id);
				ELSE
					CALL insert_star(movie_star, null, star_id);
				END IF;
            END IF;
            
            #insert star to sim
            INSERT INTO stars_in_movies(starId, movieId) VALUES(star_id, c);
            
		END IF;
		
		IF movie_genre IS NOT NULL THEN
			CALL insert_genre(movie_genre, genre_id);
			CALL insert_gim(c, genre_id);
		END IF;
    
        
		
        
	END$$

DELIMITER ;

-- 
-- DELIMITER $$
-- DROP PROCEDURE if exists add_movie$$
-- 
-- CREATE PROCEDURE add_movie(IN movie_title varchar(100), IN movie_year INT, IN movie_director varchar(100), IN movie_genre varchar(32), IN movie_star varchar(100), IN star_year INT)
-- 
-- 	BEGIN
--     
--     
-- 		DECLARE movie_id VARCHAR(10);
-- 		DECLARE new_movie_id VARCHAR(10);
-- 		DECLARE genre_id INT(11);
-- 		DECLARE star_id VARCHAR(10);
--         DECLARE max_id varchar(10) DEFAULT null;
--         DECLARE new_id_num int DEFAULT 0;
--         DECLARE c VARCHAR(10);
--         
-- 		IF (SELECT count(*) FROM movies WHERE title=movie_title AND year=movie_year AND director=movie_director)=0 THEN
-- 				SELECT max(id) from movies into max_id;
-- 				SELECT CAST(SUBSTRING(max_id, 3, 7) AS SIGNED) INTO new_id_num;
-- 				SET new_id_num = new_id_num + 1;
--                 #SELECT false into already_exists;
-- 				SELECT CONCAT("tt", new_id_num) into c;
-- 				INSERT INTO movies (id, title, year, director) VALUES (c, movie_title, movie_year, movie_director);
-- 		END IF;
-- 		
-- 		SELECT id FROM movies WHERE title=movie_title and year=movie_year and director=movie_director into c;
-- 		IF movie_star IS NOT NULL THEN
-- 			#star exists
-- 			IF (SELECT 1=1 FROM stars WHERE name = movie_star LIMIT 1) THEN
--             
-- 				select id from stars where name = movie_star LIMIT 1 into star_id ;
--                 select star_id as fu;
-- 				#INSERT INTO stars_in_movies VALUES(movie_star, c);
-- 			#star not exists
--             ELSE
-- 				IF star_year IS NOT NULL THEN
-- 					CALL insert_star(movie_star, star_year, star_id);
-- 				ELSE
-- 					CALL insert_star(movie_star, null, star_id);
-- 				END IF;
--             END IF;
--             
--             #insert star to sim
--             INSERT INTO stars_in_movies(starId, movieId) VALUES(star_id, c);
--             
-- 		END IF;
-- 		
-- 		IF movie_genre IS NOT NULL THEN
-- 			CALL insert_genre(movie_genre, genre_id);
-- 			CALL insert_gim(c, genre_id);
-- 		END IF;
--     
--         
-- 		
--         
-- 	END$$
-- 
-- DELIMITER ;

DELIMITER $$
DROP PROCEDURE if exists insert_genre$$

CREATE PROCEDURE insert_genre(IN genre_name varchar(32), OUT c varchar(10))

	BEGIN
		IF (SELECT 1=1 FROM genres WHERE name = genre_name) THEN
			BEGIN
				SELECT id from genres WHERE name = genre_name into c;
            END;
            ELSE
			BEGIN
				DECLARE max_id varchar(10) DEFAULT null;
				DECLARE new_id_num int DEFAULT 0;
				select max(id) from genres into max_id;
				SET new_id_num = max_id + 1;
                SELECT new_id_num into c;
				INSERT INTO genres (id, name) values (new_id_num, genre_name);
            END;
		END IF;
            
            
	END$$

DELIMITER ;


DELIMITER $$
DROP PROCEDURE if exists insert_gim$$

CREATE PROCEDURE insert_gim(IN movie_id varchar(10), IN genre_id varchar(32))

	BEGIN
		IF (SELECT 1=1 FROM genres_in_movies WHERE genreId = genre_id and movieId = movie_id) THEN
			BEGIN
				SELECT genreId from genres_in_movies  WHERE genreId = genre_id and movieId = movie_id;
            END;
		ELSE
			BEGIN
				INSERT INTO genres_in_movies (genreId, movieId) values (genre_id, movie_id);
            END;
		END IF;
            
            
	END$$

DELIMITER ;