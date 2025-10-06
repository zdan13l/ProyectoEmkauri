package fis.jave.emkauri;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.ItemCarrito;
import servicio.SCarrito;

public class TestCarrito extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SCarrito s = SCarrito.getInstance();
        // ✅ En Java no hay parámetros con nombre; usa el constructor normal:
        s.agregarItem(new ItemCarrito(1, "Curso Java", "Curso", 150.0, 1));
        s.agregarItem(new ItemCarrito(2, "Diseño Web", "Servicio", 200.0, 2));

        FXMLLoader loader = new FXMLLoader(
            TestCarrito.class.getResource("/puj.fis.pantallas/carrito.fxml")
        );
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Carrito de Compras");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
