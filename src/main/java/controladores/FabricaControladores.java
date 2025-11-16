package controladores;

import javafx.stage.Stage;
import repositorio.*;
import servicio.*;

// Controlador con inyección de dependencias para los controladores de la aplicación.
public class FabricaControladores {
    // Gestor de pantallas para la navegación.
    private final GestorPantallas gestorPantallas;

    // Servicios necesarios para los controladores.
    private final ISCalificacion servicioCalificacion;
    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;
    private final ISProducto servicioProducto;
    private final ISCategoria servicioCategoria;
    private final ISPago servicioPago;
    private final ISSolicitud servicioSolicitud;

    // Constructor con inyección de dependencias.
    public FabricaControladores(Stage stage) {
        // Inyección de dependencias.
        this.servicioUsuario = new SUsuario(new RUsuario());
        this.servicioCompra = new SCompra(new RCompra());
        this.servicioProducto = new SProducto(new RProducto());
        this.servicioCategoria = new SCategoria(new RCategoria());
        this.servicioPago = new SPago(new RPago());
        this.servicioSolicitud = new SSolicitud(new RSolicitud());
        this.servicioCalificacion = new SCalificacion(new RCalificacion());

        // Gestor de pantallas con acceso a fábrica.
        this.gestorPantallas = new GestorPantallas(stage, this);
    }

    // Getter para el gestor de pantallas.
    public GestorPantallas getGestorPantallas() {
        return gestorPantallas;
    }

    // Metodo para crear controladores con los servicios inyectados.
    public Object createController(Class<?> tipo) {
        if (tipo == CalificacionController.class)
            return new CalificacionController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == CalificarController.class)
            return new CalificarController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == LoginController.class)
            return new LoginController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == RegistroController.class)
            return new RegistroController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == ClienteController.class)
            return new ClienteController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == CarritoController.class)
            return new CarritoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == CatalogoController.class)
            return new CatalogoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == PagoController.class)
            return new PagoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == EmprendedorController.class)
            return new EmprendedorController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == SolicitudProductoController.class)
            return new SolicitudProductoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == ReclutadorController.class)
            return new ReclutadorController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == SolicitudController.class)
            return new SolicitudController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == CategoriaController.class)
            return new CategoriaController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == ProductoEController.class)
            return new ProductoEController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == ProductoCController.class)
            return new ProductoCController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        if (tipo == AdminProductoController.class)
            return new AdminProductoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
