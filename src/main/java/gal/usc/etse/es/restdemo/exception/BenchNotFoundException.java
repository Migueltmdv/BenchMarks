package gal.usc.etse.es.restdemo.exception;

/**
 * Excepción de negocio: el banco solicitado no existe.
 * Guarda el id para que ErrorController construya el mensaje de error.
 */
public class BenchNotFoundException extends Exception {
    private final Long id;
    public BenchNotFoundException(Long id) { this.id = id; }
    public Long getId() { return id; }
}
