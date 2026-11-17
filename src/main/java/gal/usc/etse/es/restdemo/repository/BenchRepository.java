package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.Bench;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Acceso a datos de bancos.
 * - Las derived queries generan el SQL a partir del nombre del método.
 * - Los DELETE nativos limpian las tablas relacionadas antes de borrar un banco
 *   (favoritos y likes), porque el modelo no tiene lado inverso con cascade.
 */
@Repository
@NullMarked
public interface BenchRepository extends JpaRepository<Bench, Long> {
    Page<Bench> findByTypeContainingIgnoreCase(String type, Pageable pageable);
    Page<Bench> findByTypeContainingIgnoreCaseOrCordsContainingIgnoreCase(String type, String cords, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM user_favorite_benches WHERE bench_id = :benchId", nativeQuery = true)
    void deleteFavoritesByBenchId(@Param("benchId") Long benchId);

    @Modifying
    @Query(value = "DELETE FROM bench_likes WHERE bench_id = :benchId", nativeQuery = true)
    void deleteLikesByBenchId(@Param("benchId") Long benchId);
}
