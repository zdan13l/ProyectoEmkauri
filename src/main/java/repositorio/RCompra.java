package repositorio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// Repositorio para acceder a la tabla Compra en la base de datos.
public class RCompra {

    // Crear compra.
    public boolean crearCompra(int idCompra, int idCliente, int idCurso, int idServicio, int idPago, String fecha) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO Compra (idCompra, idCliente, idCurso, idServicio, idPago, fecha) VALUES ("
                    + idCompra + ", " + idCliente + ", " + idCurso + ", " + idServicio + ", " + idPago + ", '" + fecha + "')";

            int filas = stmt.executeUpdate(sql);
            return filas > 0;

        } catch (Exception e) {
            System.err.println("Error creando compra: " + e.getMessage());
        }
        return false;
    }

    // Leer compra por ID.
    public ResultSet leerCompra(int idCompra) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM Compra WHERE idCompra = " + idCompra;
            return stmt.executeQuery(sql);

        } catch (Exception e) {
            System.err.println("Error leyendo compra: " + e.getMessage());
        }
        return null;
    }

    // Actualizar compra.
    public boolean actualizarCompra(int idCompra, int idCliente, int idCurso, int idServicio, int idPago, String fecha) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "UPDATE Compra SET " + "idCliente = " + idCliente + ", " + "idCurso = " + idCurso + ", " + "idServicio = " + idServicio + ", "
                    + "idPago = " + idPago + ", " + "fecha = '" + fecha + "' " + "WHERE idCompra = " + idCompra;

            int filas = stmt.executeUpdate(sql);
            return filas > 0;

        } catch (Exception e) {
            System.err.println("Error actualizando compra: " + e.getMessage());
        }
        return false;
    }

    // Eliminar compra.
    public boolean eliminarCompra(int idCompra) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "DELETE FROM Compra WHERE idCompra = " + idCompra;
            int filas = stmt.executeUpdate(sql);
            return filas > 0;

        } catch (Exception e) {
            System.err.println("Error eliminando compra: " + e.getMessage());
        }
        return false;
    }
}
