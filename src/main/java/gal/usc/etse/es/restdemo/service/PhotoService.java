package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.PhotoNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Photo;
import gal.usc.etse.es.restdemo.repository.PhotoRepository;
import gal.usc.etse.es.utils.patch.JsonPatch;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
@NullMarked
public class PhotoService {
    private final PhotoRepository photos;
    private final JsonMapper mapper;

    @Autowired
    public PhotoService(PhotoRepository photos, JsonMapper mapper) {
        this.photos = photos;
        this.mapper = mapper;
    }

    public Photo addPhoto(Photo photo) {
        gal.usc.etse.es.restdemo.model.entity.Photo saved =
                photos.save(gal.usc.etse.es.restdemo.model.entity.Photo.from(photo));
        return Photo.from(saved);
    }

    public Page<Photo> getPhotos(PageRequest page) {
        return photos.findAll(page).map(Photo::from);
    }

    public Photo getPhoto(Long id) throws PhotoNotFoundException {
        return photos.findById(id).map(Photo::from).orElseThrow(() -> new PhotoNotFoundException(id));
    }

    public Photo updatePhoto(Long id, List<JsonPatchOperation> changes) throws PhotoNotFoundException {
        Photo photo = photos.findById(id).map(Photo::from).orElseThrow(() -> new PhotoNotFoundException(id));
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(photo, JsonNode.class));
        Photo updated = mapper.convertValue(patched, Photo.class);
        gal.usc.etse.es.restdemo.model.entity.Photo entity =
                gal.usc.etse.es.restdemo.model.entity.Photo.from(updated);
        entity.setPhotoID(id);
        return Photo.from(photos.save(entity));
    }

    public void deletePhoto(Long id) throws PhotoNotFoundException {
        if (!photos.existsById(id)) throw new PhotoNotFoundException(id);
        photos.deleteById(id);
    }
}
