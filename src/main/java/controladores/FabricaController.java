package controladores;

import javafx.stage.*;
import repositorio.*;
import servicio.*;

import java.sql.Connection;
import java.sql.SQLException;

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
    private final ISProgresoMaterial servicioProgreso;
    private final ISSolicitud servicioSolicitud;
    private final ISUsuario servicioUsuario;

    // Constructor con inyección de dependencias.
    public FabricaController(Stage stage) throws SQLException {
        // Obtener conexión a la base de datos.
        Connection conexion = ConexionDB.getConnection();
        // Inyección de dependencias.
        this.servicioCalificacion = new SCalificacion(new RCalificacion(conexion));
        this.servicioCategoria = new SCategoria(new RCategoria(conexion));
        this.servicioCompra = new SCompra(new RCompra(conexion));
        this.servicioMaterial = new SMaterial(new RMaterial(conexion));
        this.servicioPago = new SPago(new RPago(conexion));
        this.servicioProducto = new SProducto(new RProducto(conexion));
        this.servicioProgreso = new SProgresoMaterial(new RProgresoMaterial(conexion));
        this.servicioSolicitud = new SSolicitud(new RSolicitud(conexion));
        this.servicioUsuario = new SUsuario(new RUsuario(conexion));

        // Gestor de pantallas con acceso a fábrica.
        this.gestorPantallas = new GestorPantallas(stage, this);
    }

    // Getter para el gestor de pantallas.
    public GestorPantallas getGestorPantallas() {
        return gestorPantallas;
    }

    // Permite crear controladores con los servicios inyectados.
    public Object createController(Class<?> tipo) {
        if (tipo == AccederCursoController.class) { return new AccederCursoController(servicioMaterial, servicioProgreso, gestorPantallas); }
        if (tipo == AdminProductoController.class) { return new AdminProductoController(servicioProducto, servicioMaterial, gestorPantallas); }
        if (tipo == CalificacionController.class) { return new CalificacionController(servicioProducto, servicioCalificacion, gestorPantallas); }
        if (tipo == CalificarController.class) { return new CalificarController(servicioProducto, servicioCalificacion, gestorPantallas); }
        if (tipo == CarritoController.class) { return new CarritoController(gestorPantallas); }
        if (tipo == CatalogoController.class) { return new CatalogoController(servicioProducto, servicioCategoria, gestorPantallas); }
        if (tipo == CategoriaController.class) { return new CategoriaController(servicioCategoria, gestorPantallas); }
        if (tipo == ClienteController.class) { return new ClienteController(servicioUsuario, gestorPantallas); }
        if (tipo == EmprendedorController.class) { return new EmprendedorController(servicioUsuario, gestorPantallas); }
        if (tipo == LoginController.class) { return new LoginController(servicioUsuario, gestorPantallas); }
        if (tipo == PagoController.class) { return new PagoController(servicioPago, servicioCompra, gestorPantallas); }
        if (tipo == ProductoCController.class) { return new ProductoCController(servicioProducto, gestorPantallas); }
        if (tipo == ProductoEController.class) { return new ProductoEController(servicioProducto, gestorPantallas); }
        if (tipo == ReclutadorController.class) { return new ReclutadorController(servicioUsuario, gestorPantallas); }
        if (tipo == RegistroController.class) { return new RegistroController(servicioUsuario, gestorPantallas); }
        if (tipo == SolicitudController.class) { return new SolicitudController(servicioSolicitud, gestorPantallas); }
        if (tipo == SolicitudProductoController.class) { return new SolicitudProductoController(servicioProducto, servicioCategoria, servicioSolicitud, gestorPantallas); }

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
