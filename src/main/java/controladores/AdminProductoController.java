package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Material;
import modelo.Producto;
import servicio.*;

import java.io.IOException;

public class AdminProductoController {

    @FXML private TextField txtTitulo, txtPrecio, txtDuracionCurso, txtDuracionServicio,
            txtNivel, txtCertificacion, txtUbicacion, txtModalidad;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar, btnVolver, btnAgregarMaterial, btnEliminarMaterial;
    @FXML private TableView<Material> tablaMateriales;
    @FXML private TableColumn<Material, String> colTituloMat, colTipoMat, colUrlMat;

    private Producto productoSeleccionado;
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;
    private final ObservableList<Material> materiales = FXCollections.observableArrayList();

    public AdminProductoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
    }

    @FXML
    public void initialize() {
        if (tablaMateriales != null) {
            colTituloMat.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitulo()));
            colTipoMat.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTipo()));
            colUrlMat.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUrl()));
            tablaMateriales.setItems(materiales);
        }
    }

    public void setProducto(Producto producto) {
        this.productoSeleccionado = producto;
        if (producto == null) return;

        txtTitulo.setText(producto.getTitulo());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
    }

    @FXML
    private void onGuardar() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "No hay producto cargado.");
            return;
        }

        try {
            if (!txtTitulo.getText().isBlank()) productoSeleccionado.setTitulo(txtTitulo.getText());
            if (!txtDescripcion.getText().isBlank()) productoSeleccionado.setDescripcion(txtDescripcion.getText());
            if (!txtPrecio.getText().isBlank())
                productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText()));

            // Ejemplo: actualización usando servicioP
            servicioP.actualizarProducto(productoSeleccionado);

            mostrarAlerta("Guardado", "Cambios guardados correctamente.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        cambiarPantalla("/puj.fis.pantallas/productosE.fxml", "Mis Productos", btnVolver);
    }

    @FXML
    private void onAgregarMaterial() {
        if (tablaMateriales == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuevo Material");
        dialog.setHeaderText("Agregar nuevo material");
        dialog.setContentText("Título:");
        dialog.showAndWait().ifPresent(titulo -> {
            Material m = new Material(0, titulo, "Archivo", "http://enlace.com");
            materiales.add(m);
        });
    }

    @FXML
    private void onEliminarMaterial() {
        if (tablaMateriales == null) return;
        Material seleccionado = tablaMateriales.getSelectionModel().getSelectedItem();
        if (seleccionado != null) materiales.remove(seleccionado);
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Cambia a la pantalla especificada por el path del FXML, con el título dado, usando el botón como referencia para obtener la ventana actual.
    private void cambiarPantalla(String fxmlPath, String titulo, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS, servicioCal);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla: " + titulo);
        }
    }
}
