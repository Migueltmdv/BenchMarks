package gal.usc.etse.es.restdemo.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * Documento Redis (no tabla SQL) que representa un refresh token.
 * La clave es el propio UUID y 'ttl' indica a Redis cuándo debe caducarlo.
 * Se usa para renovar el JWT sin volver a pedir la contraseña.
 */
@SuppressWarnings("unused")
@RedisHash()
public class RefreshToken {
    // El propio UUID es la clave del documento en Redis.
    @Id
    private String token;

    // Usuario propietario del refresh token.
    private String user;

    // Segundos que Redis mantendrá vivo el documento (7 días).
    @TimeToLive
    private long ttl;

    public RefreshToken() { }

    public RefreshToken(String token, String user, long ttl) {
        this.token = token;
        this.user = user;
        this.ttl = ttl;
    }

    public String getToken() {
        return token;
    }

    public RefreshToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUser() {
        return user;
    }

    public RefreshToken setUser(String user) {
        this.user = user;
        return this;
    }

    public long getTtl() {
        return ttl;
    }

    public RefreshToken setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }
}
