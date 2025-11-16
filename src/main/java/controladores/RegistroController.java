package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import modelo.Datos;
import modelo.Rol;
import modelo.Usuario;
import servicio.ISUsuario;

// Controlador para gestionar el registro de usuarios.
public class RegistroController {

    // Elementos de la interfaz gráfica.
    @FXML private VBox emprendedorBox;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton clienteRadio;
    @FXML private RadioButton emprendedorRadio;
    @FXML private TextArea mensajeField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;

    // Servicios para la lógica y gestor de navegación.
    private final ISUsuario servicioU;
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public RegistroController(ISUsuario servicioU, GestorPantallas gestorPantallas) {
        this.servicioU = servicioU;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicializa la interfaz, configurando la visibilidad del campo de mensaje.
    @FXML
    public void initialize() {
        // Muestra - oculta el campo del mensaje según el rol elegido.
        clienteRadio.setOnAction(evento -> {
            emprendedorBox.setVisible(false);
            emprendedorBox.setManaged(false);
        });

        emprendedorRadio.setOnAction(evento -> {
            emprendedorBox.setVisible(true);
            emprendedorBox.setManaged(true);
        });
    }

    // Maneja el evento de clic en el botón de registrar.
    @FXML
    public void onRegistrarClick() {
        try {
            // Validar campos obligatorios.
            if (nombreField.getText().isEmpty() || apellidoField.getText().isEmpty() || telefonoField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    passwordField.getText().isEmpty() || (!clienteRadio.isSelected() && !emprendedorRadio.isSelected())) {
                gestorPantallas.mostrarAlerta("Campos incompletos", "Por favor completa todos los campos antes de registrarte.");
                return;
            }

            // Validar formato de nombre y apellido.
            String regexTexto = "^[a-zA-ZÀ-ÿ\\\\s]+$";
            if (!nombreField.getText().matches(regexTexto) || !apellidoField.getText().matches(regexTexto)) {
                gestorPantallas.mostrarAlerta("Formato inválido", "El nombre y apellido solo deben contener letras y espacios.");
                return;
            }

            // Validar formato de teléfono.
            String regexTelefono = "^?[0-9]{7,15}$";
            if (!telefonoField.getText().matches(regexTelefono)) {
                gestorPantallas.mostrarAlerta("Formato inválido", "El número de teléfono debe contener solo dígitos y tener entre 7 y 10 caracteres.");
                return;
            }

            // Validar formato de correo electrónico.
            String regexCorreo = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
            if (!emailField.getText().matches(regexCorreo)) {
                gestorPantallas.mostrarAlerta("Formato inválido", "Asegúrese de ingresar un correo electrónico válido.");
                return;
            }

            // Evaluar la fortaleza de la contraseña.
            String fortaleza = evaluarContrasena(passwordField.getText());
            if(fortaleza.equals("Muy débil") || fortaleza.equals("Débil")) {
                boolean seguir = gestorPantallas.mostrarConfirmacion("Contraseña débil", "Tu contraseña es " + fortaleza + ". ¿Deseas continuar con esta contraseña?");
                if (!seguir) {
                    return;
                }
            }

            // Crear objeto Usuario desde los campos del formulario.
            Usuario usuario = getUsuario();

            // Registrar el usuario con el servicio.
            boolean registrado = servicioU.registrarUsuario(usuario, mensajeField.getText());

            if (registrado) {
                String mensaje;
                if (emprendedorRadio.isSelected()) {
                    mensaje = "Tu registro como emprendedor ha sido enviado. Tu cuenta quedará pendiente hasta que un reclutador apruebe tu solicitud.";
                } else {
                    mensaje = "Usuario registrado correctamente. Ya puedes iniciar sesión.";
                }

                gestorPantallas.mostrarExito("Registro exitoso", mensaje);
                limpiarCampos();
                onVolverClick(new ActionEvent());
            } else {
                gestorPantallas.mostrarError("Error al registrar", "No se pudo registrar el usuario. Verifica los datos.");
            }

        } catch (Exception e) {
            gestorPantallas.mostrarError("Error inesperado", e.getMessage());
        }
    }

    // Maneja el evento de clic en el botón de volver al login.
    @FXML
    public void onVolverClick(ActionEvent actionEvent) {
        gestorPantallas.irLogin();
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

    // Crea y devuelve un objeto Usuario.
    private Usuario getUsuario() {
        // Crear objeto Datos personales.
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

        // Crear objeto Usuario.
        Usuario usuario = new Usuario();
        usuario.setCorreo(emailField.getText());
        usuario.setContrasena(passwordField.getText());
        usuario.setDatosPersonales(datos);
        usuario.setRol(rol);

        return usuario;
    }

    // Evaluar la fortaleza de la contraseña ingresada.
    private String evaluarContrasena(String contrasena) {
        int puntuacion = 0;

        if (contrasena.length() >= 7) { puntuacion++; }
        if (contrasena.matches(".*[A-Z].*")) { puntuacion++; }
        if (contrasena.matches(".*[a-z].*")) { puntuacion++; }
        if (contrasena.matches(".*\\d.*")) { puntuacion++; }
        if (contrasena.matches(".*[!@#$%^&*()-+=].*")) { puntuacion++; }

        return switch (puntuacion) {
            case 5 -> "Muy fuerte";
            case 4 -> "Fuerte";
            case 3 -> "Moderada";
            case 2 -> "Débil";
            default -> "Muy débil";
        };
    }
}
