package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import servicio.SUsuario;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegistroController {

    // Campos comunes
    @FXML private TextField nombreField;
    @FXML private TextField usuarioField;   // correo / username
    @FXML private PasswordField passwordField;
    @FXML private TextField documentoField;

    // Tipo de usuario
    @FXML private ToggleGroup tipoUsuarioGroup;        // <- inyectado por FXMLLoader, NO declarar <ToggleGroup/> en FXML
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton emprendedorRadio;

    // Emprendedor
    @FXML private VBox emprendedorBox;
    @FXML private TextField nombrePublicoField;
    @FXML private TextField categoriasField;
    @FXML private ToggleGroup tipoOfertaGroup;         // idem para tipo de oferta (Curso/Servicio)
    @FXML private RadioButton cursoRadio;
    @FXML private RadioButton servicioRadio;
    @FXML private TextArea descripcionField;
    @FXML private TextArea portafolioField;

    // Simulación / lugar para llamar a SUsuario (reemplaza por tu servicio real)
    private final static List<String> usuariosRegistrados = new ArrayList<>();
    // opcional: si ya lo implementaste

    @FXML
    public void initialize() {
        // Asegurarse de ocultar la sección emprendedor al iniciar
        emprendedorBox.setVisible(false);
        emprendedorBox.setManaged(false);

        // Listener para mostrar/ocultar la sección de emprendedor
        tipoUsuarioGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            Toggle selected = tipoUsuarioGroup.getSelectedToggle();
            if (selected == emprendedorRadio) {
                emprendedorBox.setVisible(true);
                emprendedorBox.setManaged(true);
            } else {
                emprendedorBox.setVisible(false);
                emprendedorBox.setManaged(false);
            }
        });
    }

    @FXML
    private void onRegistrarClick() {
        String nombre = nombreField.getText().trim();
        String usuario = usuarioField.getText().trim(); // correo
        String password = passwordField.getText();
        String documento = documentoField.getText().trim();

        // Validaciones básicas
        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || documento.isEmpty()) {
            mostrarAlertaError("Datos incompletos", "Por favor completa todos los campos obligatorios.");
            return;
        }

        // Verificar duplicado (ejemplo local). Reemplazar por consulta real a BD / servicio.
        if (usuariosRegistrados.contains(usuario)) {
            mostrarAlertaError("Usuario existente", "El correo/usuario ya está registrado.");
            return;
        }

        // Si es Cliente
        if (clienteRadio.isSelected()) {
            // Aquí podrías crear Cliente y llamar al servicio que persista en BD
            // ejemplo: servicioUsuario.registrar(nombre, usuario, password, ...)

            usuariosRegistrados.add(usuario); // simulo persistencia

            mostrarAlertaInfo("Cuenta creada", "Tu cuenta de Cliente se creó correctamente.\nAhora puedes iniciar sesión.");
            // Redirigir a login (opcional)
            redirigirALogin(); // descomenta si quieres redirigir directamente
            return;
        }

        // Si es Emprendedor
        if (emprendedorRadio.isSelected()) {
            // Validar campos de emprendedor
            String nombrePublico = nombrePublicoField.getText().trim();
            String categorias = categoriasField.getText().trim();
            Toggle tipoOferta = tipoOfertaGroup.getSelectedToggle();
            String descripcion = descripcionField.getText().trim();
            String portafolio = portafolioField.getText().trim();

            if (nombrePublico.isEmpty() || categorias.isEmpty() || tipoOferta == null || descripcion.isEmpty() || portafolio.isEmpty()) {
                mostrarAlertaError("Datos incompletos", "Completa todos los campos requeridos para Emprendedor.");
                return;
            }

            // Persistir emprendedor (aquí debes llamar a tu servicio real)
            usuariosRegistrados.add(usuario); // simulo persistencia

            // Mensaje para emprendedor: pendiente de verificación
            mostrarAlertaInfo("Registro exitoso",
                    "Tu cuenta de Emprendedor ha sido creada.\n" +
                            "Estado de cuenta: Pendiente de verificación.\n" +
                            "Serás notificado tras la revisión.");
            return;
        }

        // Si no seleccionó tipo
        mostrarAlertaError("Tipo no seleccionado", "Debes seleccionar Cliente o Emprendedor.");
    }

    // Redirige a la pantalla de login (intenta cargar /puj.fis.pantallas/login.fxml)
    private void redirigirALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlertaError("Redirección fallida", "No se pudo abrir la pantalla de login: " + e.getMessage());
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
