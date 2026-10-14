package gal.usc.etse.es.restdemo.exception;

public class BenchNotFoundException extends Exception {
    private final Long id;
    public BenchNotFoundException(Long id) { this.id = id; }
    public Long getId() { return id; }
}
