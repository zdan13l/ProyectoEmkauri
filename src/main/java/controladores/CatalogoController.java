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
import modelo.Categoria;
import modelo.Producto;
import servicio.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Controlador para manejar la lógica de la pantalla del catálogo de productos.
public class CatalogoController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private TextField busquedaField;
    @FXML private Button btnCarrito;
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
    private final ISSolicitud servicioS;

    // Lista completa de productos.
    private List<Producto> listaProductos = new ArrayList<>();

    // Constructor que recibe los servicios necesarios.
    public CatalogoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    // Inicializa el controlador: configura tabla, categorías y productos.
    @FXML
    public void initialize() {
        configurarTabla();
        cargarCategorias();
        cargarProductos();
    }

    // Configura las columnas de la tabla de resultados.
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

    // Carga las categorías disponibles en el ComboBox.
    private void cargarCategorias() {
        try {
            List<Categoria> categorias = servicioCa.listarCategorias();
            if (categorias == null || categorias.isEmpty()) {
                categoriaFilter.setItems(FXCollections.observableArrayList("Todos"));
                categoriaFilter.getSelectionModel().select("Todos");
                return;
            }

            List<String> nombres = categorias.stream()
                    .map(Categoria::getNombre)
                    .filter(n -> n != null && !n.isBlank())
                    .distinct()
                    .collect(Collectors.toList());

            nombres.add(0, "Todos");

            categoriaFilter.setItems(FXCollections.observableArrayList(nombres));
            categoriaFilter.getSelectionModel().select("Todos");
        } catch (Exception e) {
            categoriaFilter.setItems(FXCollections.observableArrayList("Todos", "Programación", "Diseño", "Marketing"));
            categoriaFilter.getSelectionModel().select("Todos");
        }
    }

    // Carga todos los productos desde el servicio.
    private void cargarProductos() {
        listaProductos = servicioP.listarProductos();
        mostrarProductos(listaProductos);
    }

    // Muestra los productos en la tabla.
    private void mostrarProductos(List<Producto> productos) {
        ObservableList<Producto> datos = FXCollections.observableArrayList(productos);
        resultadosTable.setItems(datos);
    }

    // Maneja la búsqueda y filtrado de productos.
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

    // Agrega el producto seleccionado al carrito.
    @FXML
    private void onAgregarCarritoClick() {
        Producto seleccionado = resultadosTable.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Error", "Seleccione un producto antes de agregar al carrito.");
            return;
        }

        SesionActual.agregarProductoAlCarrito(seleccionado);
    }

    // Vuelve a la pantalla del cliente.
    @FXML
    private void onVolverClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/cliente.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) busquedaField.getScene().getWindow();
            stage.setTitle("Menú del Cliente");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver a la pantalla del cliente.");
        }
    }

    // Abre la pantalla del carrito de compras.
    @FXML
    private void onVerCarrito() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/carrito.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) busquedaField.getScene().getWindow();
            stage.setTitle("Carrito de Compras");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el carrito.");
        }
    }

    // Muestra una alerta con título y mensaje proporcionados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
