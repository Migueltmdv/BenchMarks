package gal.usc.etse.es.restdemo.model.dto;

/**
 * Datos para subir una foto desde el dispositivo del cliente.
 * {@code data} contiene la imagen codificada en Base64 (puede incluir el prefijo data URL).
 */
public record PhotoUpload(
        String resolution,
        Long size,
        String contentType,
        String data
) {
}
