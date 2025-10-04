package repositorio;

import modelo.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RPago {

    // ==== CREATE ====
    public int crear(Pago p) throws SQLException {
        validar(p);
        String sql = "INSERT INTO Pago (monto, metodo, estado, fecha) VALUES (?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(p.getMonto()));
            ps.setString(2, p.getMetodo());
            ps.setString(3, p.getEstado());
            ps.setDate(4, new java.sql.Date(p.getFecha().getTime()));

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("No se insertó el pago.");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setIdPago(id);
                    return id;
                } else {
                    throw new SQLException("No se generó idPago.");
                }
            }
        }
    }

    // ==== READ (por id) ====
    public Pago buscarPorId(int idPago) throws SQLException {
        String sql = "SELECT idPago, monto, metodo, estado, fecha FROM Pago WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
                return null;
            }
        }
    }

    // ==== READ (listar todos) ====
    public List<Pago> listar() throws SQLException {
        String sql = "SELECT idPago, monto, metodo, estado, fecha FROM Pago ORDER BY idPago DESC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Pago> out = new ArrayList<>();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        }
    }

    // ==== READ (por cliente) ====
    public List<Pago> listarPorCliente(int idCliente) throws SQLException {
        String sql = """
            SELECT p.idPago, p.monto, p.metodo, p.estado, p.fecha
            FROM Pago p
            JOIN Compra c ON c.idPago = p.idPago
            WHERE c.idCliente = ?
            ORDER BY p.idPago DESC
            """;
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                List<Pago> out = new ArrayList<>();
                while (rs.next()) out.add(mapRow(rs));
                return out;
            }
        }
    }

    // ==== UPDATE (estado) ====
    public boolean actualizarEstado(int idPago, String nuevoEstado) throws SQLException {
        validarEstado(nuevoEstado);
        String sql = "UPDATE Pago SET estado=? WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPago);
            return ps.executeUpdate() > 0;
        }
    }

    // ==== DELETE (para pruebas) ====
    public boolean eliminar(int idPago) throws SQLException {
        String sql = "DELETE FROM Pago WHERE idPago=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        }
    }

    // ==== Helpers ====
    private Pago mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("idPago");
        double monto = rs.getBigDecimal("monto").doubleValue();
        String metodo = rs.getString("metodo");
        String estado = rs.getString("estado");
        Date fecha = new Date(rs.getDate("fecha").getTime());
        return new Pago(id, monto, metodo, estado, fecha);
    }

    private void validar(Pago p) {
        if (p == null) throw new IllegalArgumentException("Pago no puede ser null");
        if (p.getFecha() == null) throw new IllegalArgumentException("Fecha es obligatoria");
        if (p.getMonto() <= 0) throw new IllegalArgumentException("Monto debe ser > 0");
        validarMetodo(p.getMetodo());
        validarEstado(p.getEstado());
    }

    private void validarMetodo(String metodo) {
        if (metodo == null) throw new IllegalArgumentException("Método es obligatorio");
        switch (metodo.toUpperCase()) {
            case "TARJETA":
            case "PSE":
            case "EFECTIVO":
                break;
            default:
                throw new IllegalArgumentException("Método inválido: " + metodo);
        }
    }

    private void validarEstado(String estado) {
        if (estado == null) throw new IllegalArgumentException("Estado es obligatorio");
        switch (estado.toUpperCase()) {
            case "PENDIENTE":
            case "APROBADO":
            case "RECHAZADO":
                break;
            default:
                throw new IllegalArgumentException("Estado inválido: " + estado);
        }
    }
}
