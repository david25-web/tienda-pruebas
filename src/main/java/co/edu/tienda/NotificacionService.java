package co.edu.tienda;

/** Dependencia externa (en producción enviaría correo/SMS). */
public interface NotificacionService {
    void enviar(String destinatario, String mensaje);
}
