package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.Datos;
import modelo.Rol;
import modelo.Usuario;
import servicio.ISUsuario;
import java.io.IOException;

// Controlador para la pantalla de registro de usuarios.
public class RegistroController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private VBox emprendedorBox;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton emprendedorRadio;
    @FXML private TextArea mensajeField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;

    // Servicio para manejar la lógica de usuario.
    private final ISUsuario servicioU;

    public RegistroController(ISUsuario servicioU) {
        this.servicioU = servicioU;
    }

    // Inicializa la interfaz, configurando la visibilidad del campo de mensaje.
    @FXML
    public void initialize() {
        // Muestra / oculta el campo del mensaje según el rol elegido.
        clienteRadio.setOnAction(e -> {
            emprendedorBox.setVisible(false);
            emprendedorBox.setManaged(false);
        });

        emprendedorRadio.setOnAction(e -> {
            emprendedorBox.setVisible(true);
            emprendedorBox.setManaged(true);
        });
    }

    // Maneja el evento de clic en el botón de registrar.
    @FXML
    public void onRegistrarClick() {
        try {
            // Validar campos obligatorios.
            if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() ||
                    telefonoField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    passwordField.getText().isEmpty() ||
                    (!clienteRadio.isSelected() && !emprendedorRadio.isSelected())) {

                mostrarAlerta("Campos incompletos", "Por favor completa todos los campos antes de registrarte.");
                return;
            }

            // Crear objeto Datos.
            Datos datos = new Datos();
            datos.setNombre(nombreField.getText());
            datos.setApellido(apellidoField.getText());
            datos.setTelefono(telefonoField.getText());

            // Crear objeto Rol según selección.
            Rol rol = new Rol();
            if (clienteRadio.isSelected()) {
                rol.setNombre("Cliente");
            } else if (emprendedorRadio.isSelected()) {
                rol.setNombre("Emprendedor");
            }

            // Crear objeto Usuario
            Usuario usuario = new Usuario();
            usuario.setCorreo(emailField.getText());
            usuario.setContrasena(passwordField.getText());
            usuario.setDatosPersonales(datos);
            usuario.setRol(rol);

            // Registrar el usuario con el servicio.
            boolean registrado = servicioU.registrarUsuario(usuario, mensajeField.getText());

            if (registrado) {
                String msg;
                if (emprendedorRadio.isSelected()) {
                    msg = "Tu registro como emprendedor ha sido enviado. " + "Tu cuenta quedará pendiente hasta que un reclutador apruebe tu solicitud.";
                } else {
                    msg = "Usuario registrado correctamente. Ya puedes iniciar sesión.";
                }

                mostrarAlerta("Registro exitoso", msg);
                limpiarCampos();
                onVolverClick(new ActionEvent());
            } else {
                mostrarAlerta("Error al registrar", "No se pudo registrar el usuario. Verifica los datos.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error inesperado", e.getMessage());
        }
    }

    // Maneja el evento de clic en el botón de volver al login.
    @FXML
    public void onVolverClick(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) nombreField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            loader.setControllerFactory(param -> new Controlador(servicioU).createController(param));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver al inicio de sesión.");
        }
    }

    // Limpia todos los campos del formulario.
    private void limpiarCampos() {
        nombreField.clear();
        apellidoField.clear();
        telefonoField.clear();
        emailField.clear();
        passwordField.clear();
        mensajeField.clear();
        clienteRadio.setSelected(false);
        emprendedorRadio.setSelected(false);
    }

    // Muestra una alerta con el título y mensaje proporcionados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}