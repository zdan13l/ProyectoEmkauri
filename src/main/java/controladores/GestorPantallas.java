package controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GestorPantallas {
    // Referencia al Stage principal y a la fábrica de controladores.
    private final Stage stage;
    private final FabricaControladores fabrica;

    // Mapa de rutas de pantallas por clave.
    private final Map<String, String> pantallas = new HashMap<>();

    // Constructor que recibe el Stage principal y la fábrica de controladores.
    public GestorPantallas(Stage stage, FabricaControladores fabrica) {
        this.stage = stage;
        this.fabrica = fabrica;

        // Registrar todas las rutas de pantallas.
        pantallas.put("adminCurso", "/puj.fis.pantallas/administrarCurso.fxml");
        pantallas.put("adminServicio", "/puj.fis.pantallas/administrarServicio.fxml");
        pantallas.put("calificaciones", "/puj.fis.pantallas/calificaciones.fxml");
        pantallas.put("calificar", "/puj.fis.pantallas/calificar.fxml");
        pantallas.put("carrito","/puj.fis.pantallas/carrito.fxml");
        pantallas.put("catalogo", "/puj.fis.pantallas/catalogo.fxml");
        pantallas.put("categoria", "/puj.fis.pantallas/categoria.fxml");
        pantallas.put("cliente", "/puj.fis.pantallas/cliente.fxml");
        pantallas.put("comprobante", "/puj.fis.pantallas/comprobante.fxml");
        pantallas.put("emprendedor", "/puj.fis.pantallas/emprendedor.fxml");
        pantallas.put("login", "/puj.fis.pantallas/login.fxml");
        pantallas.put("pago", "/puj.fis.pantallas/pago.fxml");
        pantallas.put("productosCliente", "/puj.fis.pantallas/productosC.fxml");
        pantallas.put("productosEmprendedor", "/puj.fis.pantallas/productosE.fxml");
        pantallas.put("reclutador", "/puj.fis.pantallas/reclutador.fxml");
        pantallas.put("registro", "/puj.fis.pantallas/registro.fxml");
        pantallas.put("solicitudEmprendedor", "/puj.fis.pantallas/solicitudE.fxml");
        pantallas.put("solicitudProducto", "/puj.fis.pantallas/solicitudP.fxml");
        pantallas.put("solicitudes", "/puj.fis.pantallas/solicitudProducto.fxml");
    }

    // Cambiar la pantalla actual a la indicada por la clave.
    private void cambiarPantalla(String clavePantalla) {
        try {
            String ruta = pantallas.get(clavePantalla);

            if (ruta == null)
                throw new RuntimeException("Pantalla no registrada: " + clavePantalla);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));

            // Inyectar controladores desde la fábrica.
            loader.setControllerFactory(fabrica::createController);

            // Cargar la nueva escena.
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar pantalla: " + clavePantalla, e);
        }
    }

    // Mostrar una alerta informativa al usuario.
    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Mostrar una alerta de error al usuario.
    public void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos públicos para navegar a pantallas específicas.
    public void irAdminCurso() { cambiarPantalla("adminCurso"); }
    public void irAdminServicio() { cambiarPantalla("adminServicio"); }
    public void irCalificaciones() { cambiarPantalla("calificaciones"); }
    public void irCalificar() { cambiarPantalla("calificar"); }
    public void irCarrito() { cambiarPantalla("carrito"); }
    public void irCatalogo() { cambiarPantalla("catalogo"); }
    public void irCategoria() { cambiarPantalla("categoria"); }
    public void irCliente() { cambiarPantalla("cliente"); }
    public void irComprobante() { cambiarPantalla("comprobante"); }
    public void irEmprendedor() { cambiarPantalla("emprendedor"); }
    public void irLogin() { cambiarPantalla("login"); }
    public void irPago() { cambiarPantalla("pago"); }
    public void irProductosCliente() { cambiarPantalla("productosCliente"); }
    public void irProductosEmprendedor() { cambiarPantalla("productosEmprendedor"); }
    public void irReclutador()   { cambiarPantalla("reclutador"); }
    public void irRegistro() { cambiarPantalla("registro"); }
    public void irSolicitudEmprendedor() { cambiarPantalla("solicitudEmprendedor"); }
    public void irSolicitudProducto() { cambiarPantalla("solicitudProducto"); }
    public void irSolicitudes() { cambiarPantalla("solicitudes"); }
}
