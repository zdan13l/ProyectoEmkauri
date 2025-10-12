package repositorio;

import modelo.Pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RPago {

    public Pago crear(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("Pago no puede ser nulo.");
        }
        validarDatosBasicos(pago);

        String sql = "INSERT INTO Pagos (monto, metodo, fecha) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setBigDecimal(1, pago.getMonto());
            st.setString(2, pago.getMetodo());
            st.setDate(3, Date.valueOf(pago.getFecha()));
            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    if (!rs.wasNull()) {
                        pago.setId(id);
                    }
                }
            }
            return pago;
        } catch (SQLException e) {
            throw new RuntimeException("Error creando pago: " + e.getMessage(), e);
        }
    }

    public Pago buscarPorId(long idPago) {
        String sql = "SELECT idPago, monto, metodo, fecha FROM Pagos WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, idPago);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando pago: " + e.getMessage(), e);
        }
    }

    public List<Pago> listar() {
        String sql = "SELECT idPago, monto, metodo, fecha FROM Pagos ORDER BY idPago DESC";
        List<Pago> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando pagos: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean actualizar(Pago pago) {
        if (pago == null || pago.getId() == null) {
            throw new IllegalArgumentException("Pago invalido para actualizar.");
        }
        validarDatosBasicos(pago);

        String sql = "UPDATE Pagos SET monto=?, metodo=?, fecha=? WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setBigDecimal(1, pago.getMonto());
            st.setString(2, pago.getMetodo());
            st.setDate(3, Date.valueOf(pago.getFecha()));
            st.setLong(4, pago.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando pago: " + e.getMessage(), e);
        }
    }

    public boolean eliminar(long idPago) {
        String sql = "DELETE FROM Pagos WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setLong(1, idPago);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando pago: " + e.getMessage(), e);
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        Pago pago = new Pago();

        long id = rs.getLong("idPago");
        if (!rs.wasNull()) {
            pago.setId(id);
        }

        pago.setMonto(rs.getBigDecimal("monto"));
        pago.setMetodo(rs.getString("metodo"));
        Date fechaSql = rs.getDate("fecha");
        if (fechaSql != null) {
            pago.setFecha(fechaSql.toLocalDate());
        }
        return pago;
    }

    private void validarDatosBasicos(Pago pago) {
        if (pago.getMonto() == null) {
            throw new IllegalArgumentException("El monto es obligatorio.");
        }
        if (pago.getMetodo() == null || pago.getMetodo().isBlank()) {
            throw new IllegalArgumentException("El metodo es obligatorio.");
        }
        if (pago.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
    }
}
