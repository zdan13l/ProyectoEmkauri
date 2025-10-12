package repositorio;

import modelo.Usuario;
import modelo.Datos;
import modelo.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Repositorio para acceder a la tabla Usuario en la base de datos.
public class RUsuario implements IRUsuario {

    // Auténtica a un usuario verificando nombre y contraseña.
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

    //Insertar Usuario (Cliente)
    public boolean insertarCliente(Usuario usuario){
        Connection conn = null;
        try{
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            //insertar datos personales
            String sqlDatos = "INSERT INTO DatosPersonales (nombre, apellido, telefono) VALUES (?,?,?)";
            PreparedStatement stmDatos = conn.prepareStatement(sqlDatos, PreparedStatement.RETURN_GENERATED_KEYS);
            stmDatos.setString(1, usuario.getDatosPersonales().getNombre());
            stmDatos.setString(2, usuario.getDatosPersonales().getApellido());
            stmDatos.setString(3, usuario.getDatosPersonales().getTelefono());
            stmDatos.executeUpdate();

            ResultSet rs = stmDatos.getGeneratedKeys();
            int idDatos = 0;
            if (rs.next()) idDatos = rs.getInt(1);

            //Insertar Cliente (idRol = 2)
            String sqlUsuario = "INSERT INTO Usuarios (correo, contrasena, idDatos, idRol) VALUES (?, ?, ?, 2)";
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario);
            stmtUsuario.setString(1, usuario.getCorreo());
            stmtUsuario.setString(2, usuario.getContrasena());
            stmtUsuario.setInt(3, idDatos);
            stmtUsuario.executeUpdate();

            conn.commit();
            System.out.println("Cliente insertado correctamente.");
            return true;

        } catch (Exception e) {
            System.err.println("Error insertando cliente: " + e.getMessage());
            return false;
        }
    }

    // Insertar Emprendedor
    public boolean insertarEmprendedor(Usuario usuario, String mensajeSolicitud) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // Insertar datos personales
            String sqlDatos = "INSERT INTO DatosPersonales (nombre, apellido, telefono) VALUES (?, ?, ?)";
            PreparedStatement stmtDatos = conn.prepareStatement(sqlDatos, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtDatos.setString(1, usuario.getDatosPersonales().getNombre());
            stmtDatos.setString(2, usuario.getDatosPersonales().getApellido());
            stmtDatos.setString(3, usuario.getDatosPersonales().getTelefono());
            stmtDatos.executeUpdate();

            ResultSet rs = stmtDatos.getGeneratedKeys();
            int idDatos = 0;
            if (rs.next()) idDatos = rs.getInt(1);

            // Insertar Emprendedor (idRol = 1)
            String sqlUsuario = "INSERT INTO Usuarios (correo, contrasena, idDatos, idRol) VALUES (?, ?, ?, 1)";
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtUsuario.setString(1, usuario.getCorreo());
            stmtUsuario.setString(2, usuario.getContrasena());
            stmtUsuario.setInt(3, idDatos);
            stmtUsuario.executeUpdate();

            rs = stmtUsuario.getGeneratedKeys();
            int idUsuario = 0;
            if (rs.next()) idUsuario = rs.getInt(1);

            // Crear solicitud para aprobación del reclutador
            String sqlSolicitud = "INSERT INTO Solicitudes (idSolicitante, estado, mensaje, idEmprendedorAsociado) VALUES (?, 'PENDIENTE', ?, ?)";
            PreparedStatement stmtSolicitud = conn.prepareStatement(sqlSolicitud);
            stmtSolicitud.setInt(1, idUsuario);
            stmtSolicitud.setString(2, mensajeSolicitud);
            stmtSolicitud.setInt(3, idUsuario);
            stmtSolicitud.executeUpdate();

            conn.commit();
            System.out.println("Emprendedor insertado y solicitud creada correctamente.");
            return true;

        } catch (Exception e) {
            System.err.println("Error insertando emprendedor: " + e.getMessage());
            return false;
        }
    }
}

