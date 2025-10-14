package repositorio;

import modelo.Solicitud;
import modelo.Usuario;
import modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RSolicitud implements IRSolicitud {

    // -------------------------------------------------------------------------
    // GUARDAR SOLICITUD
    // -------------------------------------------------------------------------
    @Override
    public void guardar(Solicitud solicitud) {
        String sql = "INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idProductoAsociado, idEmprendedorAsociado) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, solicitud.getSolicitante().getIdUsuario());

            Integer idReclutador = null;
            if (solicitud.getReclutador() != null && solicitud.getReclutador().getIdUsuario() != 0) {
                idReclutador = solicitud.getReclutador().getIdUsuario();
            } else {
                // Buscar reclutador por defecto en la base de datos
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

    // -------------------------------------------------------------------------
    // LISTAR SOLICITUDES PENDIENTES
    // -------------------------------------------------------------------------
    @Override
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

    // -------------------------------------------------------------------------
    // APROBAR SOLICITUD
    // -------------------------------------------------------------------------
    @Override
    public boolean aprobar(int idSolicitud) {
        actualizarEstado(idSolicitud, "APROBADO");
        return true;
    }

    // -------------------------------------------------------------------------
    // RECHAZAR SOLICITUD
    // -------------------------------------------------------------------------
    @Override
    public boolean rechazar(int idSolicitud) {
        actualizarEstado(idSolicitud, "RECHAZADO");
        return true;
    }

    // -------------------------------------------------------------------------
    // MÉTODO PRIVADO: ACTUALIZAR ESTADO
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // MAPEAR RESULTSET A OBJETO Solicitud
    // -------------------------------------------------------------------------
    private Solicitud mapearSolicitud(ResultSet rs) throws SQLException {
        Solicitud solicitud = new Solicitud();

        solicitud.setIdSolicitud(rs.getInt("idSolicitud"));
        solicitud.setEstado(rs.getString("estado"));
        solicitud.setMensaje(rs.getString("mensaje"));

        // Crear y asignar solicitante
        Usuario solicitante = new Usuario();
        solicitante.setIdUsuario(rs.getInt("idSolicitante"));
        solicitud.setSolicitante(solicitante);

        // Crear y asignar reclutador
        int idReclutador = rs.getInt("idReclutador");
        if (!rs.wasNull()) {
            Usuario reclutador = new Usuario();
            reclutador.setIdUsuario(idReclutador);
            solicitud.setReclutador(reclutador);
        }

        // Producto asociado
        int idProducto = rs.getInt("idProductoAsociado");
        if (!rs.wasNull()) {
            Producto producto = new Producto();
            producto.setIdProducto(idProducto);
            solicitud.setProductoAsociado(producto);
        }

        // Emprendedor asociado
        int idEmprendedor = rs.getInt("idEmprendedorAsociado");
        if (!rs.wasNull()) {
            Usuario emprendedor = new Usuario();
            emprendedor.setIdUsuario(idEmprendedor);
            solicitud.setEmprendedor(emprendedor);
        }

        return solicitud;
    }

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

}
