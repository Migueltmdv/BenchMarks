package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "valoraciones")
@SuppressWarnings("unused")
public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    @NotNull
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bench_id")
    @NotNull
    private Bench benchOfReview;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Column
    @NotNull
    private LocalDate date;

    @Column
    @NotNull
    @Min(0)
    @Max(10) // Suponiendo una nota del 0 al 10
    private Integer mark;

    @Column(length = 2000)
    private String note;

    public static Valoracion from(gal.usc.etse.es.restdemo.model.dto.Valoracion dto, User reviewerEntity, Bench benchEntity) {
        if (dto == null) return null;
        // Nota: Pasamos el User y Bench como parámetros extra en el from() 
        // porque la entidad necesita conectarse con objetos persistidos en la base de datos.
        return new Valoracion(
                dto.reviewId(),
                reviewerEntity,
                benchEntity,
                Photo.from(dto.photo()),
                dto.date(),
                dto.mark(),
                dto.note()
        );
    }

    public Valoracion() {}

    public Valoracion(Long reviewId, User reviewer, Bench benchOfReview, Photo photo, LocalDate date, Integer mark, String note) {
        this.reviewId = reviewId;
        this.reviewer = reviewer;
        this.benchOfReview = benchOfReview;
        this.photo = photo;
        this.date = date;
        this.mark = mark;
        this.note = note;
    }

    public Long getReviewId() { return reviewId; }
    public Valoracion setReviewId(Long reviewId) { this.reviewId = reviewId; return this; }

    public User getReviewer() { return reviewer; }
    public Valoracion setReviewer(User reviewer) { this.reviewer = reviewer; return this; }

    public Bench getBenchOfReview() { return benchOfReview; }
    public Valoracion setBenchOfReview(Bench benchOfReview) { this.benchOfReview = benchOfReview; return this; }

    public Photo getPhoto() { return photo; }
    public Valoracion setPhoto(Photo photo) { this.photo = photo; return this; }

    public LocalDate getDate() { return date; }
    public Valoracion setDate(LocalDate date) { this.date = date; return this; }

    public Integer getMark() { return mark; }
    public Valoracion setMark(Integer mark) { this.mark = mark; return this; }

    public String getNote() { return note; }
    public Valoracion setNote(String note) { this.note = note; return this; }
}