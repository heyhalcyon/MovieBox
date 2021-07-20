DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies(
	`id` varchar(10) NOT NULL,
    `title` varchar(100) NOT NULL,
	`year` int NOT NULL,
    `director` varchar(100) NOT NULL,
    
    primary key(`id`),
    FULLTEXT (title)
    
);

CREATE TABLE stars(
	`id` varchar(10) NOT NULL,
    `name` varchar(100) NOT NULL,
    `birthYear` int,
    
    primary key(`id`)

);

CREATE TABLE stars_in_movies(
	`starId` varchar(10) NOT NULL,
    `movieId` varchar(10) NOT NULL,
    
    foreign key(`starId`)
		references stars(`id`),
	
    foreign key(`movieId`)
		references movies(`id`)

);

CREATE TABLE genres(
	`id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(32) NOT NULL,
    
    primary key(`id`)

);

CREATE TABLE genres_in_movies(
	`genreId` int NOT NULL,
    `movieId` varchar(10) NOT NULL,
    
    foreign key(`genreId`)
		references genres(`id`),
	
    foreign key(`movieId`)
		references movies(`id`)

);

CREATE TABLE creditcards(
	`id` varchar(20) NOT NULL,
    `firstName` varchar(50) NOT NULL,
    `lastName` varchar(50) NOT NULL,
    `expiration` date NOT NULL,
    
    primary key(`id`)
    
);

CREATE TABLE customers(
	`id` int NOT NULL AUTO_INCREMENT,
    `firstName` varchar(50) NOT NULL,
	`lastName` varchar(50) NOT NULL,
    `ccId` varchar(20) NOT NULL,
    `address` varchar(200) NOT NULL,
    `email` varchar(50) NOT NULL,
    `password` varchar(20) NOT NULL,
    
    foreign key(`ccId`)
		references creditcards(`id`),
        
	primary key(`id`)

);

CREATE TABLE sales(
	`id` int NOT NULL AUTO_INCREMENT,
	`customerId` int NOT NULL,
    `movieId` varchar(10) NOT NULL,
	`saleDate` date NOT NULL,
    
    foreign key(`customerId`)
		references customers(`id`),
        
	foreign key(`movieId`)
		references movies(`id`),
        
	primary key(`id`)

);

CREATE TABLE ratings(
	`movieId` varchar(10) NOT NULL,
    `rating` float NOT NULL,
    `numVotes` int NOT NULL,
    
    foreign key(`movieId`)
		references movies(`id`)

);

CREATE TABLE `employees`(
	`email` varchar(50) primary key,
	`password` varchar(128) not null,
	`fullname` varchar(100)
)


