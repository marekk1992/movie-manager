create table movie (
    id SERIAL primary key,
    title varchar(100),
    description varchar(500),
    release_year int,
    rating float(2)
);