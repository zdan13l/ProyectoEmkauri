package repositorio;

import modelo.Categoria;
import modelo.Curso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RCurso {

    public List<Curso> listarActivos() {
        List<Curso> list = new ArrayList<>();
        String sql = "SELECT c.idCurso, c.nombre, c.descripcion, c.estado, c.precio, " +
                "cat.idCategoria, cat.nombre AS catNombre " +
                "FROM Curso c LEFT JOIN Categoria cat ON c.idCategoria = cat.idCategoria " +
                "WHERE LOWER(c.estado) = 'activo'";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Categoria cat = new Categoria(rs.getInt("idCategoria"), rs.getString("catNombre"), null);
                Curso c = new Curso(
                        rs.getInt("idCurso"),
                        rs.getDouble("precio"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("estado"),
                        cat,
                        null,
                        null
                );
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error listarActivos: " + e.getMessage());
        }
        return list;
    }

    public Curso buscarPorId(int idCurso) {
        String sql = "SELECT c.idCurso, c.nombre, c.descripcion, c.estado, c.precio, " +
                "cat.idCategoria, cat.nombre AS catNombre " +
                "FROM Curso c LEFT JOIN Categoria cat ON c.idCategoria = cat.idCategoria " +
                "WHERE c.idCurso = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Categoria cat = new Categoria(rs.getInt("idCategoria"), rs.getString("catNombre"), null);
                return new Curso(
                        rs.getInt("idCurso"),
                        rs.getDouble("precio"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("estado"),
                        cat,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            System.err.println("Error buscarPorId: " + e.getMessage());
        }
        return null;
    }
}
