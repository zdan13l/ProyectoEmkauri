package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.stream.Collectors;
import java.util.List;
import modelo.*;
import servicio.*;

// Controlador para gestionar la calificación de productos por parte del cliente.
public class CalificarController {

    // Elementos de la interfaz gráfica.
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<Producto> comboElemento;
    @FXML private Slider sliderPuntaje;
    @FXML private TextArea txtComentario;
    @FXML private Button btnVolver;
    @FXML private Button btnEnviar;

    // Servicios para la lógica y gestor de navegación.
    private final ISProducto servicioP;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Producto (curso o servicio) actualmente cargado para calificar.
    private Producto productoSeleccionado;

    // Constructor que recibe los servicios necesarios.
    public CalificarController(ISProducto servicioP, ISCalificacion servicioCal, GestorPantallas gestorPantallas) {
        this.servicioP = servicioP;
        this.servicioCal = servicioCal;
        this.gestorPantallas = gestorPantallas;
    }

    // Setter para el producto seleccionado.
    public void setProductoSeleccionado(Producto producto) {
        this.productoSeleccionado = producto;
    }

    // Inicialización del controlador.
    @FXML
    private void initialize() {
        comboTipo.setOnAction(e -> cargarElementosSegunTipo());
        comboElemento.setPromptText("Seleccione un curso o servicio");

        // Mostrar el título del producto en el ComboBox.
        comboElemento.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(item.getTitulo());
            }
        });
        comboElemento.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(item.getTitulo());
            }
        });
    }

    // Envía la calificación al servicio para guardarla.
    @FXML
    private void onEnviar(ActionEvent event) {
        // Obtener datos del formulario.
        Producto seleccionado = comboElemento.getValue();
        Usuario cliente = SesionActual.getUsuarioActual();
        int puntaje = (int) sliderPuntaje.getValue();
        String comentario = txtComentario.getText().trim();

        if (cliente == null) {
            gestorPantallas.mostrarError("Error", "Debe iniciar sesión para calificar.");
            return;
        }
        if (seleccionado == null) {
            gestorPantallas.mostrarAlerta("Validación", "Seleccione un elemento para calificar.");
            return;
        }
        if (puntaje < 1 || puntaje > 5) {
            gestorPantallas.mostrarAlerta("Validación", "El puntaje debe estar entre 1 y 5.");
            return;
        }

        // Crear la nueva calificación.
        Calificacion nueva = new Calificacion();
        nueva.setCliente(cliente);
        nueva.setProducto(seleccionado);
        nueva.setPuntaje(puntaje);
        nueva.setComentario(comentario.isEmpty() ? "Sin comentario" : comentario);

        // Manejar resultados de la tarea.
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return servicioCal.crearCalificacion(nueva);
            }
        };
        task.setOnSucceeded(evt -> {
            if (task.getValue()) {
                gestorPantallas.mostrarExito("Éxito", "Calificación enviada correctamente.");
                gestorPantallas.irProductosCliente();
            } else {
                gestorPantallas.mostrarError("Error", "No se pudo guardar la calificación (es posible que ya exista una previa).");
            }
        });
        task.setOnFailed(evt -> gestorPantallas.mostrarError("Error", "Error al enviar calificación: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // Vuelve a la pantalla de productos del cliente.
    @FXML
    private void onVolver(ActionEvent event) { gestorPantallas.irProductosCliente(); }

    // Inicia el formulario con un producto ya seleccionado.
    public void iniciarConProducto() {
        String tipo;
        if (productoSeleccionado.getClass().getSimpleName().equals("Curso")) {
            tipo = "Curso";
        } else { tipo = "Servicio"; }

        // Seleccionar el tipo correspondiente.
        comboTipo.getSelectionModel().select(tipo);
        cargarElementosSegunTipo();

        // Esperar un momento para asegurar que los elementos se hayan cargado.
        Task<Void> selectTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(200);
                return null;
            }
        };

        // Seleccionar el producto correspondiente.
        selectTask.setOnSucceeded(e -> {
            Producto encontrado = comboElemento.getItems()
                    .stream()
                    .filter(p -> p.getIdProducto() == productoSeleccionado.getIdProducto())
                    .findFirst()
                    .orElse(null);
            comboElemento.getSelectionModel().select(encontrado);
        });
        new Thread(selectTask).start();
    }

    // Carga los elementos (cursos o servicios) según el tipo seleccionado.
    private void cargarElementosSegunTipo() {
        String tipo = comboTipo.getValue();
        Usuario cliente = SesionActual.getUsuarioActual();

        if (cliente == null) {
            gestorPantallas.mostrarError("Error", "No hay usuario en sesión.");
            return;
        }
        if (tipo == null) {
            gestorPantallas.mostrarError("Atención", "Selecciona un tipo (Curso o Servicio).");
            return;
        }

        // Cargar productos adquiridos del tipo seleccionado en un hilo separado.
        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                List<Producto> productosAdquiridos = servicioP.listarComprados(cliente.getIdUsuario());
                if (tipo.equals("Curso")) {
                    return productosAdquiridos.stream()
                            .filter(p -> p.getClass().getSimpleName().equals("Curso"))
                            .collect(Collectors.toList());
                } else {
                    return productosAdquiridos.stream()
                            .filter(p -> p.getClass().getSimpleName().equals("Servicio"))
                            .collect(Collectors.toList());
                }
            }
        };

        task.setOnSucceeded(evt -> {
            List<Producto> productos = task.getValue();
            if (productos.isEmpty()) {
                gestorPantallas.mostrarAlerta("Información", "No tienes " + tipo.toLowerCase() + " disponibles para calificar.");
            }
            comboElemento.setItems(FXCollections.observableArrayList(productos));
        });

        task.setOnFailed(evt -> gestorPantallas.mostrarError("Error", "Error al cargar los elementos disponibles."));
        new Thread(task).start();
    }
}
