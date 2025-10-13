package repositorio;

import modelo.Datos;
import modelo.Rol;
import modelo.Servicio;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RServicio implements IRServicio {

    private Servicio mapRow(ResultSet rs) throws SQLException {
        Usuario emprendedor = new Usuario(
                rs.getInt("idEmprendedor"),
                rs.getString("correoEmp"),
                null,
                new Datos(rs.getString("nomEmp"), rs.getString("apeEmp"), rs.getString("telEmp")),
                new Rol(rs.getInt("idRolEmp"), rs.getString("rolEmp"))
        );
        Servicio s = new Servicio(
                rs.getInt("idProducto"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                emprendedor,
                rs.getInt("duracionServicio"),
                rs.getString("ubicacion"),
                rs.getString("modalidad")
        );
        return s;
    }

    @Override
    public List<Servicio> listar() {
        String sql = """
            SELECT p.idProducto, p.titulo, p.descripcion, p.precio,
                   p.idEmprendedor, p.duracionServicio, p.ubicacion, p.modalidad,
                   u.correo AS correoEmp, d.nombre AS nomEmp, d.apellido AS apeEmp, d.telefono AS telEmp,
                   r.idRol AS idRolEmp, r.nombre AS rolEmp
            FROM Productos p
               JOIN Usuarios u ON p.idEmprendedor = u.idUsuario
               JOIN DatosPersonales d ON u.idDatos = d.idDatos
               JOIN Roles r ON u.idRol = r.idRol
            WHERE p.tipoProducto = 'SERVICIO'
            ORDER BY p.idProducto DESC
        """;
        List<Servicio> out = new ArrayList<>();
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public Servicio buscarPorId(int id) {
        String sql = """
            SELECT p.idProducto, p.titulo, p.descripcion, p.precio,
                   p.idEmprendedor, p.duracionServicio, p.ubicacion, p.modalidad,
                   u.correo AS correoEmp, d.nombre AS nomEmp, d.apellido AS apeEmp, d.telefono AS telEmp,
                   r.idRol AS idRolEmp, r.nombre AS rolEmp
            FROM Productos p
               JOIN Usuarios u ON p.idEmprendedor = u.idUsuario
               JOIN DatosPersonales d ON u.idDatos = d.idDatos
               JOIN Roles r ON u.idRol = r.idRol
            WHERE p.tipoProducto='SERVICIO' AND p.idProducto=?
        """;
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public int crear(Servicio s) {
        String sql = """
          INSERT INTO Productos (titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto,
                                  duracionServicio, ubicacion, modalidad)
          VALUES (?, ?, ?, ?, ?, 'SERVICIO', ?, ?, ?)
        """;
        // OJO: si decides modelar categoría en el modelo, pásala aquí; por ahora usa una categoría por defecto 1.
        int idGenerado = -1;
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, s.getTitulo());
            st.setString(2, s.getDescripcion());
            st.setDouble(3, s.getPrecio());
            st.setInt(4, s.getEmprendedor().getIdUsuario());
            st.setInt(5, 1); // idCategoria placeholder
            st.setInt(6, s.getDuracion());
            st.setString(7, s.getUbicacion());
            st.setString(8, s.getModalidad());
            st.executeUpdate();
            try (ResultSet keys = st.getGeneratedKeys()) {
                if (keys.next()) idGenerado = keys.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return idGenerado;
    }

    @Override
    public boolean actualizar(Servicio s) {
        String sql = """
          UPDATE Productos
             SET titulo=?, descripcion=?, precio=?, idEmprendedor=?, idCategoria=?,
                 duracionServicio=?, ubicacion=?, modalidad=?
           WHERE idProducto=? AND tipoProducto='SERVICIO'
        """;
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, s.getTitulo());
            st.setString(2, s.getDescripcion());
            st.setDouble(3, s.getPrecio());
            st.setInt(4, s.getEmprendedor().getIdUsuario());
            st.setInt(5, 1); // idCategoria placeholder
            st.setInt(6, s.getDuracion());
            st.setString(7, s.getUbicacion());
            st.setString(8, s.getModalidad());
            st.setInt(9, s.getIdProducto());
            return st.executeUpdate() == 1;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Productos WHERE idProducto=? AND tipoProducto='SERVICIO'";
        try (Connection c = ConexionDB.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() == 1;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
