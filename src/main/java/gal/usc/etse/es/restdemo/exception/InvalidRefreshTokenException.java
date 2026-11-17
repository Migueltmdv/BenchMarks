package gal.usc.etse.es.restdemo.exception;

/**
 * Excepción de negocio: el refresh token no existe en Redis o ya caducó.
 */
public class InvalidRefreshTokenException extends Exception {
    private final String token;

    public InvalidRefreshTokenException(String token) {
        super("Invalid refresh token ");
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
