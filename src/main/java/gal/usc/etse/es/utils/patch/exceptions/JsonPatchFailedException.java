package gal.usc.etse.es.utils.patch.exceptions;

/**
 * Error al aplicar una operación JSON Patch (ruta inválida, tipo incorrecto...).
 */
public class JsonPatchFailedException extends RuntimeException {
    public JsonPatchFailedException(String message) {
        super(message);
    }
}
