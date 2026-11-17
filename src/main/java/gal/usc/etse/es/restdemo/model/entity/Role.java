package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;

import java.util.Set;

/**
 * Rol de seguridad ('USER', 'ADMIN').
 * - 'includes' define la jerarquía entre roles (ADMIN incluye USER).
 * - 'permissions' asocia permisos granulares al rol.
 */
@Entity
@Table(name = "roles")
@SuppressWarnings("unused")
public class Role {
    @Id
    private String rolename;

    // Roles que este rol incluye (jerarquía): ADMIN -> USER.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_hierarchy",
            joinColumns = @JoinColumn(name = "role"),
            inverseJoinColumns = @JoinColumn(name = "includes"))
    private Set<Role> includes;

    // Permisos granulares del rol (p. ej. users:read).
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role"),
            inverseJoinColumns = @JoinColumn(name = "permission"))
    private Set<Permission> permissions;

    public Role() { }

    public Role(String rolename, Set<Role> includes, Set<Permission> permissions) {
        this.rolename = rolename;
        this.includes = includes;
        this.permissions = permissions;
    }

    public String getRolename() {
        return rolename;
    }

    public Role setRolename(String rolename) {
        this.rolename = rolename;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Role setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Set<Role> getIncludes() {
        return includes;
    }

    public Role setIncludes(Set<Role> includes) {
        this.includes = includes;
        return this;
    }
}
