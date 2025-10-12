package controladores;

import servicio.ISUsuario;

public class Controlador {

    private final ISUsuario servicioUsuario;

    public Controlador(ISUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    // Este metodo decide qué controlador crear según la clase solicitada
    public Object createController(Class<?> tipo) {
        if (tipo == LoginController.class) {
            return new LoginController(servicioUsuario);
        } else if (tipo == RegistroController.class) {
            return new RegistroController(servicioUsuario);
        }
        throw new IllegalArgumentException("Controlador no soportado: " + tipo.getName());
    }
}
