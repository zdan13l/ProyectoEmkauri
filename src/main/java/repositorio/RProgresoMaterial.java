package repositorio;

import modelo.Material;
import modelo.ProgresoMaterial;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RProgresoMaterial implements IRProgresoMaterial {

    @Override
    public boolean crear(ProgresoMaterial progreso) {
        String sql = "INSERT INTO ProgresoMateriales (idCliente, idMaterial, visto) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, progreso.getIdCliente().getIdUsuario());
            ps.setInt(2, progreso.getIdMaterial().getIdMaterial());
            ps.setBoolean(3, progreso.isVisto());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) progreso.setIdProgreso(rs.getInt(1));
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al crear ProgresoMaterial: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizar(ProgresoMaterial progreso) {
        String sql = "UPDATE ProgresoMateriales SET visto=? WHERE idProgreso=?";

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setBoolean(1, progreso.isVisto());
            ps.setInt(2, progreso.getIdProgreso());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar ProgresoMaterial: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ProgresoMaterial obtenerPorClienteYMaterial(int idCliente, int idMaterial) {
        String sql = "SELECT * FROM ProgresoMateriales WHERE idCliente=? AND idMaterial=?";

        try (Connection conn = ConexionDB.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);
            ps.setInt(2, idMaterial);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ProgresoMaterial: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ProgresoMaterial> obtenerPorCliente(int idCliente) {
        List<ProgresoMaterial> lista = new ArrayList<>();

        String sql = "SELECT * FROM ProgresoMateriales WHERE idCliente=?";

        try (Connection conn = ConexionDB.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            System.err.println("Error al listar ProgresoMaterial: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public int contarVistosPorCurso(int idCliente, int idProducto) {
        String sql = "SELECT COUNT(*) AS total FROM ProgresoMateriales pm " +
                     "JOIN Materiales m ON pm.idMaterial = m.idMaterial " +
                     "WHERE pm.idCliente = ? AND m.idCurso = ? AND pm.visto = TRUE";
        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);
            ps.setInt(2, idProducto);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar materiales vistos: " + e.getMessage());
        }
        return 0;
    }

    private ProgresoMaterial mapear(ResultSet rs) throws SQLException {
        ProgresoMaterial p = new ProgresoMaterial();

        p.setIdProgreso(rs.getInt("idProgreso"));

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("idCliente"));
        p.setIdCliente(u);

        Material m = new Material();
        m.setIdMaterial(rs.getInt("idMaterial"));
        p.setIdMaterial(m);

        p.setVisto(rs.getBoolean("visto"));

        return p;
    }
}
