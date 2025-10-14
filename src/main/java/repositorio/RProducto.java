package repositorio;

import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RProducto implements IRProducto {

    // Agregar un nuevo producto a la base de datos.
    public boolean agregar(Producto producto) {
        String sql = "INSERT INTO Productos (titulo, descripcion, precio, idEmprendedor, idCategoria, tipoProducto, " +
                "duracionCurso, nivelDificultad, certificacion, duracionServicio, ubicacion, modalidad) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getTitulo());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getEmprendedor().getIdUsuario());
            ps.setInt(5, producto.getCategoria().getIdCategoria());

            // 🔹 Diferenciar entre curso y servicio
            if (producto instanceof Curso curso) {
                ps.setString(6, "CURSO");
                ps.setInt(7, curso.getDuracionCurso());
                ps.setString(8, curso.getNivelDificultad());
                ps.setString(9, curso.getCertificacion());
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR);
            } else if (producto instanceof Servicio servicio) {
                ps.setString(6, "SERVICIO");
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
                ps.setInt(10, servicio.getDuracionServicio());
                ps.setString(11, servicio.getUbicacion());
                ps.setString(12, servicio.getModalidad());
            } else {
                throw new SQLException("Tipo de producto desconocido");
            }

            int filasAfectadas = ps.executeUpdate();

            // 🔹 Recuperar el ID generado automáticamente
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    producto.setIdProducto(idGenerado);
                }
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Listar todos los productos aprobados
    @Override
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT p.idProducto, p.titulo, p.descripcion, p.precio, p.tipoProducto, " +
                            " u.idUsuario, u.correo, " +
                            "c.idCategoria, c.nombre AS nombreCategoria, c.descripcion AS descCategoria, " +
                            "p.duracionCurso, p.nivelDificultad, p.certificacion, " +
                            "p.duracionServicio, p.ubicacion, p.modalidad " +
                        "FROM Productos p " +
                        "JOIN Usuarios u ON p.idEmprendedor = u.idUsuario " +
                        "JOIN Categorias c ON p.idCategoria = c.idCategoria " +
                        "JOIN Solicitudes s ON p.idProducto = s.idProductoAsociado " +
                        "WHERE s.estado = 'APROBADO'";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = mapearProducto(rs);
                if (p != null) productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println(" Error al listar productos aprobados: " + e.getMessage());
        }
        return productos;
    }

    // Listar productos comprados por un cliente específico.
    public List<Producto> listarComprados(int idCliente) {
        List<Producto> productos = new ArrayList<>();

        String sql = """
        SELECT p.idProducto, p.titulo, p.descripcion, p.precio, p.tipoProducto,
               u.idUsuario, u.correo,
               c.idCategoria, c.nombre AS nombreCategoria, c.descripcion AS descCategoria,
               p.duracionCurso, p.nivelDificultad, p.certificacion,
               p.duracionServicio, p.ubicacion, p.modalidad
        FROM Compras co
        JOIN ComprasProductos cp ON co.idCompra = cp.idCompra
        JOIN Productos p ON cp.idProducto = p.idProducto
        JOIN Usuarios u ON p.idEmprendedor = u.idUsuario
        LEFT JOIN Categorias c ON p.idCategoria = c.idCategoria
        WHERE co.idCliente = ?
    """;

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = mapearProducto(rs);
                    if (p != null) productos.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar productos comprados: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }


    public List<Producto> listarPorEmprendedor(int idEmprendedor) {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT p.idProducto, p.titulo, p.descripcion, p.precio, p.tipoProducto, " +
                " u.idUsuario, u.correo, " +
                " c.idCategoria, c.nombre AS nombreCategoria, c.descripcion AS descCategoria, " +
                " p.duracionCurso, p.nivelDificultad, p.certificacion, " +
                " p.duracionServicio, p.ubicacion, p.modalidad " +
                "FROM Productos p " +
                "JOIN Usuarios u ON p.idEmprendedor = u.idUsuario " +
                "LEFT JOIN Categorias c ON p.idCategoria = c.idCategoria " +
                "WHERE p.idEmprendedor = ?";

        try (Connection conexion = ConexionDB.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEmprendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = mapearProducto(rs);
                    if (p != null) productos.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al listar productos por emprendedor: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // Mapear un ResultSet a un objeto Producto (o sus subclases).
    public Producto mapearProducto(ResultSet rs) {
        try {
            // Emprendedor.
            Usuario emprendedor = new Usuario();
            emprendedor.setIdUsuario(rs.getInt("idUsuario"));
            emprendedor.setCorreo(rs.getString("correo"));

            // Categoría.
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(rs.getInt("idCategoria"));
            categoria.setNombre(rs.getString("nombreCategoria"));
            categoria.setDescripcion(rs.getString("descCategoria"));

            // Tipo de producto.
            String tipo = rs.getString("tipoProducto");
            Producto producto;

            if ("CURSO".equalsIgnoreCase(tipo)) {
                Curso curso = new Curso();
                curso.setDuracionCurso(rs.getInt("duracionCurso"));
                curso.setNivelDificultad(rs.getString("nivelDificultad"));
                curso.setCertificacion(rs.getString("certificacion"));
                producto = curso;

            } else if ("SERVICIO".equalsIgnoreCase(tipo)) {
                Servicio servicio = new Servicio();
                servicio.setDuracionServicio(rs.getInt("duracionServicio"));
                servicio.setUbicacion(rs.getString("ubicacion"));
                servicio.setModalidad(rs.getString("modalidad"));
                producto = servicio;

            } else {
                producto = new Producto();
            }

            // Campos comunes.
            producto.setIdProducto(rs.getInt("idProducto"));
            producto.setTitulo(rs.getString("titulo"));
            producto.setDescripcion(rs.getString("descripcion"));
            producto.setPrecio(rs.getDouble("precio"));
            producto.setEmprendedor(emprendedor);
            producto.setCategoria(categoria);

            return producto;

        } catch (SQLException e) {
            System.err.println(" Error al mapear producto: " + e.getMessage());
            return null;
        }
    }

    // Buscar un producto por su ID.
    public Producto buscarPorId(int idProducto) {
        String sql = " SELECT p.idProducto, p.titulo, p.descripcion, p.precio, p.tipoProducto," +
                            "u.idUsuario, u.correo," +
                            "c.idCategoria, c.nombre AS nombreCategoria, c.descripcion AS descCategoria," +
                            "p.duracionCurso, p.nivelDificultad, p.certificacion," +
                            "p.duracionServicio, p.ubicacion, p.modalidad " +
                        "FROM Productos p " +
                        "JOIN Usuarios u ON p.idEmprendedor = u.idUsuario " +
                        "JOIN Categorias c ON p.idCategoria = c.idCategoria " +
                        "WHERE p.idProducto = ?";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapearProducto(rs);

        } catch (SQLException e) {
            System.err.println(" Error al buscar producto: " + e.getMessage());
        }

        return null;
    }

    // Buscar productos por título.
    public List<Producto> buscarPorTitulo(String titulo) {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT p.idProducto, p.titulo, p.descripcion, p.precio, p.tipoProducto," +
                            "u.idUsuario, u.correo," +
                            "c.idCategoria, c.nombre AS nombreCategoria, c.descripcion AS descCategoria," +
                            "p.duracionCurso, p.nivelDificultad, p.certificacion," +
                            "p.duracionServicio, p.ubicacion, p.modalidad " +
                        "FROM Productos p " +
                        "JOIN Usuarios u ON p.idEmprendedor = u.idUsuario " +
                        "JOIN Categorias c ON p.idCategoria = c.idCategoria " +
                        "WHERE LOWER(p.titulo) LIKE ?";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, "%" + titulo.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producto p = mapearProducto(rs);
                if (p != null) productos.add(p);
            }

        } catch (SQLException e) {
            System.err.println(" Error al buscar por título: " + e.getMessage());
        }

        return productos;
    }

    // Actualizar un producto existente en la base de datos.
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Productos SET titulo = ?, descripcion = ?, precio = ?, idCategoria = ? WHERE idProducto = ?";
        try ( Connection conexion = ConexionDB.getConnection()) {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, producto.getTitulo());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getCategoria().getIdCategoria());
            ps.setInt(5, producto.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // Eliminar un producto de la base de datos por su ID.
    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM Productos WHERE idProducto = ?";
        try ( Connection conexion = ConexionDB.getConnection()) {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(" Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}
