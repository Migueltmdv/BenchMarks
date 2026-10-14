package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.RefreshToken;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("UnusedReturnValue")
@NullMarked
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Collection<RefreshToken> deleteAllByUser(String user);
    Optional<RefreshToken> findByToken(String token);
}
