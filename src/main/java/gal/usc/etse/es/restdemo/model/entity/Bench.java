package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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

    @Column
    private Integer likes = 0;

    public static Bench from(gal.usc.etse.es.restdemo.model.dto.Bench benchDTO) {
        if (benchDTO == null) return null;
        return new Bench(
                benchDTO.benchID(),
                benchDTO.cords(),
                benchDTO.type(),
                benchDTO.likes()
        );
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
}