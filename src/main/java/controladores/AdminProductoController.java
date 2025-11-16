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
    private final GestorPantallas gestorPantallas;

    public AdminProductoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
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
            gestorPantallas.mostrarAlerta("Error", "No hay producto cargado.");
            return;
        }

        try {
            if (!txtTitulo.getText().isBlank()) productoSeleccionado.setTitulo(txtTitulo.getText());
            if (!txtDescripcion.getText().isBlank()) productoSeleccionado.setDescripcion(txtDescripcion.getText());
            if (!txtPrecio.getText().isBlank())
                productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText()));

            // Ejemplo: actualización usando servicioP
            servicioP.actualizarProducto(productoSeleccionado);

            gestorPantallas.mostrarAlerta("Guardado", "Cambios guardados correctamente.");
        } catch (Exception e) {
            gestorPantallas.mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        gestorPantallas.irProductosEmprendedor();
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
}
