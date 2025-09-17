package repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:h2:./src/main/resources/db/emkauriDB";
    private static final String USER = "emkauri";
    private static final String PASSWORD = "12345";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
