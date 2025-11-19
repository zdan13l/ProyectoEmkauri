package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modelo.Usuario;
import servicio.ISUsuario;

// Controlador para gestionar el inicio de sesión de usuarios.
public class LoginController {

    // Elementos de la interfaz gráfica.
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    // Servicios para la lógica y gestor de navegación.
    private final ISUsuario servicioU;
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public LoginController(ISUsuario servicioU, GestorPantallas gestorPantallas) {
        this.servicioU = servicioU;
        this.gestorPantallas = gestorPantallas;
    }

    // Manejo del evento de clic en el botón de inicio de sesión.
    @FXML
    public void onLoginClick(ActionEvent actionEvent) {
        try {
            String correo = txtUsuario.getText();
            String contrasena = txtPassword.getText();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                gestorPantallas.mostrarAlerta("Campos Incompletos", "Por favor completa todos los campos.");
                return;
            }

            // Pedimos al servicio que autentique al usuario.
            boolean autenticado = servicioU.autenticar(correo, contrasena);

            if (autenticado) {
                Usuario usuario = servicioU.obternerUsuario(correo);
                SesionActual.setUsuarioActual(usuario);
                String nombre = servicioU.obtenerNombre(correo);
                String apellido = servicioU.obtenerApellido(correo);
                String rol = servicioU.obtenerRol(correo);

                gestorPantallas.mostrarExito("Bienvenido", "Hola " + nombre + " " + apellido + " (" + rol + ")");

                if (rol == null) {
                    gestorPantallas.mostrarError("Error", "Rol no reconocido.");
                    return;
                }

                // Navegar a la pantalla correspondiente según el rol del usuario.
                switch (rol.toLowerCase()) {
                    case "cliente":
                        gestorPantallas.irCliente();
                        break;
                    case "emprendedor":
                        gestorPantallas.irEmprendedor();
                        break;
                    case "reclutador":
                        gestorPantallas.irReclutador();
                        break;
                    default:
                        gestorPantallas.mostrarError("Error", "Rol no reconocido.");
                }
            } else {
                gestorPantallas.mostrarError("Error", "Usuario o contraseña incorrectos.");
            }
        } catch (RuntimeException e) {
            gestorPantallas.mostrarError("Error", "Error inesperado: " + e.getMessage());
        }
    }

    // Manejo del evento de clic en el botón de registro.
    @FXML
    public void onRegisterClick(ActionEvent actionEvent) { gestorPantallas.irRegistro(); }
}
