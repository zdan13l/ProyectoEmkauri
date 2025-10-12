package fis.jave.emkauri;

import controladores.Controlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repositorio.ConexionDB;
import repositorio.IRUsuario;
import repositorio.RUsuario;
import servicio.ISUsuario;
import servicio.SUsuario;
import java.sql.Connection;

public class EmkauriApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Inyección de dependencias manual.
        IRUsuario repoUsuario = new RUsuario();
        ISUsuario servicioUsuario = new SUsuario(repoUsuario);

        // Cargar la interfaz de login.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
        fxmlLoader.setControllerFactory(param -> new Controlador(servicioUsuario).createController(param));

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Login - Emkauri");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        // Modo pruebas por defecto.
        ConexionDB.setModoPruebas(true);

        // Iniciar el servidor TCP y Web si no estamos en modo pruebas.
        if (ConexionDB.modoPruebas) {
            ConexionDB.startTcpAndWebServer();
        }

        // Conectar y cargar los scripts.
        try (Connection conn = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conn);
            ConexionDB.loadTestData(conn);
            System.out.println("DB de PRUEBAS creada en memoria con datos iniciales.");
        }

        // Lanzar la aplicación JavaFX.
        launch();
    }
}