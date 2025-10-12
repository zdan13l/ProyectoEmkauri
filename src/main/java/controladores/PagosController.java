package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Pago;
import servicio.ISPago;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PagosController implements IControlador {

    // Servicio inyectado más tarde (Scene Builder necesita ctor sin args)
    private ISPago sPago;

    // UI
    @FXML private TableView<Pago> tabla;
    @FXML private TableColumn<Pago, Number> colId;
    @FXML private TableColumn<Pago, String> colMetodo;
    @FXML private TableColumn<Pago, String> colMonto;
    @FXML private TableColumn<Pago, String> colFecha;

    @FXML private TextField txtMonto, txtIdCompra;
    @FXML private ComboBox<String> cbMetodo;
    @FXML private DatePicker dpFecha;

    private final ObservableList<Pago> datos = FXCollections.observableArrayList();

    public PagosController() {
        // ctor sin args requerido por Scene Builder
    }

    @Override
    public void setServicios(Object... servicios) {
        // Esperamos ISPago como primer argumento
        if (servicios != null && servicios.length > 0 && servicios[0] instanceof ISPago) {
            this.sPago = (ISPago) servicios[0];
            // Si la UI ya está inicializada, podemos cargar datos
            refrescarSeguro();
        }
    }

    @FXML
    public void initialize() {
        // Config tabla
        if (colId != null) {
            colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdPago()));
        }
        if (colMetodo != null) {
            colMetodo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMetodo()));
        }
        if (colMonto != null) {
            colMonto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getMonto())));
        }
        if (colFecha != null) {
            colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getFecha())));
        }
        if (tabla != null) {
            tabla.setItems(datos);
        }

        // Combos/fecha
        if (cbMetodo != null) {
            cbMetodo.setItems(FXCollections.observableArrayList("EFECTIVO", "TARJETA", "TRANSFERENCIA"));
        }
        if (dpFecha != null) {
            dpFecha.setValue(LocalDate.now());
        }

        // Cargar datos solo si el servicio ya fue inyectado
        refrescarSeguro();
    }

    // ===== Botones =====
    @FXML public void onNuevo() {
        if (txtMonto != null) txtMonto.clear();
        if (cbMetodo != null) cbMetodo.getSelectionModel().clearSelection();
        if (dpFecha != null) dpFecha.setValue(LocalDate.now());
        if (txtIdCompra != null) txtIdCompra.clear();
        if (tabla != null) tabla.getSelectionModel().clearSelection();
    }

    @FXML public void onCrear() {
        try {
            Pago p = fromForm(0);
            p = sPago.crear(p);
            datos.add(0, p);
            info("Pago creado con ID " + p.getIdPago());
        } catch (Exception e) { error(e); }
    }

    @FXML public void onActualizar() {
        Pago sel = (tabla == null ? null : tabla.getSelectionModel().getSelectedItem());
        if (sel == null) { warn("Selecciona un pago"); return; }
        try {
            Pago upd = fromForm(sel.getIdPago());
            sPago.actualizar(upd);
            refrescar();
            info("Pago actualizado");
        } catch (Exception e) { error(e); }
    }

    @FXML public void onEliminar() {
        Pago sel = (tabla == null ? null : tabla.getSelectionModel().getSelectedItem());
        if (sel == null) { warn("Selecciona un pago"); return; }
        try {
            sPago.eliminar(sel.getIdPago());
            datos.remove(sel);
            info("Pago eliminado");
        } catch (Exception e) { error(e); }
    }

    @FXML public void onAsociar() {
        Pago sel = (tabla == null ? null : tabla.getSelectionModel().getSelectedItem());
        if (sel == null) { warn("Selecciona un pago"); return; }
        Integer idCompra = parseIntOrNull(txtIdCompra == null ? null : txtIdCompra.getText());
        if (idCompra == null) { warn("ID Compra requerido"); return; }
        try {
            sPago.asociarPagoACompra(sel.getIdPago(), idCompra);
            info("Pago asociado a compra " + idCompra);
        } catch (Exception e) { error(e); }
    }

    @FXML public void onDesasociar() {
        Integer idCompra = parseIntOrNull(txtIdCompra == null ? null : txtIdCompra.getText());
        if (idCompra == null) { warn("ID Compra requerido"); return; }
        try {
            sPago.desasociarPagoDeCompra(idCompra);
            info("Pago desasociado de compra " + idCompra);
        } catch (Exception e) { error(e); }
    }

    @FXML public void onRefrescar() { refrescar(); }

    // ===== Helpers =====
    private void refrescarSeguro() {
        // Solo refresca si ya tenemos servicio (Scene Builder lo abrirá sin servicio)
        if (this.sPago != null) refrescar();
    }

    private void refrescar() {
        try {
            List<Pago> lista = sPago.listar();
            datos.setAll(lista);
        } catch (Exception e) { error(e); }
    }

    private Pago fromForm(int idPago) {
        double monto = Double.parseDouble(txtMonto.getText().trim());
        String metodo = cbMetodo.getValue();
        LocalDate ld = dpFecha.getValue();
        Date fecha = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Pago p = new Pago();
        p.setIdPago(idPago);
        p.setMonto(monto);
        p.setMetodo(metodo);
        p.setFecha(fecha);
        return p;
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s.trim());
    }

    private void info(String m){ new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    private void warn(String m){ new Alert(Alert.AlertType.WARNING, m).showAndWait(); }
    private void error(Exception e){ new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); e.printStackTrace(); }
}
