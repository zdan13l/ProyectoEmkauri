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
        if (tipo == LoginController.class) return new LoginController(servicioUsuario);
        if (tipo == RegistroController.class) return new RegistroController(servicioUsuario);
        if (tipo == ClienteController.class) return new ClienteController(servicioUsuario);

        // Constructor sin argumentos.
        try {
            return tipo.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName(), e);
        }
    }
}
