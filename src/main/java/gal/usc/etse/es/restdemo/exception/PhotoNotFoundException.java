package gal.usc.etse.es.restdemo.exception;

/**
 * Excepción de negocio: la foto solicitada no existe.
 */
public class PhotoNotFoundException extends Exception {
    private final Long id;
    public PhotoNotFoundException(Long id) { this.id = id; }
    public Long getId() { return id; }
}
