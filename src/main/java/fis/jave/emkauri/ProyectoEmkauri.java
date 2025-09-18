package fis.jave.emkauri;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repositorio.ConexionDB;

import java.sql.Connection;
import java.util.Scanner;

public class ProyectoEmkauri extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Login - Emkauri");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Selecciona el entorno: ");
        System.out.println("1. Producción (BD en archivo)");
        System.out.println("2. Pruebas (BD en memoria)");
        System.out.print("Opción: ");

        int opcion = scanner.nextInt();

        if (opcion == 1) {
            ConexionDB.setModoPruebas(false);
            try (Connection conn = ConexionDB.getConnection()) {
                ConexionDB.initSchema(conn);
                System.out.println("Base de PRODUCCIÓN lista.");
            }
        } else {
            ConexionDB.setModoPruebas(true);

            // Crear esquema y cargar datos en la DB en memoria
            try (Connection conn = ConexionDB.getConnection()) {
                ConexionDB.initSchema(conn);
                ConexionDB.loadTestData(conn);
                System.out.println("DB de PRUEBAS creada en memoria con datos iniciales.");
            }

            // Levantar servidor TCP para poder visualizar la DB en consola H2
            ConexionDB.startTcpServer();
        }

        launch();
    }
}