package controladores;

import servicio.ISUsuario;

public class Controlador {

    private final ISUsuario servicioUsuario;

    public Controlador(ISUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
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
