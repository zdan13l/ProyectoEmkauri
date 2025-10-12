package servicio;

import modelo.Pago;
import repositorio.RCompra;
import repositorio.RPago;

import java.util.List;

/**
 * Implementación del servicio de Pagos con reglas de negocio mínimas.
 * - CRUD de pagos
 * - Asociación 1:1 con Compras (una compra no puede tener más de un pago).
 */
public class SPago implements ISPago {
    private final RPago rPago;
    private final RCompra rCompra;

    public SPago(RPago rPago, RCompra rCompra) {
        this.rPago = rPago;
        this.rCompra = rCompra;
    }

    @Override public Pago crear(Pago pago) { return rPago.crear(pago); }
    @Override public Pago buscarPorId(int idPago) { return rPago.buscarPorId(idPago); }
    @Override public List<Pago> listar() { return rPago.listar(); }
    @Override public boolean actualizar(Pago pago) { return rPago.actualizar(pago); }
    @Override public boolean eliminar(int idPago) { return rPago.eliminar(idPago); }

    @Override
    public void asociarPagoACompra(int idPago, int idCompra) {
        if (rCompra.compraTienePago(idCompra)) {
            throw new IllegalStateException("La compra ya tiene un pago asociado.");
        }
        rCompra.asociarPagoACompra(idCompra, idPago);
    }

    @Override
    public void desasociarPagoDeCompra(int idCompra) {
        rCompra.desasociarPagoDeCompra(idCompra);
    }
}
