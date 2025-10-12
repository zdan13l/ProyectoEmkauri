package servicio;

import modelo.Pago;
import repositorio.RCompra;
import repositorio.RPago;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class SPago implements ISPago {

    private final RPago rPago;
    private final RCompra rCompra;
    private final DataSource ds;

    public SPago(RPago rPago, RCompra rCompra, DataSource ds) {
        this.rPago = rPago;
        this.rCompra = rCompra;
        this.ds = ds;
    }

    @Override public Pago crear(Pago p) throws SQLException { return rPago.crear(p); }
    @Override public Pago buscarPorId(Long id) throws SQLException { return rPago.buscarPorId(id); }
    @Override public List<Pago> listar() throws SQLException { return rPago.listar(); }
    @Override public boolean actualizar(Pago p) throws SQLException { return rPago.actualizar(p); }

    @Override
    public boolean eliminar(Long id) throws SQLException {
        // Si el pago está asociado, desasociar previamente
        Pago p = rPago.buscarPorId(id);
        if (p != null && p.getIdCompra() != null) {
            rCompra.desasociarPagoDeCompra(p.getIdCompra());
        }
        return rPago.eliminar(id);
    }

    @Override
    public void asociarPagoACompra(Long idPago, Long idCompra) throws SQLException {
        // Regla: una compra no puede tener más de un pago
        if (rCompra.compraTienePago(idCompra))
            throw new IllegalStateException("La compra ya tiene un pago asociado");

        // Transacción simple
        try (Connection c = ds.getConnection()) {
            try {
                c.setAutoCommit(false);
                rCompra.asociarPagoACompra(idCompra, idPago);
                // Actualiza también el campo id_compra en la fila del pago para ver la relación desde ambos lados (opcional)
                Pago p = rPago.buscarPorId(idPago);
                if (p == null) throw new IllegalArgumentException("Pago no existe");
                p.setIdCompra(idCompra);
                rPago.actualizar(p);
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    @Override
    public void desasociarPagoDeCompra(Long idCompra) throws SQLException {
        rCompra.desasociarPagoDeCompra(idCompra);
    }
}
