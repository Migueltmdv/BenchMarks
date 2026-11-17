package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.BenchLike;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de 'me gusta' por usuario.
 * Incluye una consulta JPQL de conteo, aunque hoy el like se gestiona
 * con el contador Bench.likes.
 */
@Repository
@NullMarked
public interface BenchLikeRepository extends JpaRepository<BenchLike, Long> {
    @Query("select count(bl) from BenchLike bl where bl.bench.benchID = :benchId and bl.user.userID = :userId")
    long countByBenchIdAndUserId(@Param("benchId") Long benchId, @Param("userId") String userId);
}
