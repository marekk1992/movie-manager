CREATE TABLE movie (
    id bigserial PRIMARY KEY,
    title VARCHAR(100),
    description VARCHAR(500),
    release_year INT,
    rating FLOAT(2)
);