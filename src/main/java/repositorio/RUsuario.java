package repositorio;

import modelo.Usuario;
import modelo.Datos;
import modelo.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Repositorio para acceder a la tabla Usuario en la base de datos.
public class RUsuario {

    // Autentica a un usuario verificando nombre y contraseña.
    public Usuario autenticar(String correo, String contrasena) {
        try (Connection conn = ConexionDB.getConnection()) {
            String sql = "SELECT u.idUsuario, u.correo, u.contrasena, d.idDatos, d.nombre, d.apellido, d.telefono, r.idRol, r.nombre AS nombreRol " +
                    "FROM Usuarios u " +
                    "JOIN DatosPersonales d ON u.idDatos = d.idDatos " +
                    "JOIN Roles r ON u.idRol = r.idRol " +
                    "WHERE u.correo = ? AND u.contrasena = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Datos datos = new Datos(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono")
                );

                Rol rol = new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombreRol")
                );

                return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("correo"),
                        rs.getString("contrasena"),
                        datos,
                        rol
                );
            }
        } catch (Exception e) {
            System.err.println("Error autenticando usuario: " + e.getMessage());
        }
        return null;
    }

    // Busca un usuario por su correo electrónico.
    public Usuario buscarPorCorreo(String correo) {
        try (Connection conn = ConexionDB.getConnection()) {
            String sql = "SELECT u.idUsuario, u.correo, u.contrasena, d.idDatos, d.nombre, d.apellido, d.telefono, r.idRol, r.nombre AS nombreRol " +
                    "FROM Usuarios u " +
                    "JOIN DatosPersonales d ON u.idDatos = d.idDatos " +
                    "JOIN Roles r ON u.idRol = r.idRol " +
                    "WHERE u.correo = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Datos datos = new Datos(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono")
                );

                Rol rol = new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombreRol")
                );

                return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("correo"),
                        rs.getString("contrasena"),
                        datos,
                        rol
                );
            }
        } catch (Exception e) {
            System.err.println("Error buscando usuario por correo: " + e.getMessage());
        }
        return null;
    }
}
