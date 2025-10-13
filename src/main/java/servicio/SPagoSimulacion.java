package servicio;

import modelo.Pago;
import modelo.PagoSolicitud;
import modelo.PaymentStatus;
import modelo.PaymentMethod;
import repositorio.RPago;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;

public class SPagoSimulacion {

    private final RPago rPago;
    private final DataSource ds;

    public SPagoSimulacion(RPago rPago, DataSource ds) {
        this.rPago = rPago;
        this.ds = ds;
    }

    public Pago simular(PagoSolicitud req) throws SQLException {
        // Validación básica
        if (!ReglasPago.solicitudBasicaValida(req)) {
            return persistir(req, PaymentStatus.RECHAZADO);
        }
        // Luhn + fecha + cvv
        String numero = req.getNumeroTarjeta() == null ? "" : req.getNumeroTarjeta().replaceAll("\\s+","");
        boolean ok = LuhnValidator.isValid(numero)
                && ReglasPago.fechaValida(req.getMesExp(), req.getAnioExp())
                && ReglasPago.cvvValido(req.getCvv());

        // Umbral MVP (ajustable)
        boolean montoOk = req.getMonto().signum() > 0 && req.getMonto().doubleValue() <= 5_000_000d;

        return persistir(req, (ok && montoOk) ? PaymentStatus.APROBADO : PaymentStatus.RECHAZADO);
    }

    private Pago persistir(PagoSolicitud req, PaymentStatus status) throws SQLException {
        // Construir el Pago con tu modelo (idCompra null aquí; se asocia en otro flujo)
        Pago pago = new Pago(
                null,                    // id
                null,                    // id_compra (null en simulación; se asociará desde otro flujo)
                req.getMonto(),          // BigDecimal
                metodoTexto(req.getMetodo()),
                LocalDate.now(),         // fecha
                status.name()            // estado
        );
        // Persistir con TU RPago actual (no modificado)
        return rPago.crear(pago);
    }

    private String metodoTexto(PaymentMethod m) {
        return m == null ? "OTRO" : m.name();
    }
}
