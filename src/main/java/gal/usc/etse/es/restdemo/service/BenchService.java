package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.BenchNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Bench;
import gal.usc.etse.es.restdemo.repository.BenchRepository;
import gal.usc.etse.es.restdemo.repository.PhotoRepository;
import gal.usc.etse.es.restdemo.repository.ValoracionRepository;
import gal.usc.etse.es.utils.patch.JsonPatch;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Lógica de negocio de bancos: alta, consulta con filtros, edición con
 * JSON Patch, likes y borrado. El borrado es transaccional y elimina antes
 * las filas relacionadas (valoraciones, favoritos y likes) para no violar FKs.
 */
@Service
@NullMarked
public class BenchService {
    private final BenchRepository benches;
    private final PhotoRepository photos;
    private final ValoracionRepository reviews;
    private final JsonMapper mapper;

    @Autowired
    public BenchService(BenchRepository benches, PhotoRepository photos, ValoracionRepository reviews, JsonMapper mapper) {
        this.benches = benches;
        this.photos = photos;
        this.reviews = reviews;
        this.mapper = mapper;
    }

    // Alta: el cliente solo envía el id de una foto ya subida (o null).
    public Bench addBench(Bench bench) {
        gal.usc.etse.es.restdemo.model.entity.Photo photo = resolvePhoto(bench);
        gal.usc.etse.es.restdemo.model.entity.Bench saved =
                benches.save(gal.usc.etse.es.restdemo.model.entity.Bench.from(bench, photo));
        return Bench.from(saved);
    }

    // Convierte el photoID del DTO en la entidad Photo persistida.
    private gal.usc.etse.es.restdemo.model.entity.Photo resolvePhoto(Bench bench) {
        if (bench.photo() == null || bench.photo().photoID() == null) return null;
        return photos.findById(bench.photo().photoID()).orElse(null);
    }

    // Listado con búsqueda libre (q), filtro por tipo o sin filtros.
    public Page<Bench> getBenches(@Nullable String type, @Nullable String q, PageRequest page) {
        if (q != null && !q.isBlank()) {
            return benches.findByTypeContainingIgnoreCaseOrCordsContainingIgnoreCase(q, q, page).map(Bench::from);
        }
        if (type != null && !type.isBlank()) {
            return benches.findByTypeContainingIgnoreCase(type, page).map(Bench::from);
        }
        return benches.findAll(page).map(Bench::from);
    }

    public Bench getBench(Long id) throws BenchNotFoundException {
        return benches.findById(id).map(Bench::from).orElseThrow(() -> new BenchNotFoundException(id));
    }

    // Edición parcial: entidad -> DTO -> JSON -> aplicar parche -> DTO -> entidad.
    public Bench updateBench(Long id, List<JsonPatchOperation> changes) throws BenchNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.Bench current =
                benches.findById(id).orElseThrow(() -> new BenchNotFoundException(id));
        Bench bench = Bench.from(current);
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(bench, JsonNode.class));
        Bench updated = mapper.convertValue(patched, Bench.class);
        gal.usc.etse.es.restdemo.model.entity.Bench entity = gal.usc.etse.es.restdemo.model.entity.Bench.from(updated);
        entity.setBenchID(id);
        gal.usc.etse.es.restdemo.model.entity.Photo photo = resolvePhoto(updated);
        entity.setPhoto(photo != null ? photo : current.getPhoto());
        return Bench.from(benches.save(entity));
    }

    // Like simple: incrementa el contador (no registra quién votó).
    public Bench likeBench(Long id) throws BenchNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.Bench entity =
                benches.findById(id).orElseThrow(() -> new BenchNotFoundException(id));
        entity.setLikes((entity.getLikes() == null ? 0 : entity.getLikes()) + 1);
        return Bench.from(benches.save(entity));
    }

    // El modelo no tiene lados inversos con cascade, así que el borrado
    // se hace a mano y dentro de una transacción para no dejar FKs huérfanas.
    @Transactional
    public void deleteBench(Long id) throws BenchNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.Bench bench =
                benches.findById(id).orElseThrow(() -> new BenchNotFoundException(id));

        // Borra primero las filas que referencian al banco para no violar las claves foráneas.
        reviews.deleteByBenchOfReview_BenchID(id);
        benches.deleteFavoritesByBenchId(id);
        benches.deleteLikesByBenchId(id);
        benches.delete(bench);
    }
}
