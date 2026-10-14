package gal.usc.etse.es.restdemo.service;

import gal.usc.etse.es.restdemo.exception.BenchNotFoundException;
import gal.usc.etse.es.restdemo.model.dto.Bench;
import gal.usc.etse.es.restdemo.repository.BenchRepository;
import gal.usc.etse.es.utils.patch.JsonPatch;
import gal.usc.etse.es.utils.patch.JsonPatchOperation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
@NullMarked
public class BenchService {
    private final BenchRepository benches;
    private final JsonMapper mapper;

    @Autowired
    public BenchService(BenchRepository benches, JsonMapper mapper) {
        this.benches = benches;
        this.mapper = mapper;
    }

    public Bench addBench(Bench bench) {
        gal.usc.etse.es.restdemo.model.entity.Bench saved = benches.save(gal.usc.etse.es.restdemo.model.entity.Bench.from(bench));
        return Bench.from(saved);
    }

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

    public Bench updateBench(Long id, List<JsonPatchOperation> changes) throws BenchNotFoundException {
        Bench bench = benches.findById(id).map(Bench::from).orElseThrow(() -> new BenchNotFoundException(id));
        JsonNode patched = JsonPatch.apply(changes, mapper.convertValue(bench, JsonNode.class));
        Bench updated = mapper.convertValue(patched, Bench.class);
        gal.usc.etse.es.restdemo.model.entity.Bench entity = gal.usc.etse.es.restdemo.model.entity.Bench.from(updated);
        entity.setBenchID(id);
        return Bench.from(benches.save(entity));
    }

    public Bench likeBench(Long id) throws BenchNotFoundException {
        gal.usc.etse.es.restdemo.model.entity.Bench entity =
                benches.findById(id).orElseThrow(() -> new BenchNotFoundException(id));
        entity.setLikes((entity.getLikes() == null ? 0 : entity.getLikes()) + 1);
        return Bench.from(benches.save(entity));
    }

    public void deleteBench(Long id) throws BenchNotFoundException {
        if (!benches.existsById(id)) throw new BenchNotFoundException(id);
        benches.deleteById(id);
    }
}
