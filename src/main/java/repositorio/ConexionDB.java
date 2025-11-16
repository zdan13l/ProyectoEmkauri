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

// Clase para gestionar la conexión a la base de datos H2.
public class ConexionDB {
    public static boolean modoPruebas;
    private static Server tcpServer;
    private static Server webServer;

    // Permite establecer el modo de ejecución.
    public static void setModoPruebas(boolean pruebas) {
        modoPruebas = pruebas;
    }

    // Retorna una conexión H2 dependiendo del modo.
    public static Connection getConnection() throws SQLException {
        if (modoPruebas) {
            return DriverManager.getConnection(
                    "jdbc:h2:tcp://localhost:9093/mem:emkauriPruebas;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                    "sa",
                    ""
            );
        } else {
            // Base persistente en archivo.
            return DriverManager.getConnection(
                    "jdbc:h2:./emkauriDB;AUTO_SERVER=TRUE",
                    "sa",
                    ""
            );
        }
    }

    // Ejecuta el script DDL para crear las tablas.
    public static void initSchema(Connection conn) {
        try {
            String ddlPath = Paths.get("src/main/resources/sql/DDL.sql").toAbsolutePath().toString();
            RunScript.execute(conn, new FileReader(ddlPath));
            System.out.println(" - Script DDL ejecutado correctamente.");
        } catch (Exception e) {
            System.err.println(" - Error ejecutando el script DDL: " + e.getMessage());
        }
    }

    // Carga datos iniciales de prueba.
    public static void loadTestData(Connection conn) {
        try {
            String dataPath = Paths.get("src/main/resources/sql/data.sql").toAbsolutePath().toString();
            RunScript.execute(conn, new FileReader(dataPath));
            System.out.println(" - Script DATA ejecutado correctamente.");
        } catch (Exception e) {
            System.err.println(" - Error ejecutando el script DATA: " + e.getMessage());
        }
    }

    // Inicia los servidores TCP y Web con detección de puertos libres.
    public static void startTcpAndWebServer() {
        try {
            // Si ya están corriendo, no los reinicia.
            if (tcpServer == null || !tcpServer.isRunning(true)) {
                tcpServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9093", "-ifNotExists").start();
            } else {
                System.out.println(" - Servidor TCP ya está corriendo en: " + tcpServer.getURL());
            }
            // WebConsole — usa puerto aleatorio libre.
            if (webServer == null || !webServer.isRunning(true)) {
                webServer = Server.createWebServer("-webAllowOthers", "-webPort", "0").start();
                String url = webServer.getURL();
                abrirNavegador(url);
            } else {
                System.out.println(" - Consola H2 ya está corriendo en: " + webServer.getURL());
            }
        } catch (SQLException e) {
            System.err.println(" - Error iniciando los servidores H2: " + e.getMessage());
        }
    }

    // Abre la consola en el navegador de manera segura.
    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println(" - Navegador abierto en " + url);
            } else {
                System.out.println(" - Desktop no soportado. Abre manualmente: " + url);
            }
        } catch (Exception e) {
            System.err.println(" - No se pudo abrir el navegador: " + e.getMessage());
        }
    }
}
