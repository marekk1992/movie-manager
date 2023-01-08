drop schema if exists movie_directory CASCADE;

create schema movie_directory;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA movie_directory TO movieuser;

drop table if exists movie;

create table if not exists movie_directory.movie (
id SERIAL primary key,
title varchar(100) null,
description varchar(500) null,
release_year int null,
rating float(2) null);
