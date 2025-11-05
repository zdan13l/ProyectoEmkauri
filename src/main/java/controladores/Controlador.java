package controladores;

import servicio.*;

// Controlador con inyección de dependencias para los controladores de la aplicación.
public class Controlador {

    // Servicios necesarios para los controladores.
    private final ISCalificacion servicioCalificacion;
    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;
    private final ISProducto servicioProducto;
    private final ISCategoria servicioCategoria;
    private final ISPago servicioPago;
    private final ISSolicitud servicioSolicitud;

    // Constructor con inyección de dependencias.
    public Controlador(ISUsuario servicioUsuario, ISCompra servicioCompra, ISProducto servicioProducto, ISCategoria servicioCategoria, ISPago servicioPago, ISSolicitud servicioSolicitud, ISCalificacion servicioCalificacion) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = servicioCompra;
        this.servicioProducto = servicioProducto;
        this.servicioCategoria = servicioCategoria;
        this.servicioPago = servicioPago;
        this.servicioSolicitud = servicioSolicitud;
        this.servicioCalificacion = servicioCalificacion;
    }

    // Metodo para crear controladores con los servicios inyectados.
    public Object createController(Class<?> tipo) {
        if (tipo == CalificacionController.class) return new CalificacionController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == CalificarController.class) return new CalificarController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == LoginController.class) return new LoginController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == RegistroController.class) return new RegistroController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == ClienteController.class) return new ClienteController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == CarritoController.class) return new CarritoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == CatalogoController.class) return new CatalogoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == PagoController.class) return new PagoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == EmprendedorController.class) return new EmprendedorController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == SolicitudProductoController.class) return new SolicitudProductoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == ReclutadorController.class) return new ReclutadorController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == SolicitudController.class) return new SolicitudController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == CategoriaController.class) return new CategoriaController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == ProductoEController.class) return new ProductoEController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == ProductoCController.class) return new ProductoCController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);
        if (tipo == AdminProductoController.class) return new AdminProductoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud, servicioCalificacion);

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
