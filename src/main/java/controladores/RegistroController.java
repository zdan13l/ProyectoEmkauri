package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import servicio.ISUsuario;
import servicio.SUsuario;

public class RegistroController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private VBox emprendedorBox;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private TextArea mensajeField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private RadioButton emprendedorRadio;

    // Servicio para manejar la lógica de usuario.
    private ISUsuario servicioU;

    public RegistroController(ISUsuario servicioU) {
        this.servicioU = servicioU;
    }
    // Aquí va tu lógica del botón de "Registrarse"
    @FXML
    public void onRegistrarClick() {
        // Lógica de registro usando sUsuario
    }

    @FXML
    public void onVolverClick(ActionEvent actionEvent) {
    }
}
