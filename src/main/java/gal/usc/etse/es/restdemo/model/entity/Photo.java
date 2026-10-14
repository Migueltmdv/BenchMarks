package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
}