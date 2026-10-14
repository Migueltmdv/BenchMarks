package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@NullMarked
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserID(String userID);
    Page<User> findAll(Pageable pageRequest);
}
