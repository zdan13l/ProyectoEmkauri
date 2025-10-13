package controladores;

import servicio.ISCompra;
import servicio.ISUsuario;

public class Controlador {

    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;

    public Controlador(ISUsuario servicioUsuario, ISCompra servicioCompra) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = servicioCompra;
    }

    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) return new LoginController(servicioUsuario, servicioCompra);
        if (tipo == RegistroController.class) return new RegistroController(servicioUsuario, servicioCompra);
        if (tipo == ClienteController.class) return new ClienteController(servicioUsuario, servicioCompra);
        if (tipo == CarritoController.class) return new CarritoController(servicioUsuario, servicioCompra);

        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
