package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.Datos;
import modelo.Usuario;
import servicio.ISUsuario;

import java.io.IOException;

public class RegistroController {

    @FXML private VBox emprendedorBox;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton emprendedorRadio;
    @FXML private TextArea mensajeField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;

    private final ISUsuario servicioU;

    public RegistroController(ISUsuario servicioU) {
        this.servicioU = servicioU;
    }

    @FXML
    public void initialize() {
        // Muestra/oculta el campo del mensaje según el rol elegido
        clienteRadio.setOnAction(e -> {
            emprendedorBox.setVisible(false);
            emprendedorBox.setManaged(false);
        });

        emprendedorRadio.setOnAction(e -> {
            emprendedorBox.setVisible(true);
            emprendedorBox.setManaged(true);
        });
    }

    @FXML
    public void onRegistrarClick() {
        try {
            // 1.Validar campos obligatorios
            if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() ||
                    telefonoField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    passwordField.getText().isEmpty() ||
                    (!clienteRadio.isSelected() && !emprendedorRadio.isSelected())) {

                mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos",
                        "Por favor completa todos los campos antes de registrarte.");
                return;
            }

            // 2.Crear objeto Datos
            Datos datos = new Datos();
            datos.setNombre(nombreField.getText());
            datos.setApellido(apellidoField.getText());
            datos.setTelefono(telefonoField.getText());

            // 3.Crear objeto Rol según selección
            String tipoUsuario;
            String mensaje = null;
            if (clienteRadio.isSelected()) {
                tipoUsuario = "Cliente";
            } else {
                tipoUsuario = "Emprendedor";
                mensaje = mensajeField.getText().isEmpty() ? "Solicitud sin mensaje adicional." : mensajeField.getText();
            }

            // 4.Crear objeto Usuario
            Usuario usuario = new Usuario();
            usuario.setCorreo(emailField.getText());
            usuario.setContrasena(passwordField.getText());
            usuario.setDatosPersonales(datos);

            // 5.Registrar el usuario con el servicio
            boolean registrado = servicioU.registrarUsuario(usuario, tipoUsuario, mensaje);

            if (registrado) {
                String msg;
                if (emprendedorRadio.isSelected()) {
                    msg = "Tu registro como emprendedor ha sido enviado. " +
                            "Tu cuenta quedará pendiente hasta que un reclutador apruebe tu solicitud.";
                } else {
                    msg = "Usuario registrado correctamente. Ya puedes iniciar sesión.";
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", msg);
                limpiarCampos();
                onVolverClick(new ActionEvent());
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar",
                        "No se pudo registrar el usuario. Verifica los datos.");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onVolverClick(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) nombreField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            loader.setControllerFactory(param -> new Controlador(servicioU).createController(param));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo volver al inicio de sesión.");
        }
    }

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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}