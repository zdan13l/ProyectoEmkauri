package repositorio;

import modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RSolicitud implements IRSolicitud {

    @Override
    public int crearSolicitud(int idCliente, int idProducto, String mensaje) {
        String sql = """
            INSERT INTO Solicitudes (idSolicitante, idReclutador, estado, mensaje, idProductoAsociado, idEmprendedorAsociado)
            VALUES (?, NULL, 'PENDIENTE', ?, ?, NULL)
        """;
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, idCliente);
            st.setString(2, mensaje);
            st.setInt(3, idProducto);
            st.executeUpdate();
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    private Solicitud mapRow(ResultSet rs) throws SQLException {
        Usuario solicitante = new Usuario(
                rs.getInt("idSolicitante"),
                rs.getString("correoSol"),
                null,
                new Datos(rs.getString("nomSol"), rs.getString("apeSol"), rs.getString("telSol")),
                new Rol(rs.getInt("idRolSol"), rs.getString("rolSol"))
        );
        Usuario emprendedor = new Usuario(rs.getInt("idEmp"), null, null, null, null);
        Solicitud s = new Solicitud(
                rs.getInt("idSolicitud"),
                solicitante,
                new Usuario(), // reclutador será llenado en otro CU
                rs.getString("estado"),
                rs.getString("mensaje"),
                null, // productoAsociado -> si quieres, puedes mapear un Servicio aquí con un join extra
                emprendedor
        );
        return s;
    }

    @Override
    public List<Solicitud> listarPorSolicitante(int idCliente) {
        String sql = """
            SELECT s.idSolicitud, s.estado, s.mensaje, s.idSolicitante, s.idEmprendedorAsociado AS idEmp,
                   u.correo AS correoSol, d.nombre AS nomSol, d.apellido AS apeSol, d.telefono AS telSol,
                   r.idRol AS idRolSol, r.nombre AS rolSol
              FROM Solicitudes s
              JOIN Usuarios u ON s.idSolicitante = u.idUsuario
              JOIN DatosPersonales d ON u.idDatos = d.idDatos
              JOIN Roles r ON u.idRol = r.idRol
             WHERE s.idSolicitante = ?
             ORDER BY s.idSolicitud DESC
        """;
        List<Solicitud> out = new ArrayList<>();
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, idCliente);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return out;
    }

    @Override
    public List<Solicitud> listarPendientes() {
        String sql = """
            SELECT s.idSolicitud, s.estado, s.mensaje, s.idSolicitante, s.idEmprendedorAsociado AS idEmp,
                   u.correo AS correoSol, d.nombre AS nomSol, d.apellido AS apeSol, d.telefono AS telSol,
                   r.idRol AS idRolSol, r.nombre AS rolSol
              FROM Solicitudes s
              JOIN Usuarios u ON s.idSolicitante = u.idUsuario
              JOIN DatosPersonales d ON u.idDatos = d.idDatos
              JOIN Roles r ON u.idRol = r.idRol
             WHERE s.estado = 'PENDIENTE'
             ORDER BY s.idSolicitud DESC
        """;
        List<Solicitud> out = new ArrayList<>();
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return out;
    }
}
