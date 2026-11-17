package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.PhotoNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Photo;
import gal.usc.etse.es.restdemo.model.dto.PhotoUpload;
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

import java.util.Base64;
import java.util.List;

/**
 * Lógica de negocio de fotos.
 * - addPhoto decodifica el Base64 y guarda los bytes en la BD.
 * - getPhoto devuelve solo metadatos; getPhotoEntity se usa para el binario.
 * - updatePhoto aplica JSON Patch sobre resolución y tamaño.
 */
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

    // Alta: decodifica el Base64 y completa los metadatos que falten.
    public Photo addPhoto(PhotoUpload upload) {
        byte[] imageData = decode(upload.data());
        gal.usc.etse.es.restdemo.model.entity.Photo entity = new gal.usc.etse.es.restdemo.model.entity.Photo();
        entity.setResolution(upload.resolution() == null ? "Desconocida" : upload.resolution());
        entity.setSize(upload.size() != null ? upload.size() : (imageData == null ? 0L : (long) imageData.length));
        entity.setContentType(upload.contentType() == null ? "image/jpeg" : upload.contentType());
        entity.setImageData(imageData);
        return Photo.from(photos.save(entity));
    }

    // Acepta tanto Base64 puro como una data URL ("data:image/jpeg;base64,...").
    private static byte[] decode(String data) {
        if (data == null || data.isBlank()) return null;
        String base64 = data;
        int comma = base64.indexOf(',');
        if (base64.startsWith("data:") && comma >= 0) base64 = base64.substring(comma + 1);
        return Base64.getDecoder().decode(base64);
    }

    public Page<Photo> getPhotos(PageRequest page) {
        return photos.findAll(page).map(Photo::from);
    }

    public Photo getPhoto(Long id) throws PhotoNotFoundException {
        return Photo.from(getPhotoEntity(id));
    }

    // Devuelve la entidad completa (incluidos los bytes) para /photos/{id}/image.
    public gal.usc.etse.es.restdemo.model.entity.Photo getPhotoEntity(Long id) throws PhotoNotFoundException {
        return photos.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
    }

    public Photo updatePhoto(Long id, List<JsonPatchOperation> changes) throws PhotoNotFoundException {
        Photo photo = photos.findById(id).map(Photo::from).orElseThrow(() -> new PhotoNotFoundException(id));
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(photo, JsonNode.class));
        Photo updated = mapper.convertValue(patched, Photo.class);
        gal.usc.etse.es.restdemo.model.entity.Photo entity = photos.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
        entity.setResolution(updated.resolution()).setSize(updated.size());
        return Photo.from(photos.save(entity));
    }

    public void deletePhoto(Long id) throws PhotoNotFoundException {
        if (!photos.existsById(id)) throw new PhotoNotFoundException(id);
        photos.deleteById(id);
    }
}
