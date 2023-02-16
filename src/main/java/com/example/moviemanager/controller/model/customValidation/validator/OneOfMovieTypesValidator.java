package com.example.moviemanager.controller.model.customValidation.validator;

import com.example.moviemanager.controller.model.customValidation.validation.OneOfMovieTypes;
import com.example.moviemanager.constantValues.MovieType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class OneOfMovieTypesValidator implements ConstraintValidator<OneOfMovieTypes, String> {

    @Override
    public boolean isValid(String movieType, ConstraintValidatorContext constraintValidatorContext) {
        List<String> validMovieTypes = Arrays.asList(
                MovieType.MOVIE.name(),
                MovieType.TVSHOW.name()
        );

        return validMovieTypes.contains(movieType.toUpperCase().trim().replace("-", ""));
    }
}
