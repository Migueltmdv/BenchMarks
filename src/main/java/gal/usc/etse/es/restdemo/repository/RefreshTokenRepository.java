package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.RefreshToken;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * Repositorio CRUD de refresh tokens en Redis.
 * findByToken busca el UUID y deleteAllByUser invalida todas las sesiones
 * de un usuario (logout o rotación del token).
 */
@SuppressWarnings("UnusedReturnValue")
@NullMarked
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Collection<RefreshToken> deleteAllByUser(String user);
    Optional<RefreshToken> findByToken(String token);
}
