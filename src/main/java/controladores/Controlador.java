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

    public Controlador (ISUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = null; // Puede ser nulo si no se necesita
    }

    // Este metodo decide qué controlador crear según la clase solicitada
    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) {
            return new LoginController(servicioUsuario);
        } else if (tipo == RegistroController.class) {
            return new RegistroController(servicioUsuario);
        } else if (tipo == CarritoController.class) {
            throw new IllegalStateException("CarritoController se crea dinámicamente desde LoginController");
        }
        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
