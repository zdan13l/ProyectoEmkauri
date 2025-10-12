package fis.jave.emkauri;

import controladores.Controlador;
import controladores.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repositorio.ConexionDB;
import repositorio.RUsuario;
import servicio.SUsuario;

import java.sql.Connection;

public class EmkauriApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Inyección de dependencias manual.
        RUsuario repoUsuario = new RUsuario();
        SUsuario servicioUsuario = new SUsuario(repoUsuario);

        // Cargar la interfaz de login.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
        fxmlLoader.setControllerFactory(param -> new Controlador(servicioUsuario).createController(param));

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Login - Emkauri");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        // Modo pruebas por defecto
        ConexionDB.setModoPruebas(true);

        try (Connection conn = ConexionDB.getConnection()) {
            ConexionDB.initSchema(conn);
            ConexionDB.loadTestData(conn);
            System.out.println("DB de PRUEBAS creada en memoria con datos iniciales.");
        }

        // Levantar servidor TCP para poder verla desde consola H2
        ConexionDB.startTcpAndWebServer();

        // Lanzar la aplicación JavaFX
        launch();
    }
}