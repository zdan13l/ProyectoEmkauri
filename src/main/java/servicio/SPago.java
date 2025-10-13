package servicio;

import modelo.Pago;
import repositorio.RCompra;
import repositorio.RPago;

import java.util.List;
import java.util.Objects;

public class SPago implements ISPago {
    private final RPago rPago;
    private final RCompra rCompra;

    public SPago(RPago rPago, RCompra rCompra) {
        this.rPago = Objects.requireNonNull(rPago, "rPago");
        this.rCompra = Objects.requireNonNull(rCompra, "rCompra");
    }

    @Override
    public Pago crear(Pago pago) {
        Objects.requireNonNull(pago, "pago");
        return rPago.crear(pago);
    }

    @Override
    public Pago buscarPorId(long idPago) {
        validarId(idPago);
        return rPago.buscarPorId(idPago);
    }

    @Override
    public List<Pago> listar() {
        return rPago.listar();
    }

    @Override
    public boolean actualizar(Pago pago) {
        Objects.requireNonNull(pago, "pago");
        if (pago.getId() == null) {
            throw new IllegalArgumentException("El pago no tiene ID.");
        }
        return rPago.actualizar(pago);
    }

    @Override
    public boolean eliminar(long idPago) {
        validarId(idPago);
        return rPago.eliminar(idPago);
    }

    @Override
    public void asociarPagoACompra(long idPago, int idCompra) {
        validarId(idPago);
        if (idCompra <= 0) {
            throw new IllegalArgumentException("ID de compra invalido.");
        }
        if (rCompra.compraTienePago(idCompra)) {
            throw new IllegalStateException("La compra ya tiene un pago asociado.");
        }
        rCompra.asociarPagoACompra(idCompra, Math.toIntExact(idPago));
    }

    @Override
    public void desasociarPagoDeCompra(int idCompra) {
        if (idCompra <= 0) {
            throw new IllegalArgumentException("ID de compra invalido.");
        }
        rCompra.desasociarPagoDeCompra(idCompra);
    }

    private void validarId(long idPago) {
        if (idPago <= 0) {
            throw new IllegalArgumentException("ID de pago invalido.");
        }
    }
}
