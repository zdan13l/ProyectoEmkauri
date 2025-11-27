package repositorio;

import modelo.Material;
import java.sql.*;
import java.util.*;

// Repositorio para gestionar las operaciones CRUD de los materiales en la base de datos.
public class RMaterial implements IRMaterial {

    // Inserta un nuevo material asociado a un curso específico.
    public void insertar(Material material, int idCurso) {
        String sql = "INSERT INTO Materiales (titulo, tipo, url, idCurso) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, material.getTitulo());
            ps.setString(2, material.getTipo());
            ps.setString(3, material.getUrl());
            ps.setInt(4, idCurso);
            ps.executeUpdate();

            // Obtener el ID generado y asignarlo al material.
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                material.setIdMaterial(rs.getInt(1));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Lista todos los materiales asociados a un curso específico.
    public List<Material> listarPorCurso(int idCurso) {
        List<Material> lista = new ArrayList<>();
        String sql = "SELECT idMaterial, titulo, tipo, url FROM Materiales WHERE idCurso = ?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Material m = new Material(
                        rs.getInt("idMaterial"),
                        rs.getString("titulo"),
                        rs.getString("tipo"),
                        rs.getString("url")
                );
                lista.add(m);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    // Busca un material por su ID.
    public Material buscarPorId(int idMaterial) {
        String sql = "SELECT idMaterial, titulo, tipo, url FROM Materiales WHERE idMaterial = ?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMaterial);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Material(
                        rs.getInt("idMaterial"),
                        rs.getString("titulo"),
                        rs.getString("tipo"),
                        rs.getString("url")
                );
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    // Actualiza la información de un material existente.
    public void actualizar(Material material) {
        String sql = "UPDATE Materiales SET titulo = ?, tipo = ?, url = ? WHERE idMaterial = ?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, material.getTitulo());
            ps.setString(2, material.getTipo());
            ps.setString(3, material.getUrl());
            ps.setInt(4, material.getIdMaterial());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Elimina un material por su ID.
    public void eliminar(int idMaterial) {
        String sql = "DELETE FROM Materiales WHERE idMaterial = ?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMaterial);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
