package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.binding.Bindings;
import javafx.collections.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.List;
import modelo.*;
import servicio.*;

// Controlador para gestionar la visualización y filtrado de calificaciones.
public class CalificacionController {

    // Elementos de la interfaz gráfica.
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<Producto> comboElemento;
    @FXML private Spinner<Integer> spinPuntaje;
    @FXML private TableView<Calificacion> tablaCalificaciones;
    @FXML private TableColumn<Calificacion, String> colProducto;
    @FXML private TableColumn<Calificacion, String> colCliente;
    @FXML private TableColumn<Calificacion, Integer> colPuntaje;
    @FXML private TableColumn<Calificacion, String> colComentario;
    @FXML private TableColumn<Calificacion, String> colFecha;
    @FXML private Label lblPromedioGeneral;
    @FXML private Label mensajeTablaVacia;
    @FXML private Button btnBuscar;

    // Servicios para la lógica y gestor de navegación.
    private final ISProducto servicioP;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Lista observable para las calificaciones mostradas en la tabla.
    private final ObservableList<Calificacion> listaObservable = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public CalificacionController(ISProducto servicioP, ISCalificacion servicioCal, GestorPantallas gestorPantallas) {
        this.servicioP = servicioP;
        this.servicioCal = servicioCal;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicialización del controlador.
    @FXML
    public void initialize() {
        configurarTabla();
        configurarCombos();
        configurarSpinner();

        // Mensaje de tabla vacía.
        mensajeTablaVacia.visibleProperty().bind(Bindings.isEmpty(tablaCalificaciones.getItems()));

        // Por defecto, mostrar ambos tipos.
        comboTipo.getSelectionModel().select("Ambos");
        cargarElementosSegunTipo();
    }

    // Configuración de las columnas de la tabla.
    private void configurarTabla() {
        colProducto.setCellValueFactory(data ->
                Bindings.createStringBinding(() -> {
                    Producto p = data.getValue().getProducto();
                    if (p != null) {
                        return p.getTitulo();
                    } else {
                        return "Producto eliminado";
                    }
                }) );

        colCliente.setCellValueFactory(data ->
                Bindings.createStringBinding(() ->
                        data.getValue().getCliente().getDatosPersonales().getNombre() + " " +
                                data.getValue().getCliente().getDatosPersonales().getApellido()));

        colPuntaje.setCellValueFactory(new PropertyValueFactory<>("puntaje"));
        colComentario.setCellValueFactory(new PropertyValueFactory<>("comentario"));

        colFecha.setCellValueFactory(data ->
                Bindings.createStringBinding(() ->
                        new SimpleDateFormat("yyyy-MM-dd").format(data.getValue().getFecha())));

        tablaCalificaciones.setItems(listaObservable);
    }

    // Configuración de los combos de selección.
    private void configurarCombos() {
        comboElemento.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitulo());
            }
        });

        comboElemento.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitulo());
            }
        });

        comboTipo.setOnAction(e -> cargarElementosSegunTipo());
    }

    // Configuración del spinner de puntaje (1 - 5).
    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
        spinPuntaje.setValueFactory(valueFactory);
    }

    // Carga los elementos en el combo según el tipo seleccionado.
    private void cargarElementosSegunTipo() {
        String tipo = comboTipo.getValue();
        Usuario actual = SesionActual.getUsuarioActual();

        if (actual == null) {
            gestorPantallas.mostrarError("Error", "No hay sesión activa.");
            return;
        }

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                List<Producto> productos = servicioP.listarPorEmprendedor(actual.getIdUsuario());
                if (tipo == null || tipo.equals("Ambos")) { return productos; }

                return productos.stream()
                        .filter(p -> (tipo.equals("Cursos") && p instanceof Curso) ||
                                (tipo.equals("Servicios") && p instanceof Servicio))
                        .collect(Collectors.toList());
            }
        };
        task.setOnSucceeded(evt ->
                comboElemento.setItems(FXCollections.observableArrayList(task.getValue())));

        task.setOnFailed(evt ->
                gestorPantallas.mostrarError("Error", "No se pudieron cargar los elementos."));
        new Thread(task).start();
    }

    // Manejo del evento de búsqueda de calificaciones según los filtros.
    @FXML
    private void onBuscar(ActionEvent event) {
        Usuario actual = SesionActual.getUsuarioActual();
        Producto seleccionado = comboElemento.getValue();
        String tipo = comboTipo.getValue();

        Task<List<Calificacion>> task = new Task<>() {
            @Override
            protected List<Calificacion> call() {
                List<Producto> productos;
                // Si hay un producto seleccionado, usar solo ese.
                if (seleccionado != null) {
                    productos = List.of(seleccionado);
                } else {
                    // Si no, obtener todos los productos del emprendedor y filtrar por tipo.
                    productos = servicioP.listarPorEmprendedor(actual.getIdUsuario());
                    if (!tipo.equals("Ambos")) {
                        productos = productos.stream()
                                .filter(p -> (tipo.equals("Cursos") && p instanceof Curso) ||
                                        (tipo.equals("Servicios") && p instanceof Servicio))
                                .collect(Collectors.toList());
                    }
                }
                // Obtener TODAS las calificaciones.
                List<Calificacion> todas = productos.stream()
                        .flatMap(p -> servicioCal.listarPorProducto(p.getIdProducto()).stream())
                        .collect(Collectors.toList());

                // Filtrar por puntaje mínimo.
                int minimo = spinPuntaje.getValue();
                todas = todas.stream()
                        .filter(c -> c.getPuntaje() >= minimo)
                        .collect(Collectors.toList());

                return todas;
            }
        };
        task.setOnSucceeded(evt -> {
            List<Calificacion> lista = task.getValue();
            listaObservable.setAll(lista);
            actualizarPromedio(lista);
        });

        task.setOnFailed(evt ->
                gestorPantallas.mostrarError("Error", "No se pudieron cargar las calificaciones."));
        new Thread(task).start();
    }

    // Filtra la lista para mostrar solo calificaciones con comentarios.
    @FXML
    private void onFiltrarSoloComentarios(ActionEvent e) {
        List<Calificacion> filtrado = listaObservable.stream()
                .filter(c -> c.getComentario() != null
                        && !c.getComentario().isBlank()
                        && !c.getComentario().equalsIgnoreCase("Sin comentario"))
                .collect(Collectors.toList());

        listaObservable.setAll(filtrado);
        actualizarPromedio(filtrado);
    }

    // Limpia todos los filtros y resetea la vista.
    @FXML
    private void onLimpiarFiltros(ActionEvent event) {
        comboTipo.getSelectionModel().select("Ambos");
        comboElemento.getSelectionModel().clearSelection();
        spinPuntaje.getValueFactory().setValue(1);

        listaObservable.clear();
        lblPromedioGeneral.setText("0.0");
    }

    // Navegar de vuelta a la pantalla del emprendedor.
    @FXML
    private void onVolver(ActionEvent event) { gestorPantallas.irEmprendedor(); }

    // Navegar a la pantalla de productos del emprendedor.
    @FXML
    private void onVerMisProductos(ActionEvent event) { gestorPantallas.irProductosEmprendedor(); }

    // Actualiza la etiqueta del promedio general.
    private void actualizarPromedio(List<Calificacion> lista) {
        if (lista.isEmpty()) {
            lblPromedioGeneral.setText("0.0");
            return;
        }

        double promedio = lista.stream()
                .mapToInt(Calificacion::getPuntaje)
                .average()
                .orElse(0.0);

        lblPromedioGeneral.setText(String.format("%.1f", promedio));
    }
}
