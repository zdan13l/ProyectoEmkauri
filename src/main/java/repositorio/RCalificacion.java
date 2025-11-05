package repositorio;

import modelo.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Repositorio para manejar operaciones CRUD de la entidad Calificacion.
public class RCalificacion implements IRCalificacion{
    /**
     * Guarda una nueva calificación en la base de datos.
     * Impide duplicados: un cliente no puede calificar dos veces el mismo producto.
     */
    public boolean guardar(Calificacion calificacion) {
        String sql = """
                INSERT INTO Calificaciones (puntaje, comentario, fecha, idCliente, idProducto)
                VALUES (?, ?, CURRENT_DATE, ?, ?)
                """;

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, calificacion.getPuntaje());
            ps.setString(2, calificacion.getComentario());
            ps.setInt(3, calificacion.getCliente().getIdUsuario());
            ps.setInt(4, calificacion.getProducto().getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar calificación: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las calificaciones asociadas a un producto específico.
     */
    public List<Calificacion> obtenerPorProducto(int idProducto) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = """
                SELECT c.idCalificacion, c.puntaje, c.comentario, c.fecha,
                       u.idUsuario, u.correo,
                       d.nombre, d.apellido, d.telefono
                FROM Calificaciones c
                INNER JOIN Usuarios u ON c.idCliente = u.idUsuario
                INNER JOIN DatosPersonales d ON u.idDatos = d.idDatos
                WHERE c.idProducto = ?
                ORDER BY c.fecha DESC
                """;

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario cliente = new Usuario();
                    cliente.setIdUsuario(rs.getInt("idUsuario"));
                    cliente.setCorreo(rs.getString("correo"));
                    cliente.setDatosPersonales(new Datos(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("telefono")
                    ));

                    Calificacion c = new Calificacion();
                    c.setIdCalificacion(rs.getInt("idCalificacion"));
                    c.setPuntaje(rs.getInt("puntaje"));
                    c.setComentario(rs.getString("comentario"));
                    c.setFecha(rs.getDate("fecha"));
                    c.setCliente(cliente);

                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener calificaciones por producto: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene todas las calificaciones realizadas por un cliente (para mostrar su historial).
     */
    public List<Calificacion> obtenerPorCliente(int idCliente) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = """
                SELECT c.idCalificacion, c.puntaje, c.comentario, c.fecha,
                       p.idProducto, p.titulo, p.descripcion, p.precio, p.tipoProducto
                FROM Calificaciones c
                INNER JOIN Productos p ON c.idProducto = p.idProducto
                WHERE c.idCliente = ?
                ORDER BY c.fecha DESC
                """;

        try (Connection conn = ConexionDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto;
                    if ("CURSO".equalsIgnoreCase(rs.getString("tipoProducto"))) {
                        producto = new Curso();
                    } else {
                        producto = new Servicio();
                    }

                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setTitulo(rs.getString("titulo"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecio(rs.getDouble("precio"));

                    Calificacion cal = new Calificacion();
                    cal.setIdCalificacion(rs.getInt("idCalificacion"));
                    cal.setPuntaje(rs.getInt("puntaje"));
                    cal.setComentario(rs.getString("comentario"));
                    cal.setFecha(rs.getDate("fecha"));
                    cal.setProducto(producto);

                    lista.add(cal);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Error al obtener calificaciones por cliente: " + e.getMessage());
        }
        return lista;
    }

}
