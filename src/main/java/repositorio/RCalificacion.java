package repositorio;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

// Repositorio para acceder a la tabla Calificacion en la base de datos.
public class RCalificacion {

    // Crear calificación.
    public boolean crearCalificacion(int idCalificacion, int nota, String comentario, String fecha, int idCliente, int idCurso) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO Calificacion (idCalificacion, nota, comentario, fecha, idCliente, idCurso) VALUES ("
                    + idCalificacion + ", "
                    + nota + ", '"
                    + comentario + "', '"
                    + fecha + "', "
                    + idCliente + ", "
                    + idCurso + ")";

            int filas = stmt.executeUpdate(sql);
            return filas > 0;

        } catch (Exception e) {
            System.err.println("Error creando calificación: " + e.getMessage());
        }
        return false;
    }

    // Leer calificación por ID.
    public ResultSet leerCalificacion(int idCalificacion) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM Calificacion WHERE idCalificacion = " + idCalificacion;
            return stmt.executeQuery(sql);

        } catch (Exception e) {
            System.err.println("Error leyendo calificación: " + e.getMessage());
        }
        return null;
    }

    // Actualizar calificación.
    public boolean actualizarCalificacion(int idCalificacion, int nota, String comentario, String fecha, int idCliente, int idCurso) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "UPDATE Calificacion SET "
                    + "nota = " + nota + ", "
                    + "comentario = '" + comentario + "', "
                    + "fecha = '" + fecha + "', "
                    + "idCliente = " + idCliente + ", "
                    + "idCurso = " + idCurso
                    + " WHERE idCalificacion = " + idCalificacion;

            int filas = stmt.executeUpdate(sql);
            return filas > 0;

        } catch (Exception e) {
            System.err.println("Error actualizando calificación: " + e.getMessage());
        }
        return false;
    }

    // Eliminar calificación
    public boolean eliminarCalificacion(int idCalificacion) {
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "DELETE FROM Calificacion WHERE idCalificacion = " + idCalificacion;
            int filas = stmt.executeUpdate(sql);
            return filas > 0;

        } catch (Exception e) {
            System.err.println("Error eliminando calificación: " + e.getMessage());
        }
        return false;
    }
}
