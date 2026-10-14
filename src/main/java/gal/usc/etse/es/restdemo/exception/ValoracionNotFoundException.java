package gal.usc.etse.es.restdemo.exception;

public class ValoracionNotFoundException extends Exception {
    private final Long id;
    public ValoracionNotFoundException(Long id) { this.id = id; }
    public Long getId() { return id; }
}
