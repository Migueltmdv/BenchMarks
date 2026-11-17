package gal.usc.etse.es.restdemo.model.dto;

/**
 * DTO de entrada y salida de un banco.
 * Es un record inmutable con los datos que viajan por HTTP, no la entidad JPA.
 * from(entity) convierte la entidad en DTO; la entidad tiene el from(dto) inverso.
 */
public record Bench (
        Long benchID,
        String cords,
        String type,
        Integer likes,
        Photo photo
) {
    public static Bench from(gal.usc.etse.es.restdemo.model.entity.Bench bench) {
        if (bench == null) return null;
        return new Bench(
                bench.getBenchID(),
                bench.getCords(),
                bench.getType(),
                bench.getLikes(),
                Photo.from(bench.getPhoto())
        );
    }
}
