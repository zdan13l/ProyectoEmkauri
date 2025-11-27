package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import modelo.Producto;
import modelo.Usuario;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class AccederServicioController {

    private final GestorPantallas gestorPantallas;

    @FXML private ComboBox<Producto> comboServicios;
    @FXML private Label lblNombreEmprendedor;
    @FXML private Label lblTelefono;
    @FXML private Button btnWhatsapp;
    @FXML private Button btnVolver;

    private Producto servicioSeleccionado;
    private List<Producto> productosComprados;

    public AccederServicioController(GestorPantallas gestorPantallas) {
        this.gestorPantallas = gestorPantallas;
    }

    @FXML
    private void initialize() {
        configurarComboBox();
        btnWhatsapp.setDisable(true);
    }

    private void configurarComboBox() {

        // *** LISTA VISIBLE DEL COMBOBOX ***
        comboServicios.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitulo());
            }
        });

        // *** TEXTO CUANDO ESTÁ SELECCIONADO ***
        comboServicios.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitulo());
            }
        });

        comboServicios.setOnAction(event -> {
            Producto seleccionado = comboServicios.getValue();
            if (seleccionado != null) {
                cargarEmprendedor(seleccionado);
            }
        });
    }

    // Desde GestorPantallas
    public void setServicio(Producto servicio) {
        this.servicioSeleccionado = servicio;

        if (productosComprados != null) {
            comboServicios.getItems().setAll(productosComprados);
        }

        comboServicios.getSelectionModel().select(servicio);
        cargarEmprendedor(servicio);
    }

    // Recibe la lista completa desde otra vista
    public void cargarDatos(Producto servicioSeleccionado, List<Producto> productosComprados) {
        this.productosComprados = productosComprados;
        setServicio(servicioSeleccionado);
    }

    private void cargarEmprendedor(Producto producto) {

        Usuario emprendedor = producto.getEmprendedor();

        if (emprendedor != null && emprendedor.getDatosPersonales() != null) {

            lblNombreEmprendedor.setText(
                    emprendedor.getDatosPersonales().getNombre() + " " +
                            emprendedor.getDatosPersonales().getApellido()
            );

            String telefono = emprendedor.getDatosPersonales().getTelefono();

            lblTelefono.setText(
                    (telefono != null && !telefono.isBlank()) ? telefono : "No disponible"
            );

            btnWhatsapp.setDisable(telefono == null || telefono.isBlank());

        } else {

            lblNombreEmprendedor.setText("No disponible");
            lblTelefono.setText("—");
            btnWhatsapp.setDisable(true);
        }
    }

    @FXML
    private void onContactarWhatsapp(ActionEvent event) {
        String telefono = lblTelefono.getText();
        if (telefono == null || telefono.equals("—") || telefono.equals("No disponible")) return;

        String mensaje = "¡Hola! Me comunico desde Emkauri sobre el servicio que compré.";
        String url = "https://wa.me/" + telefono + "?text=" + mensaje.replace(" ", "%20");

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onVolver(ActionEvent event) {
        gestorPantallas.irProductosCliente();
    }
}
