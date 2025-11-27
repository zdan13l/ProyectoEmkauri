package repositorio;

import modelo.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RPago implements IRPago {

    // Constructor para inyección de dependencia de la conexión.
    public RPago(Connection connection) {}

    // Crear un pago en la base de datos.
    public boolean crearPago(Pago pago) {
        String sql = "INSERT INTO Pagos (monto, metodo, fecha) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, pago.getMonto());
            ps.setString(2, pago.getMetodo());
            ps.setDate(3, new java.sql.Date(pago.getFecha().getTime()));
            int filas = ps.executeUpdate();

            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    pago.setIdPago(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al crear el pago: " + e.getMessage());
        }
        return false;
    }

    // Obtener un pago por su ID.
    public Pago obtenerPagoPorId(int idPago) {
        String sql = "SELECT * FROM Pagos WHERE idPago = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idPago);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearPago(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pago: " + e.getMessage());
        }
        return null;
    }

    // Obtener todos los pagos.
    public List<Pago> obtenerTodos() {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT * FROM Pagos";
        try (Connection conn = ConexionDB.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                pagos.add(mapearPago(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pagos: " + e.getMessage());
        }
        return pagos;
    }

    // Actualizar un pago existente.
    public boolean actualizarPago(Pago pago) {
        String sql = "UPDATE Pagos SET monto = ?, metodo = ?, fecha = ? WHERE idPago = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, pago.getMonto());
            ps.setString(2, pago.getMetodo());
            ps.setDate(3, new java.sql.Date(pago.getFecha().getTime()));
            ps.setInt(4, pago.getIdPago());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar pago: " + e.getMessage());
        }
        return false;
    }

    // Eliminar un pago por su ID.
    public boolean eliminarPago(int idPago) {
        String sql = "DELETE FROM Pagos WHERE idPago = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pago: " + e.getMessage());
        }
        return false;
    }

    // Mapea un ResultSet a un objeto Pago.
    private Pago mapearPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setIdPago(rs.getInt("idPago"));
        pago.setMonto(rs.getDouble("monto"));
        pago.setMetodo(rs.getString("metodo"));
        pago.setFecha(rs.getDate("fecha"));
        return pago;
    }
}
