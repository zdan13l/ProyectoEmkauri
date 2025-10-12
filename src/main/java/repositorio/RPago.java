package repositorio;

import modelo.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repositorio JDBC para la entidad Pago.
 * Utiliza ConexionDB.getConnection() en cada operación (patrón ya usado en RUsuario).
 */
public class RPago {

    public Pago crear(Pago p) {
        String sql = "INSERT INTO Pagos (monto, metodo, fecha) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setBigDecimal(1, java.math.BigDecimal.valueOf(p.getMonto()));
            st.setString(2, p.getMetodo());
            st.setDate(3, new java.sql.Date(p.getFecha().getTime()));
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdPago(rs.getInt(1));
                }
            }
            return p;
        } catch (Exception e) {
            throw new RuntimeException("Error creando pago: " + e.getMessage(), e);
        }
    }

    public Pago buscarPorId(int idPago) {
        String sql = "SELECT idPago, monto, metodo, fecha FROM Pagos WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idPago);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return null;
                Pago p = new Pago();
                p.setIdPago(rs.getInt("idPago"));
                p.setMonto(rs.getBigDecimal("monto").doubleValue());
                p.setMetodo(rs.getString("metodo"));
                Date fecha = new Date(rs.getDate("fecha").getTime());
                p.setFecha(fecha);
                return p;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando pago: " + e.getMessage(), e);
        }
    }

    public List<Pago> listar() {
        String sql = "SELECT idPago, monto, metodo, fecha FROM Pagos ORDER BY idPago DESC";
        List<Pago> list = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Pago p = new Pago();
                p.setIdPago(rs.getInt("idPago"));
                p.setMonto(rs.getBigDecimal("monto").doubleValue());
                p.setMetodo(rs.getString("metodo"));
                Date fecha = new Date(rs.getDate("fecha").getTime());
                p.setFecha(fecha);
                list.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando pagos: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean actualizar(Pago p) {
        String sql = "UPDATE Pagos SET monto=?, metodo=?, fecha=? WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setBigDecimal(1, java.math.BigDecimal.valueOf(p.getMonto()));
            st.setString(2, p.getMetodo());
            st.setDate(3, new java.sql.Date(p.getFecha().getTime()));
            st.setInt(4, p.getIdPago());
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando pago: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(int idPago) {
        String sql = "DELETE FROM Pagos WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idPago);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando pago: " + e.getMessage(), e);
        }
    }
}
