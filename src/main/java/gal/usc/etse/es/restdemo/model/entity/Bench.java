package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * Entidad que representa un banco público (tabla 'benches').
 * - La foto es opcional y vive en la tabla 'photos'.
 * - cascade + orphanRemoval: al borrar el banco se borra su foto.
 * - equals/hashCode por ID porque los bancos se guardan en un Set (favoritos).
 */
@Entity
@Table(name = "benches")
@SuppressWarnings("unused")
public class Bench {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bench_id")
    private Long benchID;

    @Column
    @NotBlank(message = "Las coordenadas no pueden estar vacías")
    private String cords;

    @Column
    @NotBlank
    private String type;

    // Contador global de "me gusta" (no se guarda quién votó).
    @Column
    private Integer likes = 0;

    // Foto del banco. La FK vive en esta tabla ('benches.photo_id').
    // cascade + orphanRemoval hacen que al borrar el banco se borre su foto.
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    public static Bench from(gal.usc.etse.es.restdemo.model.dto.Bench benchDTO) {
        return from(benchDTO, null);
    }

    public static Bench from(gal.usc.etse.es.restdemo.model.dto.Bench benchDTO, Photo photo) {
        if (benchDTO == null) return null;
        return new Bench(
                benchDTO.benchID(),
                benchDTO.cords(),
                benchDTO.type(),
                benchDTO.likes()
        ).setPhoto(photo);
    }

    public Bench() {}

    public Bench(Long benchID, String cords, String type) {
        this(benchID, cords, type, 0);
    }

    public Bench(Long benchID, String cords, String type, Integer likes) {
        this.benchID = benchID;
        this.cords = cords;
        this.type = type;
        this.likes = likes == null ? 0 : likes;
    }

    public Long getBenchID() { return benchID; }
    public Bench setBenchID(Long benchID) { this.benchID = benchID; return this; }

    public String getCords() { return cords; }
    public Bench setCords(String cords) { this.cords = cords; return this; }

    public String getType() { return type; }
    public Bench setType(String type) { this.type = type; return this; }

    public Integer getLikes() { return likes; }
    public Bench setLikes(Integer likes) { this.likes = likes == null ? 0 : likes; return this; }

    public Photo getPhoto() { return photo; }
    public Bench setPhoto(Photo photo) { this.photo = photo; return this; }

    // Identidad por ID: necesaria para que funcione Set<Bench> en User.favoriteBenches.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bench bench = (Bench) o;
        return Objects.equals(benchID, bench.benchID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(benchID);
    }
}