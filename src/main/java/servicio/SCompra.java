package servicio;

import repositorio.RCompra;
import repositorio.RPago;
import java.time.LocalDate;

public class SCompra {

    private final RCompra rCompra = new RCompra();
    private final RPago rPago = new RPago();

    public boolean tieneAcceso(int idCliente, int idCurso) {
        return rCompra.existeCompraAprobada(idCliente, idCurso);
    }

    public boolean comprarCursoConPagoSimulado(int idCliente, int idCurso, double monto) {
        // Regla simple de demo: si monto >= 0 se "aprueba". Sustituye por tu validación real (Luhn, etc.).
        boolean aprobado = (monto >= 0.0);
        int idPago = rPago.insertarPago(monto, "Tarjeta", aprobado ? "Completado" : "Pendiente", LocalDate.now());
        if (!aprobado) return false;
        rCompra.insertarCompra(LocalDate.now(), idCliente, idCurso, null, idPago);
        return true;
    }
}
