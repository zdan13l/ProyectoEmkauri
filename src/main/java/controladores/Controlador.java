package controladores;

import servicio.ISUsuario;
import servicio.ISPago;

/**
 * Factory de controladores FXML.
 * Mantiene DI: se recomienda crear con ambos servicios inyectados.
 * Se deja un constructor legacy (solo ISUsuario) para compatibilidad;
 * se lanzará IllegalStateException si se intenta crear PagosController sin ISPago.
 */
public class Controlador {

    private final ISUsuario servicioUsuario;
    private final ISPago servicioPago; // puede ser null si la app aún no lo inyecta

    // Constructor recomendado (DI completa)
    public Controlador(ISUsuario servicioUsuario, ISPago servicioPago) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPago = servicioPago;
    }

    // Constructor legacy para compatibilidad con EmkauriApp actual
    public Controlador(ISUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPago = null;
    }

    // Decide qué controlador crear
    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) {
            return new LoginController(servicioUsuario);
        } else if (tipo == RegistroController.class) {
            return new RegistroController(servicioUsuario);
        } else if (tipo == PagosController.class) {
            if (servicioPago == null) {
                throw new IllegalStateException("ISPAGO no fue inyectado. Actualiza EmkauriApp para DI completa.");
            }
            return new PagosController(servicioPago);
        }
        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
