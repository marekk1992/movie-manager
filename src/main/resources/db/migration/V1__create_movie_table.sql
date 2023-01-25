create table movie (
    id bigserial primary key,
    title varchar(100),
    description varchar(500),
    release_year int,
    rating float(2)
);