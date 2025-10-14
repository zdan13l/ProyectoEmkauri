package repositorio;

import modelo.Compra;
import modelo.Producto;
import modelo.Pago;
import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Repositorio para gestionar las operaciones CRUD relacionadas con las compras.
public class RCompra implements IRCompra {

    // Insertar una nueva compra junto con sus productos asociados.
    @Override
    public boolean insertar(Compra compra) {
        String sqlCompra = "INSERT INTO Compras (idCliente, montoFinal, idPago) VALUES (?, ?, ?)";
        String sqlCompraProducto = "INSERT INTO ComprasProductos (idCompra, idProducto, precioCompra) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.getConnection()) {
            conexion.setAutoCommit(false);

            // Insertar la compra principal.
            PreparedStatement psCompra = conexion.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS);
            psCompra.setInt(1, compra.getCliente().getIdUsuario());
            psCompra.setDouble(2, compra.getMontoFinal());
            psCompra.setInt(3, compra.getPago().getIdPago());
            psCompra.executeUpdate();

            // Obtener ID generado.
            ResultSet rs = psCompra.getGeneratedKeys();
            int idCompra = 0;

            if (rs.next()) {
                idCompra = rs.getInt(1);
            }

            // Insertar productos asociados a la compra.
            PreparedStatement psProductos = conexion.prepareStatement(sqlCompraProducto);
            for (Producto p : compra.getProductos()) {
                psProductos.setInt(1, idCompra);
                psProductos.setInt(2, p.getIdProducto());
                psProductos.setDouble(3, p.getPrecio());
                psProductos.addBatch();
            }
            psProductos.executeBatch();

            conexion.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error insertando compra: " + e.getMessage());
            try (Connection conexion = ConexionDB.getConnection()) {
                conexion.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
        }
        return false;
    }

    // Obtener una compra por su ID, incluyendo productos asociados.
    @Override
    public Compra obtenerPorId(int id) {
        String sql = "SELECT * FROM Compras WHERE idCompra = ?";
        Compra compra = null;

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                compra = new Compra();
                compra.setIdCompra(rs.getInt("idCompra"));
                compra.setMontoFinal(rs.getDouble("montoFinal"));
                compra.setFechaCompra(rs.getDate("fechaCompra"));

                // Cliente.
                Usuario cliente = new Usuario();
                cliente.setIdUsuario(rs.getInt("idCliente"));
                compra.setCliente(cliente);

                // Pago.
                Pago pago = new Pago();
                pago.setIdPago(rs.getInt("idPago"));
                compra.setPago(pago);

                // Productos asociados.
                compra.setProductos(obtenerProductosPorCompra(conexion, id));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo compra por ID: " + e.getMessage());
        }
        return compra;
    }

    // Obtener los productos asociados a una compra específica.
    private List<Producto> obtenerProductosPorCompra(Connection conexion, int idCompra) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.idProducto, p.titulo, cp.precioCompra " +
                "FROM ComprasProductos cp " +
                "JOIN Productos p ON cp.idProducto = p.idProducto " +
                "WHERE cp.idCompra = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setTitulo(rs.getString("titulo"));
                p.setPrecio(rs.getDouble("precioCompra"));
                productos.add(p);
            }
        }
        return productos;
    }

    // Listar todas las compras en el sistema.
    @Override
    public List<Compra> listar() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM Compras";

        try (Connection conexion = ConexionDB.getConnection(); Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Compra compra = new Compra();
                compra.setIdCompra(rs.getInt("idCompra"));
                compra.setMontoFinal(rs.getDouble("montoFinal"));
                compra.setFechaCompra(rs.getDate("fechaCompra"));

                Usuario cliente = new Usuario();
                cliente.setIdUsuario(rs.getInt("idCliente"));
                compra.setCliente(cliente);

                Pago pago = new Pago();
                pago.setIdPago(rs.getInt("idPago"));
                compra.setPago(pago);

                compras.add(compra);
            }
        } catch (SQLException e) {
            System.err.println("Error listando compras: " + e.getMessage());
        }
        return compras;
    }

    // Actualizar los detalles de una compra existente.
    @Override
    public boolean actualizar(Compra compra) {
        String sql = "UPDATE Compras SET montoFinal = ?, idPago = ? WHERE idCompra = ?";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, compra.getMontoFinal());
            ps.setInt(2, compra.getPago().getIdPago());
            ps.setInt(3, compra.getIdCompra());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando compra: " + e.getMessage());
        }
        return false;
    }

    // Eliminar una compra por su ID.
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Compras WHERE idCompra = ?";

        try (Connection conexion = ConexionDB.getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando compra: " + e.getMessage());
        }
        return false;
    }
}
