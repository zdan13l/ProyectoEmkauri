package fis.jave.emkauri;

import controladores.FabricaController;
import javafx.application.Application;
import javafx.stage.Stage;
import repositorio.*;

import java.sql.Connection;

// Clase principal de la aplicación JavaFX.
public class EmkauriApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Crear la fábrica de controladores.
        FabricaController fabrica = new FabricaController(stage);

        // Ir a la pantalla de login.
        fabrica.getGestorPantallas().irLogin();
        stage.setTitle("Login - Emkauri");
        stage.setMaximized(true);
        stage.show();
    }

    // Inicio de la aplicación.
    public static void main(String[] args) throws Exception {
        // Modo pruebas por defecto.
        ConexionDB.setModoPruebas(true);

        // Iniciar el servidor TCP y Web si estamos en modo pruebas.
        if (ConexionDB.modoPruebas) {
            ConexionDB.startTcpAndWebServer();

            // Conectar y cargar los scripts.
            try (Connection conexion = ConexionDB.getConnection()) {
                ConexionDB.initSchema(conexion);
                ConexionDB.loadTestData(conexion);
                System.out.println("DB de PRUEBAS creada en memoria con datos iniciales.");
            }
        } else {
            // Modo persistente en archivo.
            try (Connection conexion = ConexionDB.getConnection()) {
                System.out.println("Conectado a la base de datos persistente en archivo.");
            }
        }

        // Lanzar la aplicación JavaFX.
        launch();
    }
}
