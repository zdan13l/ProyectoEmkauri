package servicio;

import modelo.Pago;
import repositorio.RPago;

import java.sql.SQLException;

public class SPago {
    private final RPago rPago = new RPago();

    public int registrarPago(Pago p) throws SQLException {
        // aquí podrías calcular impuestos, validar transiciones, etc.
        return rPago.crear(p);
    }

    public boolean cambiarEstado(int idPago, String nuevoEstado) throws SQLException {
        return rPago.actualizarEstado(idPago, nuevoEstado);
    }
}
