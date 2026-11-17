package gal.usc.etse.es.restdemo.exception;

import gal.usc.etse.es.restdemo.model.entity.User;

/**
 * Excepción de negocio: ya existe un usuario con ese identificador.
 * Transporta la entidad existente para incluirla en el error 409.
 */
public class DuplicateUserException extends Exception {
    private final User user;

    public DuplicateUserException(User user) {
        this.user = user;

        super("User already exists!");
    }

    public User getUser() {
        return user;
    }
}
