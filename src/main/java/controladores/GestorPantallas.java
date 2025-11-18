package controladores;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GestorPantallas {
    // Referencia al Stage principal y a la fábrica de controladores.
    private final Stage stage;
    private final FabricaController fabrica;

    // Mapa de rutas y títulos de pantallas por clave.
    private final Map<String, String> pantallas = new HashMap<>();
    private final Map<String, String> titulos = new HashMap<>();

    // Constructor que recibe el Stage principal y la fábrica de controladores.
    public GestorPantallas(Stage stage, FabricaController fabrica) {
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
        pantallas.put("solicitud", "/puj.fis.pantallas/solicitudes.fxml");
        pantallas.put("solicitudes", "/puj.fis.pantallas/solicitudProducto.fxml");

        // Títulos de las pantallas.
        titulos.put("adminCurso", "Administración de Cursos - Emkauri");
        titulos.put("adminServicio", "Administración de Servicios - Emkauri");
        titulos.put("calificaciones", "Listado de Calificaciones - Emkauri");
        titulos.put("calificar", "Calificar Producto - Emkauri");
        titulos.put("carrito", "Carrito de Compras - Emkauri");
        titulos.put("catalogo", "Catálogo de Productos - Emkauri");
        titulos.put("categoria", "Categorías de Productos - Emkauri");
        titulos.put("cliente", "Panel Cliente - Emkauri");
        titulos.put("comprobante", "Comprobante de Compra - Emkauri");
        titulos.put("emprendedor", "Panel Emprendedor - Emkauri");
        titulos.put("login", "Login - Emkauri");
        titulos.put("pago", "Realizar Pago - Emkauri");
        titulos.put("productosCliente", "Productos del Cliente - Emkauri");
        titulos.put("productosEmprendedor", "Productos del Emprendedor - Emkauri");
        titulos.put("reclutador", "Panel Reclutador - Emkauri");
        titulos.put("registro", "Registro de Usuario - Emkauri");
        titulos.put("solicitud", "Gestión de Solicitudes - Emkauri");
        titulos.put("solicitudes", "Solicitudes Pendientes - Emkauri");
    }

    // Cambiar la pantalla actual a la indicada por la clave.
    private void cambiarPantalla(String clavePantalla) {
        try {
            String ruta = pantallas.get(clavePantalla);

            // Inyectar controladores desde la fábrica.
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            loader.setControllerFactory(fabrica::createController);

            // Cargar la nueva escena.
            Scene scene = new Scene(loader.load());
            String titulo = titulos.getOrDefault(clavePantalla, "Emkauri");
            stage.setTitle(titulo);
            stage.setMaximized(true);
            aplicarTransicion((Pane) scene.getRoot());
            stage.setScene(scene);

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar pantalla: " + clavePantalla, e);
        }
    }

    // Abrir la pantalla de solicitudes de producto con el tipo seleccionado.
    public void abrirSolicitudes(String clavePantalla, String tipoProducto) {
        try {
            String ruta = pantallas.get(clavePantalla);

            // Inyectar controladores desde la fábrica.
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            loader.setControllerFactory(fabrica::createController);

            Pane root = loader.load();

            // Obtener el controlador de JavaFX.
            SolicitudProductoController controller = loader.getController();
            controller.seleccionarTipo(tipoProducto);

            Scene scene = new Scene(root);
            stage.setTitle(titulos.get("solicitudes"));
            stage.setMaximized(true);
            aplicarTransicion(root);
            stage.setScene(scene);

        } catch (Exception e) {
            mostrarError("Error", "No se pudo abrir la solicitud de producto.");
        }
    }

    // Abrir la pantalla de solicitudes de producto con el tipo seleccionado.
    private void abrirGestionSolicitudes(String clavePantalla, String tipoProducto) {
        try {
            String ruta = pantallas.get(clavePantalla);

            // Inyectar controladores desde la fábrica.
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            loader.setControllerFactory(fabrica::createController);

            Pane root = loader.load();

            // Obtener el controlador real que JavaFX creó
            SolicitudController controller = loader.getController();
            controller.setTipoSolicitud(tipoProducto);
            controller.cargarSolicitudes();

            Scene scene = new Scene(root);
            stage.setTitle(titulos.get("solicitudes"));
            stage.setMaximized(true);
            aplicarTransicion(root);
            stage.setScene(scene);

        } catch (Exception e) {
            mostrarError("Error", "No se pudo abrir la solicitud de producto.");
        }
    }

    // Aplicar una transición de desvanecimiento al cambiar de pantalla.
    private void aplicarTransicion(Pane root) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), root);
        root.setOpacity(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // Mostrar una alerta de confirmación al usuario.
    public boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
    }

    // Mostrar una alerta de éxito al usuario.
    public void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Mostrar una alerta de advertencia al usuario.
    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
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
    public void irSolicitudEmprendedor(String tipo) { abrirGestionSolicitudes("solicitud", tipo); }
    public void irSolicitudProducto(String tipo) { abrirGestionSolicitudes("solicitud", tipo); }
    public void irSolicitudes(String tipo) { abrirSolicitudes("solicitudes", tipo); }
}
