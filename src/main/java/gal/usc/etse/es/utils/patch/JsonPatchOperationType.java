package gal.usc.etse.es.utils.patch;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Tipos de operación admitidos por JSON Patch.
 */
public enum JsonPatchOperationType {

    // Operaciones definidas por RFC 6902.
    ADD, REMOVE, REPLACE, COPY, MOVE;

    // Convierte el texto JSON ("add", "replace"...) al valor del enum.
    @JsonCreator
    public static JsonPatchOperationType forValue(String value) {
        return JsonPatchOperationType.valueOf(value.toUpperCase());
    }
}
