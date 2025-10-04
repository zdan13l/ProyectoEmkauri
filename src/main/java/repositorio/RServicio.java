package repositorio;

import modelo.Servicio;
import repositorio.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RServicio {

    public Servicio crear(Servicio s) throws SQLException {
        String sql = """
            INSERT INTO SERVICIO (nombre, descripcion, precio, estado)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setDouble(3, s.getPrecio());
            ps.setString(4, s.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Servicio(id, s.getPrecio(), s.getNombre(), s.getDescripcion(),
                            s.getEstado(), s.getCategoria(), s.getEmprendedor());
                }
            }
        }
        throw new SQLException("No se pudo insertar SERVICIO");
    }

    public Servicio buscarPorId(int id) throws SQLException {
        String sql = "SELECT idServicio, nombre, descripcion, precio, estado FROM SERVICIO WHERE idServicio = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Servicio> listar() throws SQLException {
        String sql = "SELECT idServicio, nombre, descripcion, precio, estado FROM SERVICIO";
        List<Servicio> out = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(map(rs));
            }
        }
        return out;
    }

    public boolean actualizar(Servicio s) throws SQLException {
        String sql = """
            UPDATE SERVICIO
               SET nombre=?, descripcion=?, precio=?, estado=?
             WHERE idServicio=?
        """;
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setDouble(3, s.getPrecio());
            ps.setString(4, s.getEstado());
            ps.setInt(5, s.getIdServicio());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM SERVICIO WHERE idServicio = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private Servicio map(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getInt("idServicio"),
                rs.getDouble("precio"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getString("estado"),
                null, // Categoria no se trae aquí
                null  // Emprendedor tampoco
        );
    }
}
