package gal.usc.etse.es.restdemo.controller;

import gal.usc.etse.es.restdemo.exception.BenchNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Bench;
import gal.usc.etse.es.restdemo.service.BenchService;
import gal.usc.etse.es.restdemo.service.UserService;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@NullMarked
@RestController
@RequestMapping("benches")
public class BenchController {
    private final BenchService benches;
    private final UserService userService;

    @Autowired
    public BenchController(BenchService benches, UserService userService) {
        this.benches = benches;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> get(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int pagesize,
            @RequestParam(value = "sort", required = false, defaultValue = "") List<String> sort
    ) {
        return ResponseEntity.ok(benches.getBenches(type, q, PageRequest.of(page, pagesize,
                Sort.by(sort.stream().map(key -> key.startsWith("-") ? Sort.Order.desc(key.substring(1)) : Sort.Order.asc(key)).toList()))));
    }

    // --- ENDPOINTY DLA ULUBIONYCH (FAVORITOS) ---

    @GetMapping(path = "favorites", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Set<Bench>> getFavorites(Principal principal) {
        return ResponseEntity.ok(userService.getFavoriteBenches(principal.getName()));
    }

    @PostMapping(path = "{id}/favorite", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> addFavorite(@PathVariable("id") Long id, Principal principal) throws BenchNotFoundException {
        userService.addFavoriteBench(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "{id}/favorite")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> removeFavorite(@PathVariable("id") Long id, Principal principal) throws BenchNotFoundException {
        userService.removeFavoriteBench(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Bench> get(@PathVariable("id") Long id) throws BenchNotFoundException {
        return ResponseEntity.ok(benches.getBench(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Bench> add(@RequestBody @Valid Bench bench) {
        Bench created = benches.addBench(bench);
        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(BenchController.class, "get", created.benchID()).build().toUri())
                .body(created);
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Bench> update(@PathVariable("id") Long id, @RequestBody List<JsonPatchOperation> changes) throws BenchNotFoundException {
        return ResponseEntity.ok(benches.updateBench(id, changes));
    }

    @PostMapping(path = "{id}/like", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Bench> like(@PathVariable("id") Long id) throws BenchNotFoundException {
        return ResponseEntity.ok(benches.likeBench(id));
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws BenchNotFoundException {
        benches.deleteBench(id);
        return ResponseEntity.noContent().build();
    }
}