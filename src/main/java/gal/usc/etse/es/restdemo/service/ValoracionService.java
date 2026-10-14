package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.ValoracionNotFoundException;
import gal.usc.etse.es.restdemo.exception.BenchNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Valoracion;
import gal.usc.etse.es.restdemo.model.entity.Bench;
import gal.usc.etse.es.restdemo.model.entity.Photo;
import gal.usc.etse.es.restdemo.model.entity.User;
import gal.usc.etse.es.restdemo.repository.BenchRepository;
import gal.usc.etse.es.restdemo.repository.PhotoRepository;
import gal.usc.etse.es.restdemo.repository.UserRepository;
import gal.usc.etse.es.restdemo.repository.ValoracionRepository;
import gal.usc.etse.es.utils.patch.JsonPatch;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;

@Service
@NullMarked
public class ValoracionService {
    private final ValoracionRepository reviews;
    private final UserRepository users;
    private final BenchRepository benches;
    private final PhotoRepository photos;
    private final JsonMapper mapper;

    @Autowired
    public ValoracionService(ValoracionRepository reviews, UserRepository users, BenchRepository benches, PhotoRepository photos, JsonMapper mapper) {
        this.reviews = reviews;
        this.users = users;
        this.benches = benches;
        this.photos = photos;
        this.mapper = mapper;
    }

    public Valoracion addValoracion(Valoracion dto) {
        User reviewer = users.findByUserID(dto.reviewer().userID()).orElseThrow();
        Bench bench = benches.findById(dto.benchOfReview().benchID()).orElseThrow();
        Photo photo = null;
        if (dto.photo() != null && dto.photo().photoID() != null) {
            photo = photos.findById(dto.photo().photoID()).orElse(null);
        } else if (dto.photo() != null) {
            photo = photos.save(gal.usc.etse.es.restdemo.model.entity.Photo.from(dto.photo()));
        }
        gal.usc.etse.es.restdemo.model.entity.Valoracion entity = new gal.usc.etse.es.restdemo.model.entity.Valoracion(
                null,
                reviewer,
                bench,
                photo,
                dto.date() == null ? LocalDate.now() : dto.date(),
                dto.mark(),
                dto.note()
        );
        return Valoracion.from(reviews.save(entity));
    }

    public Page<Valoracion> getValoraciones(Long benchId, String userId, PageRequest page) {
        if (benchId != null) return reviews.findByBenchOfReview_BenchID(benchId, page).map(Valoracion::from);
        if (userId != null && !userId.isBlank()) return reviews.findByReviewer_UserID(userId, page).map(Valoracion::from);
        return reviews.findAll(page).map(Valoracion::from);
    }

    public Valoracion getValoracion(Long id) throws ValoracionNotFoundException {
        return reviews.findById(id).map(Valoracion::from).orElseThrow(() -> new ValoracionNotFoundException(id));
    }

    public Valoracion updateValoracion(Long id, List<JsonPatchOperation> changes) throws ValoracionNotFoundException {
        Valoracion review = reviews.findById(id).map(Valoracion::from).orElseThrow(() -> new ValoracionNotFoundException(id));
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(review, JsonNode.class));
        Valoracion updated = mapper.convertValue(patched, Valoracion.class);
        gal.usc.etse.es.restdemo.model.entity.Valoracion entity = reviews.findById(id).orElseThrow(() -> new ValoracionNotFoundException(id));
        if (updated.mark() != null) entity.setMark(updated.mark());
        if (updated.note() != null) entity.setNote(updated.note());
        if (updated.date() != null) entity.setDate(updated.date());
        return Valoracion.from(reviews.save(entity));
    }

    public void deleteValoracion(Long id) throws ValoracionNotFoundException {
        if (!reviews.existsById(id)) throw new ValoracionNotFoundException(id);
        reviews.deleteById(id);
    }
}
