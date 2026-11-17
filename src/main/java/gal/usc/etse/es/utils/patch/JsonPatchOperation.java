package gal.usc.etse.es.utils.patch;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * Una operación JSON Patch: op (add/remove/...), path, from (copy/move)
 * y value (add/replace). @JsonAlias permite leer el campo 'op' del JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JsonPatchOperation(@JsonAlias("op") JsonPatchOperationType operation, JsonPointer path, JsonPointer from, JsonNode value) { }
