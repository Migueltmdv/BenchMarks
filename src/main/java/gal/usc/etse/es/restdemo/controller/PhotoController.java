package gal.usc.etse.es.restdemo.controller;

import gal.usc.etse.es.restdemo.exception.PhotoNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Photo;
import gal.usc.etse.es.restdemo.service.PhotoService;
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
@RequestMapping("photos")
public class PhotoController {
    private final PhotoService photos;

    @Autowired
    public PhotoController(PhotoService photos) { this.photos = photos; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> get(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int pagesize
    ) {
        return ResponseEntity.ok(photos.getPhotos(PageRequest.of(page, pagesize)));
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Photo> get(@PathVariable("id") Long id) throws PhotoNotFoundException {
        return ResponseEntity.ok(photos.getPhoto(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Photo> add(@RequestBody @Valid Photo photo) {
        Photo created = photos.addPhoto(photo);
        return ResponseEntity.created(MvcUriComponentsBuilder.fromMethodName(PhotoController.class, "get", created.photoID()).build().toUri())
                .body(created);
    }

    @PatchMapping(path = "{id}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Photo> update(@PathVariable("id") Long id, @RequestBody List<JsonPatchOperation> changes) throws PhotoNotFoundException {
        return ResponseEntity.ok(photos.updatePhoto(id, changes));
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws PhotoNotFoundException {
        photos.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
