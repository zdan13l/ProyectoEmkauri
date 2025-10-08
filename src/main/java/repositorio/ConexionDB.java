package repositorio;

import org.h2.tools.RunScript;
import org.h2.tools.Server;
import java.awt.Desktop;
import java.io.FileReader;
import java.net.URI;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static boolean modoPruebas = false;
    private static Server tcpServer;
    private static Server webServer;

    public static void setModoPruebas(boolean pruebas) {
        modoPruebas = pruebas;
    }

    public static Connection getConnection() throws SQLException {
        if (modoPruebas) {
            return DriverManager.getConnection(
                    "jdbc:h2:mem:emkauriPruebas;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "sa",
                    ""
            );
        } else {
            return DriverManager.getConnection(
                    "jdbc:h2:./emkauriDB;AUTO_SERVER=TRUE",
                    "sa",
                    ""
            );
        }
    }

    public static void initSchema(Connection conn) {
        try {
            String ddlPath = Paths.get("src/main/resources/sql/DDL.sql").toAbsolutePath().toString();
            RunScript.execute(conn, new FileReader(ddlPath));
            System.out.println("Script DDL ejecutado correctamente.");
        } catch (Exception e) {
            System.err.println("Error ejecutando el script DDL: " + e.getMessage());
        }
    }

    public static void loadTestData(Connection conn) {
        try {
            String dataPath = Paths.get("src/main/resources/sql/data.sql").toAbsolutePath().toString();
            RunScript.execute(conn, new FileReader(dataPath));
            System.out.println("Script DATA ejecutado correctamente.");
        } catch (Exception e) {
            System.err.println("Error ejecutando el script DATA: " + e.getMessage());
        }
    }

    // Inicia el servidor TCP y la consola web
    public static void startTcpAndWebServer() throws SQLException {
        if (tcpServer == null || !tcpServer.isRunning(true)) {
            tcpServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9093", "-ifNotExists").start();
            System.out.println("Servidor H2 TCP iniciado en: " + tcpServer.getURL());
            System.out.println("Conéctate con: jdbc:h2:tcp://localhost:9093/mem:emkauriPruebas");
        }

        if (webServer == null || !webServer.isRunning(true)) {
            webServer = Server.createWebServer("-webAllowOthers", "-webPort", "8082").start();
            System.out.println("Consola H2 iniciada en: " + webServer.getURL());
            System.out.println("Abre en tu navegador: http://localhost:8082");

            // 👇 Abrir el navegador automáticamente
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI("http://localhost:8082"));
                    System.out.println("Navegador abierto en http://localhost:8082");
                } else {
                    System.out.println("Desktop no soportado, abre manualmente: http://localhost:8082");
                }
            } catch (Exception e) {
                System.err.println("No se pudo abrir el navegador: " + e.getMessage());
            }
        }
    }

    public static void stopServers() {
        if (tcpServer != null) {
            tcpServer.stop();
            System.out.println("Servidor H2 TCP detenido.");
        }
        if (webServer != null) {
            webServer.stop();
            System.out.println("Consola H2 detenida.");
        }
    }
}
