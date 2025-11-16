package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.*;
import servicio.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para manejar la pantalla de visualización de calificaciones
 * del emprendedor (cursos y servicios).
 */
public class CalificacionController {

    // Elementos de la interfaz
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<Producto> comboElemento;
    @FXML private Button btnBuscar;
    @FXML private Button btnMisProductos;
    @FXML private Button btnVolver;
    @FXML private TableView<Calificacion> tablaCalificaciones;
    @FXML private TableColumn<Calificacion, String> colProducto;
    @FXML private TableColumn<Calificacion, String> colCliente;
    @FXML private TableColumn<Calificacion, Integer> colPuntaje;
    @FXML private TableColumn<Calificacion, String> colComentario;
    @FXML private TableColumn<Calificacion, String> colFecha;
    @FXML private Label lblPromedioGeneral;

    // Servicios inyectados
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal; // servicio nuevo
    private final GestorPantallas gestorPantallas;

    // Listas observables
    private final ObservableList<Calificacion> listaObservable = FXCollections.observableArrayList();
    @FXML
    private Label mensajeTablaVacia;

    // Constructor
    public CalificacionController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
                                  ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal, GestorPantallas gestorPantallas) {
        this.servicioCo = servicioCo;
        this.servicioU = servicioU;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicialización
    @Deprecated
    public void initialize(URL location, ResourceBundle resources) {
        mensajeTablaVacia.visibleProperty().bind(Bindings.isEmpty(tablaCalificaciones.getItems()));
        configurarTabla();
        configurarCombos();
    }

    private void configurarTabla() {
        colProducto.setCellValueFactory(data ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> data.getValue().getProducto().getTitulo()
                ));
        colCliente.setCellValueFactory(data ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> data.getValue().getCliente().getDatosPersonales().getNombre()
                ));
        colPuntaje.setCellValueFactory(new PropertyValueFactory<>("puntaje"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        colFecha.setCellValueFactory(data ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> data.getValue().getFecha().toString()
                ));
        tablaCalificaciones.setItems(listaObservable);
    }

    private void configurarCombos() {
        comboTipo.setOnAction(e -> cargarElementosSegunTipo());
    }

    /**
     * Carga los elementos en comboElemento según el tipo seleccionado.
     */
    private void cargarElementosSegunTipo() {
        String tipo = comboTipo.getValue();
        Usuario actual = SesionActual.getUsuarioActual();

        if (actual == null) {
            gestorPantallas.mostrarAlerta("Error", "No hay sesión activa.");
            return;
        }

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                List<Producto> productos = servicioP.listarPorEmprendedor(actual.getIdUsuario());
                if (tipo == null || tipo.equals("Ambos")) {
                    return productos;
                }
                return productos.stream()
                        .filter(p -> (tipo.equals("Cursos") && p instanceof Curso)
                                || (tipo.equals("Servicios") && p instanceof Servicio))
                        .collect(Collectors.toList());
            }
        };

        task.setOnSucceeded(evt -> comboElemento.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(evt -> gestorPantallas.mostrarAlerta("Error", "No se pudieron cargar los elementos."));
        new Thread(task).start();
    }

    /**
     * Evento al presionar el botón "Buscar".
     */
    @FXML
    private void onBuscar(ActionEvent event) {
        Producto seleccionado = comboElemento.getValue();
        if (seleccionado == null) {
            gestorPantallas.mostrarAlerta("Atención", "Seleccione un elemento para ver sus calificaciones.");
            return;
        }

        Task<List<Calificacion>> task = new Task<>() {
            @Override
            protected List<Calificacion> call() {
                return servicioCal.listarPorProducto(seleccionado.getIdProducto());
            }
        };

        task.setOnSucceeded(evt -> {
            List<Calificacion> calificaciones = task.getValue();
            listaObservable.setAll(calificaciones);
            actualizarPromedio(calificaciones);
        });

        task.setOnFailed(evt -> gestorPantallas.mostrarAlerta("Error", "Error al cargar las calificaciones."));
        new Thread(task).start();
    }

    private void actualizarPromedio(List<Calificacion> calificaciones) {
        if (calificaciones.isEmpty()) {
            lblPromedioGeneral.setText("0.0");
            return;
        }
        double promedio = calificaciones.stream()
                .mapToInt(Calificacion::getPuntaje)
                .average()
                .orElse(0.0);
        lblPromedioGeneral.setText(String.format("%.1f", promedio));
    }

    /**
     * Navegación: volver al panel del Emprendedor.
     */
    @FXML
    private void onVolver(ActionEvent event) {
        gestorPantallas.irEmprendedor();
    }

    /**
     * Navegación: ir a pantalla Mis Productos.
     */
    @FXML
    private void onVerMisProductos(ActionEvent event) {
        gestorPantallas.irProductosEmprendedor();
    }
}
