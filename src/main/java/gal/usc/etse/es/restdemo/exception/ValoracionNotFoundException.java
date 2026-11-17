package gal.usc.etse.es.restdemo.exception;

/**
 * Excepción de negocio: la valoración solicitada no existe.
 */
public class ValoracionNotFoundException extends Exception {
    private final Long id;
    public ValoracionNotFoundException(Long id) { this.id = id; }
    public Long getId() { return id; }
}
