package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad de foto (tabla 'photos').
 * Guarda los metadatos y la imagen binaria en la columna 'image_data' (bytea).
 * El binario no se serializa a JSON: se sirve mediante /photos/{id}/image.
 */
@Entity
@Table(name = "photos")
@SuppressWarnings("unused")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoID;

    @Column
    @NotBlank
    private String resolution;

    @Column
    @NotNull
    private Long size;

    @Column(name = "content_type")
    private String contentType;

    // Bytes reales de la imagen. No se envía en el JSON: se sirve aparte
    // mediante GET /photos/{id}/image.
    @Column(name = "image_data", columnDefinition = "bytea")
    private byte[] imageData;

    public static Photo from(gal.usc.etse.es.restdemo.model.dto.Photo photoDTO) {
        if (photoDTO == null) return null;
        return new Photo(
                photoDTO.photoID(),
                photoDTO.resolution(),
                photoDTO.size()
        );
    }

    public Photo() {}

    public Photo(Long photoID, String resolution, Long size) {
        this.photoID = photoID;
        this.resolution = resolution;
        this.size = size;
    }

    public Long getPhotoID() { return photoID; }
    public Photo setPhotoID(Long photoID) { this.photoID = photoID; return this; }

    public String getResolution() { return resolution; }
    public Photo setResolution(String resolution) { this.resolution = resolution; return this; }

    public Long getSize() { return size; }
    public Photo setSize(Long size) { this.size = size; return this; }

    public String getContentType() { return contentType; }
    public Photo setContentType(String contentType) { this.contentType = contentType; return this; }

    public byte[] getImageData() { return imageData; }
    public Photo setImageData(byte[] imageData) { this.imageData = imageData; return this; }
}