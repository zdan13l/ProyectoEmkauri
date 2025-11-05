package repositorio;

import modelo.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Repositorio para manejar operaciones CRUD de la entidad Categoria.
public class RCategoria implements IRCategoria {

    // Agregar una nueva categoría a la base de datos.
    public boolean agregar(Categoria categoria) {
        String sql = "INSERT INTO Categorias (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar categoría: " + e.getMessage());
            return false;
        }
    }

    // Buscar una categoría por su nombre.
    public Categoria buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM Categorias WHERE nombre = ?";
        Categoria categoria = null;

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(
                            rs.getInt("idCategoria"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar categoría por nombre: " + e.getMessage());
        }
        return categoria;
    }

    // Actualizar una categoría existente en la base de datos.
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE Categorias SET nombre = ?, descripcion = ? WHERE idCategoria = ?";

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    // Listar todas las categorías disponibles en la base de datos.
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categorias";

        try (Connection conn = ConexionDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return categorias;
    }

    // Eliminar una categoría de la base de datos por su ID.
    public boolean eliminar(int idCategoria) {
        String sql = "DELETE FROM Categorias WHERE idCategoria = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}
