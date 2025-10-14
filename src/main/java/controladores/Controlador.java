package controladores;

import servicio.*;

public class Controlador {

    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;
    private final ISProducto servicioProducto;
    private final ISCategoria servicioCategoria;
    private final ISPago servicioPago;
    private final ISSolicitud servicioSolicitud;

    public Controlador(ISUsuario servicioUsuario, ISCompra servicioCompra, ISProducto servicioProducto, ISCategoria servicioCategoria, ISPago servicioPago, ISSolicitud servicioSolicitud) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = servicioCompra;
        this.servicioProducto = servicioProducto;
        this.servicioCategoria = servicioCategoria;
        this.servicioPago = servicioPago;
        this.servicioSolicitud = servicioSolicitud;
    }

    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) return new LoginController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == RegistroController.class) return new RegistroController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == ClienteController.class) return new ClienteController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == CarritoController.class) return new CarritoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == CatalogoController.class) return new CatalogoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == PagoController.class) return new PagoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == EmprendedorController.class) return new EmprendedorController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == SolicitudProductoController.class) return new SolicitudProductoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == ReclutadorController.class) return new ReclutadorController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);
        if (tipo == SolicitudController.class) return new SolicitudController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago, servicioSolicitud);

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
