package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import servicio.SUsuario;

public class LoginController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    // Servicio para manejar la lógica de usuario.
    private final SUsuario servicioU;

    // Constructor con inyección de dependencia del servicio de usuario.
    public LoginController(SUsuario servicioU) {
        this.servicioU = servicioU;
    }

    // Maneja el evento de clic en el botón de login.
    @FXML public void onLoginClick(ActionEvent actionEvent) {
        String correo = emailField.getText();
        String contrasena = passwordField.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos.");
            return;
        }

        // Pedimos al servicio que autentique al usuario.
        boolean autenticado = servicioU.autenticar(correo, contrasena);

        if (autenticado) {
            String nombre = servicioU.obtenerNombre(correo);
            String apellido = servicioU.obtenerApellido(correo);
            String rol = servicioU.obtenerRol(correo);

            mostrarAlerta("Bienvenido", "Hola " + nombre + " " + apellido + " (" + rol + ")");

            if (rol == null) {
                mostrarAlerta("Error", "Rol no reconocido. Contacta al administrador.");
                return;
            }

            // Redirigir según el rol del usuario (NO IMPLEMENTADO).
            switch (rol) {
                case "Cliente":
                    abrirPantalla("/puj.fis.pantallas/cliente.fxml");
                    break;

                case "Emprendedor":
                    abrirPantalla("/puj.fis.pantallas/emprendedor.fxml");
                    break;

                case "Reclutador":
                    abrirPantalla("/puj.fis.pantallas/reclutador.fxml");
                    break;

                default:
                    mostrarAlerta("Error", "Rol no reconocido.");
            }
        } else {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
        }
    }

    // Maneja el evento de clic en el botón de registro. (NO IMPLEMENTADO).
    @FXML public void onRegisterClick(ActionEvent actionEvent) {
        mostrarAlerta("Registro", "Aquí debería abrir el formulario de registro.");
    }

    // Muestra una alerta con el título y mensaje proporcionados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Abre una nueva pantalla según el rol del usuario.
    private void abrirPantalla(String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla: " + e.getMessage());
        }
    }
}
