package repositorio;

import modelo.Usuario;
import modelo.Datos;
import modelo.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Repositorio para acceder a la tabla Usuario en la base de datos.
public class RUsuario implements IRUsuario {

    // Auténtica a un usuario verificando nombre y contraseña.
    public Usuario autenticar(String correo, String contrasena) {
        String sql = "SELECT u.idUsuario, u.correo, u.contrasena, " +
                            "d.idDatos, d.nombre, d.apellido, d.telefono, " +
                            "r.idRol, r.nombre AS nombreRol, " +
                            "s.estado AS estadoSolicitud " +
                    "FROM Usuarios u " +
                    "JOIN DatosPersonales d ON u.idDatos = d.idDatos " +
                    "JOIN Roles r ON u.idRol = r.idRol " +
                    "LEFT JOIN Solicitudes s ON u.idUsuario = s.idSolicitante " +
                    "WHERE u.correo = ? AND u.contrasena = ?";

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String rolNombre = rs.getString("nombreRol");
                String estadoSolicitud = rs.getString("estadoSolicitud");

                // Validaciones especiales para Emprendedores.
                if ("Emprendedor".equalsIgnoreCase(rolNombre)) {
                    if (estadoSolicitud == null) {
                        throw new SQLException("SIN_SOLICITUD");
                    }
                    if ("PENDIENTE".equalsIgnoreCase(estadoSolicitud)) {
                        throw new SQLException("PENDIENTE");
                    }
                    if ("RECHAZADO".equalsIgnoreCase(estadoSolicitud)) {
                        throw new SQLException("RECHAZADO");
                    }
                }

                Datos datos = new Datos(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono")
                );

                Rol rol = new Rol(
                        rs.getInt("idRol"),
                        rolNombre
                );

                return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("correo"),
                        rs.getString("contrasena"),
                        datos,
                        rol
                );
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg.equals("PENDIENTE") || msg.equals("RECHAZADO") || msg.equals("SIN_SOLICITUD")) {
                throw new RuntimeException(msg);
            }

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

    // Insertar Usuario (Cliente).
    public boolean insertarCliente(Usuario usuario) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sqlRol = "SELECT idRol FROM Roles WHERE nombre = 'Cliente'";
            PreparedStatement stmtRol = conn.prepareStatement(sqlRol);

            ResultSet rsRol = stmtRol.executeQuery();
            int idRol = 2;
            if (rsRol.next())
                idRol = rsRol.getInt("idRol");

            // Insertar datos personales.
            String sqlDatos = "INSERT INTO DatosPersonales (nombre, apellido, telefono) VALUES (?,?,?)";
            PreparedStatement stmDatos = conn.prepareStatement(sqlDatos, PreparedStatement.RETURN_GENERATED_KEYS);
            stmDatos.setString(1, usuario.getDatosPersonales().getNombre());
            stmDatos.setString(2, usuario.getDatosPersonales().getApellido());
            stmDatos.setString(3, usuario.getDatosPersonales().getTelefono());
            stmDatos.executeUpdate();

            ResultSet rs = stmDatos.getGeneratedKeys();
            int idDatos = 0;
            if (rs.next())
                idDatos = rs.getInt(1);

            // Insertar Cliente.
            String sqlUsuario = "INSERT INTO Usuarios (correo, contrasena, idDatos, idRol) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario);
            stmtUsuario.setString(1, usuario.getCorreo());
            stmtUsuario.setString(2, usuario.getContrasena());
            stmtUsuario.setInt(3, idDatos);
            stmtUsuario.setInt(4, idRol);
            stmtUsuario.executeUpdate();

            conn.commit();
            System.out.println("Cliente insertado correctamente.");
            return true;

        } catch (Exception e) {
            System.err.println("Error insertando cliente: " + e.getMessage());
            return false;
        }
    }

    // Insertar Emprendedor y crear solicitud para aprobación del reclutador.
    public boolean insertarEmprendedor(Usuario usuario, String mensajeSolicitud) {
        String sqlDatos = "INSERT INTO DatosPersonales (nombre, apellido, telefono) VALUES (?, ?, ?)";
        String sqlUsuario = "INSERT INTO Usuarios (correo, contrasena, idDatos, idRol) VALUES (?, ?, ?, 1)";
        String sqlBuscarReclutador = "SELECT idUsuario FROM Usuarios WHERE idRol = 3 LIMIT 1";
        String sqlSolicitud = "INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idEmprendedorAsociado) VALUES (?, ?, 'PENDIENTE', ?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);

            // Insertar DatosPersonales.
            int idDatos;
            try (PreparedStatement psDatos = conn.prepareStatement(sqlDatos, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psDatos.setString(1, usuario.getDatosPersonales().getNombre());
                psDatos.setString(2, usuario.getDatosPersonales().getApellido());
                psDatos.setString(3, usuario.getDatosPersonales().getTelefono());
                psDatos.executeUpdate();

                try (ResultSet rs = psDatos.getGeneratedKeys()) {
                    if (rs.next()) idDatos = rs.getInt(1);
                    else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Insertar Usuario (Emprendedor).
            int idUsuario;
            try (PreparedStatement psUsuario = conn.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psUsuario.setString(1, usuario.getCorreo());
                psUsuario.setString(2, usuario.getContrasena());
                psUsuario.setInt(3, idDatos);
                psUsuario.executeUpdate();

                try (ResultSet rs = psUsuario.getGeneratedKeys()) {
                    if (rs.next()) idUsuario = rs.getInt(1);
                    else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Buscar reclutador, cualquiera disponible.
            Integer idReclutador = null;
            try (PreparedStatement psReclutador = conn.prepareStatement(sqlBuscarReclutador)) {
                ResultSet rs = psReclutador.executeQuery();
                if (rs.next()) {
                    idReclutador = rs.getInt("idUsuario");
                }
            }

            // Si no hay reclutador, no se puede crear la solicitud.
            if (idReclutador == null) {
                conn.rollback();
                System.err.println("No existe reclutador, no se puede crear la solicitud.");
                return false;
            }

            // Insertar Solicitud asociando el reclutador encontrado y el emprendedor recién creado.
            try (PreparedStatement psSolicitud = conn.prepareStatement(sqlSolicitud)) {
                psSolicitud.setInt(1, idUsuario);
                psSolicitud.setInt(2, idReclutador);
                psSolicitud.setString(3, mensajeSolicitud);
                psSolicitud.setInt(4, idUsuario);
                psSolicitud.executeUpdate();
            }

            conn.commit();
            System.out.println("Emprendedor insertado y solicitud creada con reclutador asociado.");
            return true;

        } catch (Exception e) {
            System.err.println("Error insertando emprendedor: " + e.getMessage());
            return false;
        }
    }
}

