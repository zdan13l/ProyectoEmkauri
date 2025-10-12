package controladores;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Pago;
import servicio.ISPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class PagosController implements IControlador {

    // Servicio inyectado mas tarde (Scene Builder necesita ctor sin args)
    private ISPago sPago;

    // UI
    @FXML private TableView<Pago> tabla;
    @FXML private TableColumn<Pago, Number> colId;
    @FXML private TableColumn<Pago, String> colMetodo;
    @FXML private TableColumn<Pago, String> colMonto;
    @FXML private TableColumn<Pago, String> colFecha;

    @FXML private TextField txtMonto;
    @FXML private TextField txtIdCompra;
    @FXML private ComboBox<String> cbMetodo;
    @FXML private DatePicker dpFecha;

    private final ObservableList<Pago> datos = FXCollections.observableArrayList();

    public PagosController() {
        // ctor sin args requerido por Scene Builder
    }

    public PagosController(ISPago servicioPago) {
        this.sPago = servicioPago;
    }

    @Override
    public void setServicios(Object... servicios) {
        if (servicios != null && servicios.length > 0 && servicios[0] instanceof ISPago) {
            this.sPago = (ISPago) servicios[0];
            refrescarSeguro();
        }
    }

    @FXML
    public void initialize() {
        if (colId != null) {
            colId.setCellValueFactory(c -> {
                Long id = c.getValue().getId();
                return new ReadOnlyObjectWrapper<>(id != null ? id : 0L);
            });
        }
        if (colMetodo != null) {
            colMetodo.setCellValueFactory(c ->
                    new ReadOnlyObjectWrapper<>(valorSeguro(c.getValue().getMetodo()))
            );
        }
        if (colMonto != null) {
            colMonto.setCellValueFactory(c -> {
                BigDecimal monto = c.getValue().getMonto();
                String texto = monto != null ? monto.toPlainString() : "";
                return new ReadOnlyObjectWrapper<>(texto);
            });
        }
        if (colFecha != null) {
            colFecha.setCellValueFactory(c -> {
                LocalDate fecha = c.getValue().getFecha();
                String texto = fecha != null ? fecha.toString() : "";
                return new ReadOnlyObjectWrapper<>(texto);
            });
        }
        if (tabla != null) {
            tabla.setItems(datos);
        }

        if (cbMetodo != null) {
            cbMetodo.setItems(FXCollections.observableArrayList("EFECTIVO", "TARJETA", "TRANSFERENCIA"));
        }
        if (dpFecha != null) {
            dpFecha.setValue(LocalDate.now());
        }

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
        if (!servicioDisponible(true)) return;
        try {
            Pago p = fromForm(null);
            p = sPago.crear(p);
            datos.add(0, p);
            info("Pago creado con ID " + (p.getId() != null ? p.getId() : ""));
        } catch (Exception e) {
            error(e);
        }
    }

    @FXML public void onActualizar() {
        if (!servicioDisponible(true)) return;
        Pago sel = (tabla == null ? null : tabla.getSelectionModel().getSelectedItem());
        if (sel == null) {
            warn("Selecciona un pago");
            return;
        }
        if (sel.getId() == null) {
            warn("El pago seleccionado no tiene ID");
            return;
        }
        try {
            Pago upd = fromForm(sel.getId());
            sPago.actualizar(upd);
            refrescar();
            info("Pago actualizado");
        } catch (Exception e) {
            error(e);
        }
    }

    @FXML public void onEliminar() {
        if (!servicioDisponible(true)) return;
        Pago sel = (tabla == null ? null : tabla.getSelectionModel().getSelectedItem());
        if (sel == null) {
            warn("Selecciona un pago");
            return;
        }
        if (sel.getId() == null) {
            warn("El pago seleccionado no tiene ID");
            return;
        }
        try {
            sPago.eliminar(sel.getId());
            datos.remove(sel);
            info("Pago eliminado");
        } catch (Exception e) {
            error(e);
        }
    }

    @FXML public void onAsociar() {
        if (!servicioDisponible(true)) return;
        Pago sel = (tabla == null ? null : tabla.getSelectionModel().getSelectedItem());
        if (sel == null) {
            warn("Selecciona un pago");
            return;
        }
        if (sel.getId() == null) {
            warn("El pago seleccionado no tiene ID");
            return;
        }
        Integer idCompra = parseIntOrNull(txtIdCompra == null ? null : txtIdCompra.getText());
        if (idCompra == null) {
            warn("ID de compra requerido");
            return;
        }
        try {
            sPago.asociarPagoACompra(sel.getId(), idCompra);
            info("Pago asociado a la compra " + idCompra);
        } catch (Exception e) {
            error(e);
        }
    }

    @FXML public void onDesasociar() {
        if (!servicioDisponible(true)) return;
        Integer idCompra = parseIntOrNull(txtIdCompra == null ? null : txtIdCompra.getText());
        if (idCompra == null) {
            warn("ID de compra requerido");
            return;
        }
        try {
            sPago.desasociarPagoDeCompra(idCompra);
            info("Pago desasociado de la compra " + idCompra);
        } catch (Exception e) {
            error(e);
        }
    }

    @FXML public void onRefrescar() {
        refrescar();
    }

    // ===== Helpers =====
    private void refrescarSeguro() {
        if (sPago != null) {
            refrescar();
        }
    }

    private void refrescar() {
        if (!servicioDisponible(false)) return;
        try {
            List<Pago> lista = sPago.listar();
            datos.setAll(lista != null ? lista : Collections.emptyList());
        } catch (Exception e) {
            error(e);
        }
    }

    private Pago fromForm(Long idPago) {
        if (txtMonto == null || cbMetodo == null || dpFecha == null) {
            throw new IllegalStateException("Formulario no inicializado correctamente.");
        }

        String montoTxt = txtMonto.getText();
        if (montoTxt == null || montoTxt.isBlank()) {
            throw new IllegalArgumentException("El monto es obligatorio.");
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(montoTxt.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Formato de monto invalido.", ex);
        }

        String metodo = cbMetodo.getValue();
        if (metodo == null || metodo.isBlank()) {
            throw new IllegalArgumentException("Selecciona un metodo de pago.");
        }

        LocalDate fecha = dpFecha.getValue();
        if (fecha == null) {
            throw new IllegalArgumentException("Selecciona una fecha.");
        }

        Pago p = new Pago();
        p.setId(idPago);
        p.setMonto(monto);
        p.setMetodo(metodo);
        p.setFecha(fecha);
        return p;
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Integer.parseInt(s.trim());
    }

    private boolean servicioDisponible(boolean notificar) {
        if (sPago == null) {
            if (notificar) {
                warn("Servicio de pagos no disponible. Cierra y vuelve a abrir la pantalla.");
            }
            return false;
        }
        return true;
    }

    private String valorSeguro(String texto) {
        return texto != null ? texto : "";
    }

    private void info(String mensaje) {
        new Alert(Alert.AlertType.INFORMATION, mensaje).showAndWait();
    }

    private void warn(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje).showAndWait();
    }

    private void error(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : e.toString();
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
        e.printStackTrace();
    }
}
