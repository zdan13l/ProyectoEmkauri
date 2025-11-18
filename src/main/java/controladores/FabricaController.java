package controladores;

import javafx.stage.Stage;
import repositorio.*;
import servicio.*;

// Controlador con inyección de dependencias para los controladores de la aplicación.
public class FabricaController {
    // Gestor de pantallas para la navegación.
    private final GestorPantallas gestorPantallas;

    // Servicios necesarios para los controladores.
    private final ISCalificacion servicioCalificacion;
    private final ISCategoria servicioCategoria;
    private final ISCompra servicioCompra;
    private final ISMaterial servicioMaterial;
    private final ISPago servicioPago;
    private final ISProducto servicioProducto;
    private final ISSolicitud servicioSolicitud;
    private final ISUsuario servicioUsuario;

    // Constructor con inyección de dependencias.
    public FabricaController(Stage stage) {
        // Inyección de dependencias.
        this.servicioCalificacion = new SCalificacion(new RCalificacion());
        this.servicioCategoria = new SCategoria(new RCategoria());
        this.servicioCompra = new SCompra(new RCompra());
        this.servicioMaterial = new SMaterial(new RMaterial());
        this.servicioPago = new SPago(new RPago());
        this.servicioProducto = new SProducto(new RProducto());
        this.servicioSolicitud = new SSolicitud(new RSolicitud());
        this.servicioUsuario = new SUsuario(new RUsuario());

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

        if (tipo == LoginController.class) { return new LoginController(servicioUsuario, gestorPantallas); }

        if (tipo == RegistroController.class) { return new RegistroController(servicioUsuario, gestorPantallas); }

        if (tipo == ClienteController.class) { return new ClienteController(servicioUsuario, gestorPantallas); }

        if (tipo == CarritoController.class) { return new CarritoController(gestorPantallas); }

        if (tipo == CatalogoController.class) { return new CatalogoController(servicioProducto, servicioCategoria, gestorPantallas); }

        if (tipo == PagoController.class) { return new PagoController(servicioPago, servicioCompra, gestorPantallas); }

        if (tipo == EmprendedorController.class) { return new EmprendedorController(servicioUsuario, gestorPantallas); }

        if (tipo == SolicitudProductoController.class) { return new SolicitudProductoController(servicioProducto, servicioCategoria, servicioSolicitud, gestorPantallas); }

        if (tipo == ReclutadorController.class) { return new ReclutadorController(servicioUsuario, gestorPantallas); }

        if (tipo == SolicitudController.class) { return new SolicitudController(servicioSolicitud, gestorPantallas); }

        if (tipo == CategoriaController.class) { return new CategoriaController(servicioCategoria, gestorPantallas); }

        if (tipo == ProductoEController.class) { return new ProductoEController(servicioProducto, gestorPantallas); }

        if (tipo == ProductoCController.class) { return new ProductoCController(servicioProducto, gestorPantallas); }

        if (tipo == AdminProductoController.class)
            return new AdminProductoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion, gestorPantallas);


        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
