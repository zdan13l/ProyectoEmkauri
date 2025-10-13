package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Pago;
import modelo.PagoSolicitud;
import modelo.PaymentMethod;
import repositorio.RPago;
import servicio.SPagoSimulacion;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;

public class PagoSimulacionController {

    @FXML private TextField txtTitular, txtTarjeta, txtMes, txtAnio, txtCVV, txtMonto;
    @FXML private ComboBox<PaymentMethod> cbMetodo;
    @FXML private Label lblResultado;

    private SPagoSimulacion sSim;
    private RPago rPago;

    // Inyéctale tu DataSource real según como lo tengas centralizado
    private DataSource dataSource;

    @FXML
    public void initialize() {
        cbMetodo.getItems().setAll(PaymentMethod.values());
        // TODO: reemplaza getDataSource() por tu factory real de DS
        this.dataSource = getDataSource();
        this.rPago = new RPago(dataSource);
        this.sSim = new SPagoSimulacion(rPago, dataSource);
    }

    @FXML
    public void onSimular() {
        try {
            String titular = txtTitular.getText();
            String card = txtTarjeta.getText() == null ? "" : txtTarjeta.getText().replaceAll("\\s+","");
            int mes = Integer.parseInt(txtMes.getText());
            int anio = Integer.parseInt(txtAnio.getText());
            String cvv = txtCVV.getText();
            BigDecimal monto = new BigDecimal(txtMonto.getText());
            PaymentMethod metodo = cbMetodo.getValue();

            PagoSolicitud req = new PagoSolicitud(titular, card, mes, anio, cvv, monto, metodo);
            Pago pago = sSim.simular(req);
            lblResultado.setText("Resultado: " + pago.getEstado() + " (id=" + pago.getId() + ")");
        } catch (NumberFormatException nfe) {
            lblResultado.setText("Error: formato de número inválido (MM, YYYY o monto).");
        } catch (SQLException se) {
            lblResultado.setText("Error BD: " + se.getMessage());
        } catch (Exception e) {
            lblResultado.setText("Error: " + e.getMessage());
        }
    }

    // ==== PUNTO DE INTEGRACIÓN DE TU DS ====
    private DataSource getDataSource() {
        // Si ya tienes un singleton o factory, úsalo aquí.
        // Por ejemplo: return MiAppContext.getInstance().getDataSource();
        return null; // <-- reemplaza esto por tu implementación real
    }
}
