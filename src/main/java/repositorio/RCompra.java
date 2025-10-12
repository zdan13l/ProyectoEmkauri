package repositorio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class RCompra {

    public boolean existeCompraAprobada(int idCliente, int idCurso) {
        String sql = "SELECT 1 FROM Compra co " +
                "JOIN Pago p ON p.idPago = co.idPago " +
                "WHERE co.idCliente = ? AND co.idCurso = ? AND LOWER(p.estado) IN ('completado','aprobado') " +
                "FETCH FIRST 1 ROWS ONLY";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idCurso);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error existeCompraAprobada: " + e.getMessage());
            return false;
        }
    }

    public int insertarCompra(LocalDate fecha, int idCliente, Integer idCurso, Integer idServicio, int idPago) {
        String sql = "INSERT INTO Compra (fecha, idCliente, idCurso, idServicio, idPago) VALUES (?,?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setInt(2, idCliente);
            if (idCurso == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, idCurso);
            if (idServicio == null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, idServicio);
            ps.setInt(5, idPago);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("Error insertarCompra: " + e.getMessage());
        }
        return -1;
    }
}
