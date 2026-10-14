package gal.usc.etse.es.restdemo.exception;

import gal.usc.etse.es.restdemo.model.entity.User;

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
