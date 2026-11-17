package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Permiso granular de la aplicación (por ejemplo 'users:read').
 * Se asigna a roles mediante la tabla role_permissions.
 * La jerarquía de roles los convierte en autoridades tipo 'users:read'.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    private String id;
    private String resource;
    private String action;

    public Permission() {}

    public String getId() {
        return this.id;
    }

    public Permission setId(String id) {
        this.id = id;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public Permission setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Permission setAction(String action) {
        this.action = action;
        return this;
    }
}
