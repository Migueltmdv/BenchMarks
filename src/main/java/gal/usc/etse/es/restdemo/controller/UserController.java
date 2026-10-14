package gal.usc.etse.es.restdemo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.service.UserService;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@NullMarked
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService users;

    @Autowired
    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, version = "0")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @JsonView(User.Views.Public.class)
    public ResponseEntity<List<User>> get() {
        return ResponseEntity.ok(users.get());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, version = "1")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @JsonView(User.Views.Public.class)
    public ResponseEntity<Page<User>> get(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int pagesize,
            @RequestParam(value = "sort", required = false, defaultValue = "") List<String> sort
    ) {
        return ResponseEntity.ok(
                users.get(
                        PageRequest.of(
                                page,
                                pagesize,
                                Sort.by(sort.stream().map(key -> key.startsWith("-") ? Sort.Order.desc(key.substring(1)) : Sort.Order.asc(key)).toList())
                        )
                )
        );
    }

    @GetMapping(path = "{userID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @JsonView(User.Views.Public.class)
    public ResponseEntity<User> get(@PathVariable("userID") String userID){
        return ResponseEntity.ok(users.get(userID));
    }

    @PutMapping(path = "{userID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @JsonView(User.Views.Public.class)
    public ResponseEntity<User> update(@PathVariable("userID") String userID, @RequestBody User user) {
        return ResponseEntity.ok(users.update(userID, user));
    }

    @PatchMapping(path = "{userID}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @JsonView(User.Views.Public.class)
    public ResponseEntity<User> update(@PathVariable("userID") String userID, @RequestBody List<JsonPatchOperation> changes) {
        return ResponseEntity.ok(users.update(userID, changes));
    }

    @DeleteMapping(path = "{userID}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("userID") String userID) {
        users.delete(userID);
        return ResponseEntity.noContent().build();
    }
}
