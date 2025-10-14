package controladores;

import servicio.ISCategoria;
import servicio.ISCompra;
import servicio.ISProducto;
import servicio.ISUsuario;

public class Controlador {

    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;
    private final ISProducto servicioProducto;
    private final ISCategoria servicioCategoria;

    public Controlador(ISUsuario servicioUsuario, ISCompra servicioCompra, ISProducto servicioProducto, ISCategoria servicioCategoria) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = servicioCompra;
        this.servicioProducto = servicioProducto;
        this.servicioCategoria = servicioCategoria;
    }

    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) return new LoginController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria);
        if (tipo == RegistroController.class) return new RegistroController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria);
        if (tipo == ClienteController.class) return new ClienteController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria);
        if (tipo == CarritoController.class) return new CarritoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria);
        if (tipo == CatalogoController.class) return new CatalogoController(servicioUsuario, servicioCompra, servicioProducto, servicioCategoria);

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
