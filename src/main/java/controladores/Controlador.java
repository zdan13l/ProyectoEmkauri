package controladores;

import servicio.*;

public class Controlador {

    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;
    private final ISProducto servicioProducto;
    private final ISCategoria servicioCategoria;
    private final ISPago servicioPago;

    public Controlador(ISUsuario servicioUsuario, ISCompra servicioCompra, ISProducto servicioProducto, ISCategoria servicioCategoria, ISPago servicioPago) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = servicioCompra;
        this.servicioProducto = servicioProducto;
        this.servicioCategoria = servicioCategoria;
        this.servicioPago = servicioPago;
    }

    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) return new LoginController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago);
        if (tipo == RegistroController.class) return new RegistroController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago);
        if (tipo == ClienteController.class) return new ClienteController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago);
        if (tipo == CarritoController.class) return new CarritoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago);
        if (tipo == CatalogoController.class) return new CatalogoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago);
        if (tipo == PagoController.class) return new PagoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria, servicioPago);

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
