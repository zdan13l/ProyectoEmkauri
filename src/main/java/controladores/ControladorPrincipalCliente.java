package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.io.IOException;

public class ControladorPrincipalCliente {

    @FXML
    private Label lblBienvenida;

    @FXML
    private VBox contenedorProductos;

    @FXML
    private Button btnCatalogo;

    @FXML
    private Button btnCarrito;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private ImageView logoEmkauri;

    @FXML
    public void initialize() {
        // Mensaje inicial de bienvenida
        lblBienvenida.setText("¡Bienvenido, Cliente!");

        // Cargar el logo (por si no se muestra correctamente desde el FXML)
        try {
            Image logo = new Image(getClass().getResourceAsStream("/img/Emkauri_logo.png"));
            logoEmkauri.setImage(logo);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar el logo de Emkauri: " + e.getMessage());
        }

        // Simular carga de productos
        cargarProductosDemo();
    }

    private void cargarProductosDemo() {
        contenedorProductos.getChildren().clear();

        for (int i = 1; i <= 4; i++) {
            Label producto = new Label("🛒 Producto " + i + " - Precio: $" + (i * 10000));
            producto.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; "
                    + "-fx-background-color: #F9F9F9; -fx-padding: 10; "
                    + "-fx-background-radius: 8; -fx-pref-width: 640; "
                    + "-fx-border-color: #E0E0E0; -fx-border-radius: 8;");
            contenedorProductos.getChildren().add(producto);
        }
    }

    @FXML
    private void onVerProductos(ActionEvent event) {
        mostrarMensaje("Catálogo", "Mostrando todos los productos disponibles.");
        cargarProductosDemo();
    }

    @FXML
    private void onCarrito(ActionEvent event) {
        mostrarMensaje("Carrito", "Mostrando los productos en tu carrito.");
    }

    @FXML
    private void onHistorial(ActionEvent event) {
        mostrarMensaje("Historial", "Mostrando tu historial de compras.");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj/fis/pantallas/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("⚠️ Error al volver al login: " + e.getMessage());
            mostrarMensaje("Error", "No se pudo volver al login.");
        }
    }

    private void mostrarMensaje(String titulo, String contenido) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
