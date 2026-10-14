package gal.usc.etse.es.restdemo.exception;

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
