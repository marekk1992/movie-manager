package com.example.moviemanager.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    @NotBlank(message = "Title is mandatory.")
    @Size(max = 100, message = "Title can`t be longer than 100 characters.")
    private String title;

    @Column(name = "description")
    @Size(max = 500, message = "Description can`t be longer than 500 characters.")
    private String description;

    @Column(name = "release_year")
    @Min(value = 1888, message = "First official movie was released in 1888, please check your input.")
    private int releaseYear;

    @Column(name = "rating")
    @Min(value = 0, message = "Rating can`t be negative.")
    @Max(value = 10, message = "Rating can`t be higher than 10.")
    private double rating;

    public Movie() {
    }

    public Movie(int id, String title, String description, int releaseYear, double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", releaseYear=" + releaseYear +
                ", rating=" + rating +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Movie movie = (Movie) obj;

        return this.id.equals(movie.id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public double getRating() {
        return rating;
    }
}
