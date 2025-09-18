package repositorio;

import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Repositorio para acceder a la tabla Usuario en la base de datos.
public class RUsuario {

    // Autentica a un usuario verificando nombre y contraseña.
    public Usuario autenticar(String nombre, String contrasena) {
        try (Connection conn = ConexionDB.getConnection()) {
            String sql = "SELECT * FROM Usuario WHERE nombre = ? AND contrasena = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("contrasena")
                );
            }
        } catch (Exception e) {
            System.err.println("Error autenticando usuario: " + e.getMessage());
        }
        return null;
    }

    // Busca un usuario por su nombre.
    public Usuario buscarPorNombre(String nombre) {
        try (Connection conn = ConexionDB.getConnection()) {
            String sql = "SELECT * FROM Usuario WHERE nombre = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("contrasena")
                );
            }
        } catch (Exception e) {
            System.err.println("Error buscando usuario: " + e.getMessage());
        }
        return null;
    }

    // Obtiene el rol de un usuario verificando en las tablas Cliente, Emprendedor o Reclutador.
    public String obtenerRol(int idUsuario) {
        try (Connection conn = ConexionDB.getConnection()) {
            if (estaEnTabla(conn, "Cliente", idUsuario))
                return "Cliente";
            if (estaEnTabla(conn, "Emprendedor", idUsuario))
                return "Emprendedor";
            if (estaEnTabla(conn, "Reclutador", idUsuario))
                return "Reclutador";
        } catch (Exception e) {
            System.err.println("Error obteniendo rol: " + e.getMessage());
        }
        return "Desconocido";
    }

    // Metodo auxiliar para verificar si el usuario está en una tabla (Cliente, Emprendedor, Reclutador).
    private boolean estaEnTabla(Connection conn, String tabla, int idUsuario) throws Exception {
        String sql = "SELECT 1 FROM " + tabla + " WHERE idUsuario = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}
