package gal.usc.etse.es.restdemo.model.dto;

public record Photo (
        Long photoID,
        String resolution,
        Long size
) {
    public static Photo from(gal.usc.etse.es.restdemo.model.entity.Photo photo) {
        if (photo == null) return null;
        return new Photo(
                photo.getPhotoID(),
                photo.getResolution(),
                photo.getSize()
        );
    }
}