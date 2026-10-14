package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.Valoracion;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    Page<Valoracion> findByBenchOfReview_BenchID(Long benchId, Pageable pageable);
    Page<Valoracion> findByReviewer_UserID(String userId, Pageable pageable);
}
