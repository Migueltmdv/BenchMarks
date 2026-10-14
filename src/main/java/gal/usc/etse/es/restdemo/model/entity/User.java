package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Entity
@Table(name="users")
public class User implements UserDetails {
    
    // --- NUEVOS ATRIBUTOS DEL PROYECTO ---
    @Id
    @Column(name = "user_id")
    private String userID;
    
    private String name;
    private String lastName;
    private String nickname;
    private LocalDate birthday;
    private String location;

    // --- ATRIBUTOS DE SEGURIDAD MANTENIDOS ---
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    private Set<Role> roles;

    public static User from(gal.usc.etse.es.restdemo.model.dto.User user, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        if (user == null) return null;
        return new User(
                user.userID(),
                user.name(),
                user.lastName(),
                user.nickname(),
                user.birthday(),
                user.location(),
                user.password() == null || user.password().isBlank() ? null : passwordEncoder.encode(user.password()),
                user.roles() == null ? new java.util.HashSet<>() : new java.util.HashSet<>()
        );
    }

    // --- CONSTRUCTORES ---
    public User() {}

    public User(String userID, String name, String lastName, String nickname, LocalDate birthday, String location, String password, Set<Role> roles) {
        this.userID = userID;
        this.name = name;
        this.lastName = lastName;
        this.nickname = nickname;
        this.birthday = birthday;
        this.location = location;
        this.password = password;
        this.roles = roles;
    }

    // --- MÉTODOS DE SPRING SECURITY (UserDetails) ---
    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) return new HashSet<>();
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRolename()))
                .toList();
    }

    @Override
    @NonNull
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        // En Spring Security "username" es el identificador único. Usamos userID.
        // Si prefieres que inicien sesión con su nickname, cambia esto por 'return nickname;'
        return userID; 
    }

    // --- GETTERS Y SETTERS ---
    
    @NonNull
    public Set<Role> getRoles() {
        return roles;
    }

    public User setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public User addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
        return this;
    }

    public String getUserID() { return userID; }
    public User setUserID(String userID) { this.userID = userID; return this; }

    public String getName() { return name; }
    public User setName(String name) { this.name = name; return this; }

    public String getLastName() { return lastName; }
    public User setLastName(String lastName) { this.lastName = lastName; return this; }

    public String getNickname() { return nickname; }
    public User setNickname(String nickname) { this.nickname = nickname; return this; }

    public LocalDate getBirthday() { return birthday; }
    public User setBirthday(LocalDate birthday) { this.birthday = birthday; return this; }

    public String getLocation() { return location; }
    public User setLocation(String location) { this.location = location; return this; }

    public User setPassword(String password) { this.password = password; return this; }
}