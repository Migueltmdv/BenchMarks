package gal.usc.etse.es.restdemo.model.dto;

public record Bench (
        Long benchID,
        String cords,
        String type,
        Integer likes
) {
    public static Bench from(gal.usc.etse.es.restdemo.model.entity.Bench bench) {
        if (bench == null) return null;
        return new Bench(
                bench.getBenchID(),
                bench.getCords(),
                bench.getType(),
                bench.getLikes()
        );
    }
}