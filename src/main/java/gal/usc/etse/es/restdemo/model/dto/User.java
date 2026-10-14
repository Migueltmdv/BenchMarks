package gal.usc.etse.es.restdemo.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.es.restdemo.model.entity.Role;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record User (
        @JsonView(Views.Public.class)
        @JsonAlias("username")
        String userID,
        
        @JsonView(Views.Public.class)
        String name,
        
        @JsonView(Views.Public.class)
        String lastName,
        
        @JsonView(Views.Public.class)
        String nickname,
        
        @JsonView(Views.Public.class)
        LocalDate birthday,
        
        @JsonView(Views.Public.class)
        String location,
        
        @JsonView(Views.Private.class)
        String password,
        
        @JsonView(Views.Private.class)
        Set<String> roles
) {
    public static User from(gal.usc.etse.es.restdemo.model.entity.User user) {
        return new User(
                user.getUserID(),
                user.getName(),
                user.getLastName(),
                user.getNickname(),
                user.getBirthday(),
                user.getLocation(),
                user.getPassword(),
                user.getRoles() == null ? Set.of() : user.getRoles().stream().map(Role::getRolename).collect(Collectors.toSet())
        );
    }

    public interface Views {
        interface Public {}
        interface Private extends Public {}
    }
}