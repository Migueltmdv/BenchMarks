package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.Bench;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface BenchRepository extends JpaRepository<Bench, Long> {
    Page<Bench> findByTypeContainingIgnoreCase(String type, Pageable pageable);
    Page<Bench> findByTypeContainingIgnoreCaseOrCordsContainingIgnoreCase(String type, String cords, Pageable pageable);
}
