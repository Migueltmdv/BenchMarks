package gal.usc.etse.es.restdemo.controller;

import gal.usc.etse.es.restdemo.exception.ValoracionNotFoundException;
import com.fasterxml.jackson.annotation.JsonView;
import gal.usc.etse.es.restdemo.model.dto.Valoracion;
import gal.usc.etse.es.restdemo.model.dto.User;
import gal.usc.etse.es.restdemo.service.ValoracionService;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;

@NullMarked
@RestController
@RequestMapping("valoraciones")
@JsonView(User.Views.Public.class)
public class ValoracionController {
    private final ValoracionService reviews;

    @Autowired
    public ValoracionController(ValoracionService reviews) { this.reviews = reviews; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> get(
            @RequestParam(value = "benchId", required = false) Long benchId,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int pagesize
    ) {
        return ResponseEntity.ok(reviews.getValoraciones(benchId, userId, PageRequest.of(page, pagesize)));
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Valoracion> get(@PathVariable("id") Long id) throws ValoracionNotFoundException {
        return ResponseEntity.ok(reviews.getValoracion(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Valoracion> add(@RequestBody @Valid Valoracion review) {
        Valoracion created = reviews.addValoracion(review);
        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(ValoracionController.class, "get", created.reviewId()).build().toUri())
                .body(created);
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Valoracion> update(@PathVariable("id") Long id, @RequestBody List<JsonPatchOperation> changes) throws ValoracionNotFoundException {
        return ResponseEntity.ok(reviews.updateValoracion(id, changes));
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws ValoracionNotFoundException {
        reviews.deleteValoracion(id);
        return ResponseEntity.noContent().build();
    }
}
