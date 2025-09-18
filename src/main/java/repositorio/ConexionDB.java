package repositorio;

import org.h2.tools.RunScript;
import org.h2.tools.Server;
import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static boolean modoPruebas = false;
    private static Server tcpServer;

    public static void setModoPruebas(boolean pruebas) {
        modoPruebas = pruebas;
    }

    public static Connection getConnection() throws SQLException {
        if (modoPruebas) {
            // Base en memoria compartida entre embebido y TCP
            return DriverManager.getConnection(
                    "jdbc:h2:mem:emkauriPruebas;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "sa",
                    ""
            );
        } else {
            // Base en archivo (Producción)
            return DriverManager.getConnection(
                    "jdbc:h2:./emkauriDB;AUTO_SERVER=TRUE",
                    "sa",
                    ""
            );
        }
    }

    // Ejecuta el script DDL.sql para crear las tablas
    public static void initSchema(Connection conn) {
        try {
            String ddlPath = Paths.get("src/main/resources/sql/DDL.sql").toAbsolutePath().toString();
            RunScript.execute(conn, new FileReader(ddlPath));
            System.out.println("Script DDL ejecutado correctamente.");
        } catch (Exception e) {
            System.err.println("Error ejecutando el script DDL: " + e.getMessage());
        }
    }

    // Ejecuta el script DATA.sql para poblar datos mínimos
    public static void loadTestData(Connection conn) {
        try {
            String dataPath = Paths.get("src/main/resources/sql/data.sql").toAbsolutePath().toString();
            RunScript.execute(conn, new FileReader(dataPath));
            System.out.println("Script DATA ejecutado correctamente.");
        } catch (Exception e) {
            System.err.println("Error ejecutando el script DATA: " + e.getMessage());
        }
    }

    // Inicia el servidor TCP de H2 en el puerto 9093
    public static void startTcpServer() throws SQLException {
        if (tcpServer == null || !tcpServer.isRunning(true)) {
            tcpServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9093", "-ifNotExists").start();
            System.out.println("Servidor H2 TCP iniciado en: " + tcpServer.getURL());
            System.out.println("Conéctate con: jdbc:h2:tcp://localhost:9093/mem:emkauriPruebas");
        }
    }

    public static void stopTcpServer() {
        if (tcpServer != null) {
            tcpServer.stop();
            System.out.println("Servidor H2 TCP detenido.");
        }
    }
}
