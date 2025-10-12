package servicio;

import modelo.Pago;
import java.sql.SQLException;
import java.util.List;

public interface ISPago {
    Pago crear(Pago p) throws SQLException;
    Pago buscarPorId(Long id) throws SQLException;
    List<Pago> listar() throws SQLException;
    boolean actualizar(Pago p) throws SQLException;
    boolean eliminar(Long id) throws SQLException;

    // reglas de CU-009
    void asociarPagoACompra(Long idPago, Long idCompra) throws SQLException;
    void desasociarPagoDeCompra(Long idCompra) throws SQLException;
}
