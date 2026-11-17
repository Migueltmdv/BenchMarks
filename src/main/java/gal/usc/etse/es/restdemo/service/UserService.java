package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.BenchNotFoundException;
import gal.usc.etse.es.restdemo.exception.DuplicateUserException;
import gal.usc.etse.es.restdemo.model.dto.Bench;
import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.model.entity.Role;
import gal.usc.etse.es.restdemo.repository.BenchRepository;
import gal.usc.etse.es.restdemo.repository.RoleRepository;
import gal.usc.etse.es.restdemo.repository.UserRepository;
import gal.usc.etse.es.utils.patch.JsonPatch;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lógica de negocio de usuarios.
 * Implementa UserDetailsService para que Spring Security pueda cargar
 * usuarios en el login. Gestiona alta, edición (PUT y JSON Patch), borrado
 * y los bancos favoritos del usuario autenticado.
 */
@Service
@NullMarked
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BenchRepository benchRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BenchRepository benchRepository, PasswordEncoder passwordEncoder, JsonMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.benchRepository = benchRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    // Punto de entrada de Spring Security para el login: usuario por userID.
    @Override
    public gal.usc.etse.es.restdemo.model.entity.User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserID(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    // v0 del listado: todos los usuarios (sin paginar).
    public List<User> get() {
        return userRepository.findAll().stream().map(User::from).toList();
    }

    // v1 del listado: página de usuarios.
    public Page<User> get(PageRequest page) {
        return userRepository.findAll(page).map(User::from);
    }

    public User get(String userID){
        return User.from(loadUserByUsername(userID));
    }

    // Registro: comprueba duplicado, codifica la contraseña y asigna rol USER.
    public User create(User user) throws DuplicateUserException {
        var dbUser = userRepository.findByUserID(user.userID());
        if (dbUser.isPresent()) {
            throw new DuplicateUserException(dbUser.get());
        }

        Role userRole = roleRepository.findByRolename("USER");

        gal.usc.etse.es.restdemo.model.entity.User entity = gal.usc.etse.es.restdemo.model.entity.User.from(user, passwordEncoder);
        if (entity.getRoles() == null) entity.setRoles(new java.util.HashSet<>());
        if (userRole != null) entity.addRole(userRole);

        return User.from(userRepository.save(entity));
    }

    // PUT: solo sobrescribe los campos que llegan con valor.
    public User update(String userID, User changes) {
        gal.usc.etse.es.restdemo.model.entity.User db = loadUserByUsername(userID);
        if (changes.name() != null) db.setName(changes.name());
        if (changes.lastName() != null) db.setLastName(changes.lastName());
        if (changes.nickname() != null) db.setNickname(changes.nickname());
        if (changes.birthday() != null) db.setBirthday(changes.birthday());
        if (changes.location() != null) db.setLocation(changes.location());
        if (changes.password() != null && !changes.password().isBlank()) {
            db.setPassword(passwordEncoder.encode(changes.password()));
        }
        return User.from(userRepository.save(db));
    }

    // PATCH: aplica JSON Patch sobre el DTO y conserva el userID original.
    public User update(String userID, List<JsonPatchOperation> changes) throws UsernameNotFoundException {
        User user = get(userID);
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(user, JsonNode.class));
        User updated = mapper.convertValue(patched, User.class);
        gal.usc.etse.es.restdemo.model.entity.User db = loadUserByUsername(userID);
        // preserve userID
        updated = new User(userID, updated.name(), updated.lastName(), updated.nickname(), updated.birthday(), updated.location(),
                updated.password(), updated.roles());
        if (updated.password() != null && !updated.password().isBlank() && !updated.password().equals(user.password())) {
            db.setPassword(passwordEncoder.encode(updated.password()));
        }
        if (updated.name() != null) db.setName(updated.name());
        if (updated.lastName() != null) db.setLastName(updated.lastName());
        if (updated.nickname() != null) db.setNickname(updated.nickname());
        if (updated.birthday() != null) db.setBirthday(updated.birthday());
        if (updated.location() != null) db.setLocation(updated.location());
        return User.from(userRepository.save(db));
    }

    public void delete(String userID) {
        userRepository.findByUserID(userID).ifPresent(userRepository::delete);
    }

    // --- Bancos favoritos del usuario autenticado ---

    @Transactional
    public void addFavoriteBench(String userID, Long benchID) throws UsernameNotFoundException, BenchNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.User user = loadUserByUsername(userID);
        gal.usc.etse.es.restdemo.model.entity.Bench bench = benchRepository.findById(benchID)
                .orElseThrow(() -> new BenchNotFoundException(benchID));

        user.getFavoriteBenches().add(bench);
        userRepository.save(user);
    }

    @Transactional
    public void removeFavoriteBench(String userID, Long benchID) throws UsernameNotFoundException, BenchNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.User user = loadUserByUsername(userID);
        gal.usc.etse.es.restdemo.model.entity.Bench bench = benchRepository.findById(benchID)
                .orElseThrow(() -> new BenchNotFoundException(benchID));

        user.getFavoriteBenches().remove(bench);
        userRepository.save(user);
    }

    // La transacción mantiene abierta la sesión mientras se recorre el Set LAZY
    // y se convierte cada entidad en DTO.
    @Transactional(readOnly = true)
    public Set<Bench> getFavoriteBenches(String userID) throws UsernameNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.User user = loadUserByUsername(userID);
        return user.getFavoriteBenches().stream()
                .map(Bench::from)
                .collect(Collectors.toSet());
    }
}