package gal.usc.etse.es.restdemo.repository;

import gal.usc.etse.es.restdemo.model.entity.Role;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acceso a roles por nombre.
 * Se usa para asignar el rol USER al registrar y para construir la jerarquía
 * de roles que consume Spring Security.
 */
@Repository
@NullMarked
public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByRolename(String rolename);
}
