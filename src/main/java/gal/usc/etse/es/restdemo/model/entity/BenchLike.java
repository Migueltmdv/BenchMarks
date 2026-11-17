package gal.usc.etse.es.restdemo.model.entity;

import jakarta.persistence.*;

/**
 * Registro de un 'me gusta' de un usuario concreto a un banco.
 * La restricción única (bench_id, user_id) impide votar dos veces.
 * Actualmente no se usa: el like real es el contador Bench.likes.
 */
@Entity
@Table(name = "bench_likes", uniqueConstraints = @UniqueConstraint(name = "uk_bench_likes_bench_user", columnNames = {"bench_id", "user_id"}))
@SuppressWarnings("unused")
public class BenchLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bench_like_id")
    private Long benchLikeID;

    // Banco al que se dio like.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bench_id", nullable = false)
    private Bench bench;

    // Usuario que dio el like. La unique(bench_id, user_id) evita duplicados.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static BenchLike from(Bench bench, User user) {
        if (bench == null || user == null) return null;
        return new BenchLike(bench, user);
    }

    public BenchLike() {}

    public BenchLike(Bench bench, User user) {
        this.bench = bench;
        this.user = user;
    }

    public Long getBenchLikeID() { return benchLikeID; }
    public BenchLike setBenchLikeID(Long benchLikeID) { this.benchLikeID = benchLikeID; return this; }

    public Bench getBench() { return bench; }
    public BenchLike setBench(Bench bench) { this.bench = bench; return this; }

    public User getUser() { return user; }
    public BenchLike setUser(User user) { this.user = user; return this; }
}
