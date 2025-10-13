package repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Repositorio con helpers mínimos para manejar la asociación Pago <-> Compra.
 * La tabla Compras tiene columna idPago UNIQUE que referencia Pagos(idPago).
 */
public class RCompra {

    public boolean compraTienePago(int idCompra) {
        String sql = "SELECT idPago FROM Compras WHERE idCompra=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idCompra);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return false;
                return rs.getObject("idPago") != null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando pago en compra: " + e.getMessage(), e);
        }
    }

    public void asociarPagoACompra(int idCompra, int idPago) {
        String sql = "UPDATE Compras SET idPago=? WHERE idCompra=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idPago);
            st.setInt(2, idCompra);
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error asociando pago a compra: " + e.getMessage(), e);
        }
    }

    public void desasociarPagoDeCompra(int idCompra) {
        String sql = "UPDATE Compras SET idPago=NULL WHERE idCompra=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idCompra);
            st.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error desasociando pago de compra: " + e.getMessage(), e);
        }
    }
}
