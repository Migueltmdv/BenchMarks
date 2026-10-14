package gal.usc.etse.es.restdemo.controller;

import gal.usc.etse.es.restdemo.exception.BenchNotFoundException;
import gal.usc.etse.es.restdemo.exception.PhotoNotFoundException;
import gal.usc.etse.es.restdemo.exception.ValoracionNotFoundException;
import gal.usc.etse.es.restdemo.exception.DuplicateUserException;
import gal.usc.etse.es.restdemo.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(
            responseCode = "401",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ErrorResponse handle(BadCredentialsException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        error.setDetail("Provided credentials do not match any registered user");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "bad-credentials").build().toUri());
        error.setTitle(ex.getMessage());

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(
            responseCode = "401",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ErrorResponse handle(JwtException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        error.setDetail(ex.getMessage());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "expired-token").build().toUri());
        error.setTitle("The token has expired");

        return ErrorResponse.builder(ex, error).build();
    }


    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(
            responseCode = "409",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ErrorResponse handle(DuplicateUserException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        error.setDetail("Username "+ex.getUser().getUsername()+" already exists in the database with the following data: "+ex.getUser().toString());
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "duplicated-user").build().toUri());
        error.setTitle("User already exists!");

        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(
            responseCode = "401",
            content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    public ErrorResponse handle(InvalidRefreshTokenException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        error.setDetail("Refresh token "+ex.getToken()+" is invalid");
        error.setType(MvcUriComponentsBuilder.fromController(ErrorController.class).pathSegment("error", "refresh-token-user").build().toUri());
        error.setTitle("Invalid refresh token!");

        return ErrorResponse.builder(ex, error).build();
    }


    @ExceptionHandler(BenchNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(BenchNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("Bench with id="+ex.getId()+" not found");
        error.setTitle("Bench not found");
        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(PhotoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(PhotoNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("Photo with id="+ex.getId()+" not found");
        error.setTitle("Photo not found");
        return ErrorResponse.builder(ex, error).build();
    }

    @ExceptionHandler(ValoracionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(ValoracionNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setDetail("Valoracion with id="+ex.getId()+" not found");
        error.setTitle("Valoracion not found");
        return ErrorResponse.builder(ex, error).build();
    }
}
