package repositorio;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class RCurso {

    private Connection getConnection() throws SQLException {
        return ConexionDB.getConnection(); // <- AJUSTA el paquete/nombre si difiere
    }

    public RCurso() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS CURSO(
              ID_CURSO IDENTITY PRIMARY KEY,
              NOMBRE VARCHAR(150) NOT NULL UNIQUE,
              DESCRIPCION VARCHAR(1000),
              PRECIO DOUBLE NOT NULL,
              ESTADO VARCHAR(30) NOT NULL
            );
            """;
        try (Connection cn = getConnection(); Statement st = cn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando tabla CURSO", e);
        }
    }

    public boolean existePorNombre(String nombre) {
        String sql = "SELECT 1 FROM CURSO WHERE UPPER(NOMBRE)=UPPER(?)";
        try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verificando nombre de curso", e);
        }
    }

    public Curso guardar(Curso c) {
        String sql = "INSERT INTO CURSO (NOMBRE, DESCRIPCION, PRECIO, ESTADO) VALUES (?,?,?,?)";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setDouble(3, c.getPrecio());
            ps.setString(4, c.getEstado());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = c.getIdCurso();
                if (keys.next()) id = keys.getInt(1);
                // Reconstruimos porque el modelo no tiene setters
                return new Curso(
                    id,
                    c.getPrecio(),
                    c.getNombre(),
                    c.getDescripcion(),
                    c.getEstado(),
                    c.getCategoria(),
                    c.getEmprendedor(),
                    c.getCalificaciones()
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando curso", e);
        }
    }

    public Optional<Curso> buscarPorId(int id) {
        String sql = "SELECT * FROM CURSO WHERE ID_CURSO=?";
        try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando curso por id", e);
        }
    }

    public List<Curso> listar() {
        String sql = "SELECT * FROM CURSO ORDER BY ID_CURSO DESC";
        List<Curso> out = new ArrayList<>();
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error listando cursos", e);
        }
    }

    public boolean actualizar(Curso c) {
        String sql = "UPDATE CURSO SET NOMBRE=?, DESCRIPCION=?, PRECIO=?, ESTADO=? WHERE ID_CURSO=?";
        try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setDouble(3, c.getPrecio());
            ps.setString(4, c.getEstado());
            ps.setInt(5, c.getIdCurso());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando curso", e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM CURSO WHERE ID_CURSO=?";
        try (Connection cn = getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando curso", e);
        }
    }

    private Curso map(ResultSet rs) throws SQLException {
        // Relaciones (categoria, emprendedor) y calificaciones quedan null/Lista vacía
        return new Curso(
            rs.getInt("ID_CURSO"),
            rs.getDouble("PRECIO"), 
            rs.getString("NOMBRE"),
            rs.getString("DESCRIPCION"),
            rs.getString("ESTADO"),
            null, // Categoria
            null, // Emprendedor
            java.util.List.of() // Calificaciones
        );
    }
}