# Movie Manager REST API

## 1. About Movie Manager REST API
This API uses PostgreSQL database for storing movies data and provides ability to:
- add new movie;
- update existing movie;
- delete movie by it`s id;
- get all movies in library;
- get details about movie by it`s id.

You could use this API to create your own watchlist of movies and TV-shows.

## 2. How to run this API
Before running this API, I assume that you have successfully installed the following tools on your computer:
- Git - for cloning repository from GitHub;
- Maven - for building and running project from terminal;
- JDK - required for Maven compilation;
- Docker - for running 'postgres' image;
- API client tool (e.g. Postman) or browser - for testing your API;
- Database management tool (e.g. DBeaver) - for connecting to database.

`Step 1` - in your terminal window start Postgres instance and docker container:

    $ docker run -it --name my_database -p 5432:5432 -e POSTGRES_USER=movieuser -e POSTGRES_PASSWORD=postgres123 postgres

*Notes:
- Container name - my_database;
- Image - postgres;
- In PostgreSQL it is typical to use port 5432.

Now your database system is ready to accept connections.
You could connect to DB in any tool that allows you to communicate with it.

`Step 2` - use the following connection details to actually connect to the DB in your database management tool:

- Host: localhost;
- Port: 5432;
- User: movieuser;
- Password: postgres123.

`Step 3` - make a copy of repository at your location.
Navigate to your preferred directory where your want to place repository:

    $ cd <your_directory>

Clone this repository:

    $ git clone <repo>

`Step 4` - in your DB tool execute `movie_directory.sql` script.
You could find it in cloned repo `src/main/resources/`. After executing script 'movie_directory' schema
and 'movie' table with five columns inside: 'id', 'title', 'description', 'release_year' and 'rating' will be created.

`Step 5` - indicate DB connection details in your application's properties file `src/main/resources/application.properties`:
- `spring.datasource.url` = jdbc:postgresql://localhost:5432/postgres
- `spring.datasource.username` = movieuser
- `spring.datasource.password` = postgres123
- `spring.datasource.hikari.schema` = movie_directory

*In my project I have used default 'postgres' database.
You are free to use different connection details but remember to change them in all occurrences.

`Step 6` - run API.

Navigate to root the project and execute command:

    $ mvn spring-boot:run

Now you could open your API client tool or web browser and test application.

*This API is tests covered. You could run them by executing command in root of the project:

    $ mvn clean test

## 3. How to use API

The Movie Manager REST API supports following Http requests:

- `GET` - for retrieving all movies from database, or specific movie by indicating it`s id;
- `POST` - for creating new movie;
- `DELETE` - for deleting existing movie;
- `PUT` - for updating existing movie;

## 4. Technologies and Frameworks

- SpringBoot;
- Spring Data Rest;
- PostgreSQL;
- Mockito;
- JUnit;