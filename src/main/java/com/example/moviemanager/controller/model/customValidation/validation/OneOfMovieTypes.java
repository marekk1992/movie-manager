package com.example.moviemanager.controller.model.customValidation.validation;

import com.example.moviemanager.controller.model.customValidation.validator.OneOfMovieTypesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OneOfMovieTypesValidator.class)
public @interface OneOfMovieTypes {

    public String message() default "Movie type is invalid. Usage: 'Movie' or 'TV-Show'";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
