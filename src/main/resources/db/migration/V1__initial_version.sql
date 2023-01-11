create table movie_directory.movie (
id SERIAL primary key,
title varchar(100) null,
description varchar(500) null,
release_year int null,
rating float(2) null);