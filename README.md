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
- Maven - for building project;
- JDK - required for Maven compilation;
- Docker - for deploying application;
- Docker-compose - for running multi-container Docker applications;
- API client tool (e.g. Postman) or browser - for testing your API.

`Step 1` - indicate your own API KEY for integration with TMDB:

Generate your own API KEY on https://www.themoviedb.org/ website and insert it into application's properties file `src/main/resources/application.yml`:

`tmdb:apiKey: your_key`

`Step 2` - start you project services:

Navigate to root of the project and execute command:

    $ docker-compose up -d

This will start PostgreSQL and application services in the background.

*This REST API is test covered. You could run tests by executing command:

    $ mvn clean test

## 3. Technologies and Frameworks

- SpringBoot;
- Spring Data Rest;
- PostgreSQL;
- Docker-compose;
- Mockito;
- JUnit;
- Flyway;
- Testcontainers;
- WireMock.