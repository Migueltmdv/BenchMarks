package gal.usc.etse.es.restdemo.model.dto;

import java.time.LocalDate;

/**
 * DTO de valoración.
 * Anida los DTO de usuario, banco y foto para que el JSON sea autocontenido
 * sin serializar entidades JPA ni provocar ciclos.
 */
public record Valoracion (
        Long reviewId,
        User reviewer, // Reutilizamos el DTO de User que creamos antes
        Bench benchOfReview,
        Photo photo,
        LocalDate date,
        Integer mark,
        String note
) {
    public static Valoracion from(gal.usc.etse.es.restdemo.model.entity.Valoracion valoracion) {
        if (valoracion == null) return null;
        return new Valoracion(
                valoracion.getReviewId(),
                User.from(valoracion.getReviewer()),
                Bench.from(valoracion.getBenchOfReview()),
                Photo.from(valoracion.getPhoto()),
                valoracion.getDate(),
                valoracion.getMark(),
                valoracion.getNote()
        );
    }
}