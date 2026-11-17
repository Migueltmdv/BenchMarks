package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.Photo;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio CRUD de fotos (metadatos y binario).
 */
@Repository
@NullMarked
public interface PhotoRepository extends JpaRepository<Photo, Long> {}
