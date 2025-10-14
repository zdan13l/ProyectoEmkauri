package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.Compra;
import modelo.Producto;
import modelo.Usuario;
import servicio.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Controlador para manejar la lógica de la pantalla del catálogo de productos.
public class CatalogoController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private TextField busquedaField;
    @FXML private ComboBox<String> categoriaFilter;
    @FXML private TableView<Producto> resultadosTable;
    @FXML private TableColumn<Producto, String> tipoColumn;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, String> descripcionColumn;
    @FXML private TableColumn<Producto, String> categoriaColumn;
    @FXML private TableColumn<Producto, Double> precioColumn;

    // Servicios para manejar la lógica.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;

    // Lista completa de productos y carrito de compras.
    private List<Producto> listaProductos = new ArrayList<>();

    public CatalogoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
    }

    // Inicialización del controlador.
    @FXML
    public void initialize() {
        configurarTabla();
        cargarCategorias();
        cargarProductos();
    }

    // Configuración de la tabla de resultados.
    private void configurarTabla() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));

        categoriaColumn.setCellValueFactory(cellData ->
                Bindings.createObjectBinding(() -> {
                    if (cellData.getValue().getCategoria() != null)
                        return cellData.getValue().getCategoria().getNombre();
                    return "";
                })
        );

        tipoColumn.setCellValueFactory(cellData ->
                Bindings.createObjectBinding(() -> {
                    Producto p = cellData.getValue();
                    if (p == null) return "";
                    if (p.getClass().getSimpleName().equals("Curso")) return "CURSO";
                    if (p.getClass().getSimpleName().equals("Servicio")) return "SERVICIO";
                    return "DESCONOCIDO";
                })
        );
    }

    // Cargar categorías y productos desde el servicio.
    private void cargarCategorias() {
        categoriaFilter.setItems(FXCollections.observableArrayList(
                "Todos", "Programación", "Diseño", "Marketing"
        ));
        categoriaFilter.getSelectionModel().select("Todos");
    }

    // Cargar todos los productos desde el servicio.
    private void cargarProductos() {
        listaProductos = servicioP.listarProductos();
        mostrarProductos(listaProductos);
    }

    // Mostrar productos en la tabla.
    private void mostrarProductos(List<Producto> productos) {
        ObservableList<Producto> datos = FXCollections.observableArrayList(productos);
        resultadosTable.setItems(datos);
    }

    // Comportamiento del botón de búsqueda y filtrado.
    @FXML
    private void onSearchClick() {
        String texto = busquedaField.getText().trim().toLowerCase();
        String categoriaSeleccionada = categoriaFilter.getValue();

        List<Producto> filtrados = listaProductos.stream()
                .filter(p -> {
                    boolean coincideTexto = texto.isEmpty() ||
                            p.getTitulo().toLowerCase().contains(texto) ||
                            p.getDescripcion().toLowerCase().contains(texto);

                    boolean coincideCategoria = categoriaSeleccionada.equals("Todos") ||
                            (p.getCategoria() != null &&
                                    p.getCategoria().getNombre().equalsIgnoreCase(categoriaSeleccionada));

                    return coincideTexto && coincideCategoria;
                })
                .collect(Collectors.toList());

        mostrarProductos(filtrados);
    }

    // Agregar producto seleccionado al carrito.
    @FXML
    private void onAgregarCarritoClick() {
        Producto seleccionado = resultadosTable.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto antes de agregar al carrito.");
            return;
        }

        SesionActual.agregarProductoAlCarrito(seleccionado);
    }

    // Volver a la pantalla del cliente.
    @FXML
    private void onVolverClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/cliente.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) busquedaField.getScene().getWindow();
            stage.setTitle("Menú del Cliente");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("No se pudo volver a la pantalla del cliente.");
        }
    }

    // Mostrar alertas informativas.
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
