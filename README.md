# Movie Manager REST API

## 1. About Movie Manager REST API
This API uses PostgreSQL database for storing movies data and provides ability to:
- add new movie;
- update existing movie;
- delete movie by it`s id;
- get all movies in library;
- get details about movie by it`s id.

API integrates with _TMDB - The Movie Database_ for retrieving movies and tv-shows data.

## 2. How to run this API
Before running this API, I assume that you have successfully installed the following tools on your computer:
- Git - for cloning repository from GitHub;
- Maven - for building and running project from terminal;
- JDK - required for Maven compilation;
- Docker - for running 'postgres' image;
- API client tool (e.g. Postman) or browser - for testing your API;

`Step 1` - in your terminal window start Postgres instance and docker container:

    $ docker run -it --name my_database -p 5432:5432 -e POSTGRES_USER=movieuser -e POSTGRES_PASSWORD=postgres123 postgres

*Notes
- Container name - my_database;
- Image - postgres;
- In PostgreSQL it is typical to use port 5432;
- PostgreSQL version - 15.1.

Now your database system is ready to accept connections.

`Step 2` - indicate your own API KEY for integration with TMDB:

Generate your own API KEY on https://www.themoviedb.org/ website and insert it into application's properties file `src/main/resources/application.yml`:
    
`tmdb:apiKey:` your key

`Step 3` - run API.

Navigate to root of the project and execute command:

    $ mvn spring-boot:run

Now you could open your API client tool or web browser and test application.

*This REST API is test covered. You could run tests by executing command:

    $ mvn clean test

## 3. Technologies and Frameworks

- SpringBoot;
- Spring Data Rest;
- PostgreSQL;
- Mockito;
- JUnit;
- Flyway;
- Testcontainers.