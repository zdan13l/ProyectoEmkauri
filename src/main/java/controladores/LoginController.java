package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modelo.Usuario;
import servicio.*;

// Controlador para gestionar el inicio de sesión de usuarios.
public class LoginController {

    // Elementos de la interfaz gráfica.
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;

    // Gestor de pantallas para la navegación.
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public LoginController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
                            ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal, GestorPantallas gestorPantallas) {
        this.servicioCo = servicioCo;
        this.servicioU = servicioU;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
        this.gestorPantallas = gestorPantallas;
    }

    // Manejo del evento de clic en el botón de inicio de sesión.
    @FXML
    public void onLoginClick(ActionEvent actionEvent) {
        try {
            String correo = txtUsuario.getText();
            String contrasena = txtPassword.getText();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                gestorPantallas.mostrarAlerta("Error", "Por favor completa todos los campos.");
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

                gestorPantallas.mostrarAlerta("Bienvenido", "Hola " + nombre + " " + apellido + " (" + rol + ")");

                if (rol == null) {
                    gestorPantallas.mostrarAlerta("Error", "Rol no reconocido.");
                    return;
                }

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
                        gestorPantallas.mostrarAlerta("Error", "Rol no reconocido.");
                }
            } else {
                gestorPantallas.mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
            }
        } catch (RuntimeException e) {
            gestorPantallas.mostrarAlerta("Error", "Error inesperado: " + e.getMessage());
        }
    }

    // Manejo del evento de clic en el botón de registro.
    @FXML
    public void onRegisterClick(ActionEvent actionEvent) {
        gestorPantallas.irRegistro();
    }
}
