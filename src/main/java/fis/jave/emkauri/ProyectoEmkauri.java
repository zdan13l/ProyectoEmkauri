package fis.jave.emkauri;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProyectoEmkauri extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Cargar el FXML desde la carpeta resources
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/puj.fis.pantallas/login.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Login - Emkauri");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}