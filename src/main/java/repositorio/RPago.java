package repositorio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class RPago {

    public int insertarPago(double monto, String metodo, String estado, LocalDate fecha) {
        String sql = "INSERT INTO Pago (monto, metodo, estado, fecha) VALUES (?,?,?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, monto);
            ps.setString(2, metodo);
            ps.setString(3, estado);
            ps.setDate(4, Date.valueOf(fecha));
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("Error insertarPago: " + e.getMessage());
        }
        return -1;
    }
}
