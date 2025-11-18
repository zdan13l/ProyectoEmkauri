package repositorio;

import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Repositorio para gestionar las solicitudes en la base de datos.
public class RSolicitud implements IRSolicitud {

    // Guardar una nueva solicitud en la base de datos.
    public void guardar(Solicitud solicitud) {
        String sql = "INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idProductoAsociado, idEmprendedorAsociado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, solicitud.getSolicitante().getIdUsuario());

            Integer idReclutador = null;
            if (solicitud.getReclutador() != null && solicitud.getReclutador().getIdUsuario() != 0) {
                idReclutador = solicitud.getReclutador().getIdUsuario();
            } else {
                // Buscar reclutador por defecto en la base de datos.
                idReclutador = obtenerReclutador(conexion);
            }

            if (idReclutador != null) {
                ps.setInt(2, idReclutador);
            } else {
                ps.setNull(2, Types.INTEGER);
            }


            ps.setString(3, solicitud.getEstado());
            ps.setString(4, solicitud.getMensaje());

            if (solicitud.getProductoAsociado() != null && solicitud.getProductoAsociado().getIdProducto() > 0) {
                ps.setInt(5, solicitud.getProductoAsociado().getIdProducto());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (solicitud.getEmprendedor() != null && solicitud.getEmprendedor().getIdUsuario() > 0) {
                ps.setInt(6, solicitud.getEmprendedor().getIdUsuario());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar la solicitud: " + e.getMessage());
        }
    }

    // Listar todas las solicitudes pendientes.
    public List<Solicitud> listarPendientesTipo(String tipo) {
        List<Solicitud> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM Solicitudes WHERE estado = 'PENDIENTE'";

        if (tipo.equalsIgnoreCase("PRODUCTO")) {
            sql += " AND idProductoAsociado IS NOT NULL AND idEmprendedorAsociado IS NOT NULL";
        } else if (tipo.equalsIgnoreCase("EMPRENDEDOR")) {
            sql += " AND idProductoAsociado IS NULL AND idEmprendedorAsociado IS NOT NULL";
        }

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Solicitud solicitud = mapearSolicitud(rs);
                solicitudes.add(solicitud);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar las solicitudes pendientes: " + e.getMessage());
        }

        return solicitudes;
    }

    // Listar todas las solicitudes de un tipo específico sin incluir las pendientes.
    public List<Solicitud> listarSolicitudes(String tipo) {
        List<Solicitud> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM Solicitudes WHERE estado != 'PENDIENTE'";

        if (tipo.equalsIgnoreCase("PRODUCTO")) {
            sql += " AND idProductoAsociado IS NOT NULL AND idEmprendedorAsociado IS NOT NULL";
        } else if (tipo.equalsIgnoreCase("EMPRENDEDOR")) {
            sql += " AND idProductoAsociado IS NULL AND idEmprendedorAsociado IS NOT NULL";
        }

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Solicitud solicitud = mapearSolicitud(rs);
                solicitudes.add(solicitud);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar las solicitudes: " + e.getMessage());
        }

        return solicitudes;
    }

    // Aprobar una solicitud por su ID.
    public boolean aprobar(int idSolicitud) {
        actualizarEstado(idSolicitud, "APROBADO");
        return true;
    }

    // Rechazar una solicitud por su ID.
    public boolean rechazar(int idSolicitud) {
        actualizarEstado(idSolicitud, "RECHAZADO");
        return true;
    }

    // Actualizar el estado de una solicitud.
    private void actualizarEstado(int idSolicitud, String nuevoEstado) {
        String sql = "UPDATE Solicitudes SET estado = ? WHERE idSolicitud = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idSolicitud);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado de la solicitud: " + e.getMessage());
        }
    }

    // Mapear un ResultSet a un objeto Solicitud.
    private Solicitud mapearSolicitud(ResultSet rs) throws SQLException {
        Solicitud solicitud = new Solicitud();

        solicitud.setIdSolicitud(rs.getInt("idSolicitud"));
        solicitud.setEstado(rs.getString("estado"));
        solicitud.setMensaje(rs.getString("mensaje"));

        // ----------- SOLICITANTE -----------
        int idSolicitante = rs.getInt("idSolicitante");
        solicitud.setSolicitante(cargarUsuario(idSolicitante));

        // ----------- RECLUTADOR ------------
        int idReclutador = rs.getInt("idReclutador");
        if (!rs.wasNull()) {
            solicitud.setReclutador(cargarUsuario(idReclutador));
        }

        // ----------- EMPRENDEDOR ----------
        int idEmprendedor = rs.getInt("idEmprendedorAsociado");
        if (!rs.wasNull()) {
            solicitud.setEmprendedor(cargarUsuario(idEmprendedor));
        }

        // ----------- PRODUCTO --------------
        int idProducto = rs.getInt("idProductoAsociado");
        if (!rs.wasNull()) {
            solicitud.setProductoAsociado(cargarProducto(idProducto));
        }

        return solicitud;
    }

    private Usuario cargarUsuario(int idUsuario) {
        String sql = "SELECT u.*, d.nombre, d.apellido, d.telefono, r.nombre AS rolNombre " +
                "FROM Usuarios u " +
                "JOIN DatosPersonales d ON u.idDatos = d.idDatos " +
                "JOIN Roles r ON u.idRol = r.idRol " +
                "WHERE u.idUsuario = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(idUsuario);
                u.setCorreo(rs.getString("correo"));

                Datos dp = new Datos();
                dp.setNombre(rs.getString("nombre"));
                dp.setApellido(rs.getString("apellido"));
                dp.setTelefono(rs.getString("telefono"));
                u.setDatosPersonales(dp);

                modelo.Rol rol = new modelo.Rol();
                rol.setNombre(rs.getString("rolNombre"));
                u.setRol(rol);

                return u;
            }

        } catch (SQLException e) {
            System.out.println("Error cargando usuario: " + e.getMessage());
        }

        return null;
    }


    private Producto cargarProducto(int idProducto) {
        String sql = "SELECT p.*, c.nombre AS categoriaNombre " +
                "FROM Productos p " +
                "JOIN Categorias c ON p.idCategoria = c.idCategoria " +
                "WHERE p.idProducto = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // Construir categoría
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("idCategoria"));
                categoria.setNombre(rs.getString("categoriaNombre"));

                String tipo = rs.getString("tipoProducto");

                Producto p;

                if ("CURSO".equalsIgnoreCase(tipo)) {
                    Curso curso = new Curso();
                    curso.setDuracionCurso(rs.getInt("duracionCurso"));
                    curso.setNivelDificultad(rs.getString("nivelDificultad"));
                    curso.setCertificacion(rs.getString("certificacion"));
                    p = curso;

                } else if ("SERVICIO".equalsIgnoreCase(tipo)) {
                    Servicio s = new Servicio();
                    s.setDuracionServicio(rs.getInt("duracionServicio"));
                    s.setUbicacion(rs.getString("ubicacion"));
                    s.setModalidad(rs.getString("modalidad"));
                    p = s;

                } else {
                    p = new Producto(); // fallback
                }

                // Asignar los atributos base
                p.setIdProducto(rs.getInt("idProducto"));
                p.setTitulo(rs.getString("titulo"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setCategoria(categoria);

                return p;
            }

        } catch (Exception e) {
            System.out.println("Error cargando producto: " + e.getMessage());
        }

        return null;
    }


    // Obtener un reclutador por defecto de la base de datos.
    public Integer obtenerReclutador(Connection conexion) {
        String sql = "SELECT idUsuario FROM Usuarios WHERE idRol = " +
                "(SELECT idRol FROM Roles WHERE nombre = 'Reclutador') LIMIT 1";

        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("idUsuario");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Marcar pendiente
    public void marcarPendiente(int idSolicitud) {
        actualizarEstado(idSolicitud, "PENDIENTE");
    }
}
