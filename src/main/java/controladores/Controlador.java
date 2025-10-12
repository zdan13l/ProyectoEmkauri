package controladores;

import servicio.SUsuario;

// Clase que actúa como un controlador genérico para manejar inyección de dependencias en otros controladores.
public class Controlador {
    private final SUsuario servicioU;

    // Constructor que recibe el servicio de usuario.
    public Controlador(SUsuario servicioU) {
        this.servicioU = servicioU;
    }

    // Metodo para crear instancias de controladores y pasarles los servicios necesarios.
    public Object createController(Class<?> controllerClass) {
        try {
            Object controller = controllerClass.getDeclaredConstructor().newInstance();
            if (controller instanceof IControlador ic) {
                ic.setServicios(servicioU);
            }
            return controller;
        } catch (Exception e) {
            throw new RuntimeException("Error creando controlador: " + controllerClass.getSimpleName(), e);
        }
    }
}
