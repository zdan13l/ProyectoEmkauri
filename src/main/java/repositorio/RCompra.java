package repositorio;

import javax.sql.DataSource;
import java.sql.*;

public class RCompra {
    private final DataSource ds;
    public RCompra(DataSource ds) { this.ds = ds; }

    /** ¿La compra ya tiene un pago? */
    public boolean compraTienePago(Long idCompra) throws SQLException {
        String sql = "SELECT id_pago FROM Compras WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, idCompra);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return false;
                return rs.getObject("id_pago") != null;
            }
        }
    }

    /** Enlaza pago a compra (1:1). */
    public void asociarPagoACompra(Long idCompra, Long idPago) throws SQLException {
        String sql = "UPDATE Compras SET id_pago=? WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, idPago);
            st.setLong(2, idCompra);
            st.executeUpdate();
        }
    }

    /** Quita enlace (por si se elimina/anula el pago). */
    public void desasociarPagoDeCompra(Long idCompra) throws SQLException {
        String sql = "UPDATE Compras SET id_pago=NULL WHERE id=?";
        try (Connection c = ds.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, idCompra);
            st.executeUpdate();
        }
    }
}
