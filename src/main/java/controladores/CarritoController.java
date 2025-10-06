package controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.ItemCarrito;
import servicio.SCarrito;

public class CarritoController {

    @FXML private ListView<ItemCarrito> listView;
    @FXML private Label lblTotal;
    @FXML private Button btnEliminar;
    @FXML private Button btnConfirmar;

    private final SCarrito sCarrito = SCarrito.getInstance();

    @FXML
    public void initialize() {
        refrescar();
    }

    @FXML
    public void onEliminar() {
        ItemCarrito seleccionado = listView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un producto para eliminar.");
            return;
        }
        sCarrito.eliminarItem(seleccionado.getId());
        refrescar();
    }

    @FXML
    public void onConfirmar() {
        double total = sCarrito.calcularTotal();
        if (total == 0) {
            mostrarError("El carrito está vacío.");
            return;
        }
        mostrarInfo("Compra confirmada.\nTotal: $" + total + "\nRedirigiendo al módulo de pagos...");
        sCarrito.vaciar();
        refrescar();
    }

    private void refrescar() {
        listView.setItems(FXCollections.observableArrayList(sCarrito.listar()));
        lblTotal.setText("Total: $" + sCarrito.calcularTotal());
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
