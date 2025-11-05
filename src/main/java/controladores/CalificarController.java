package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.*;
import servicio.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para manejar la pantalla donde el cliente califica
 * cursos o servicios adquiridos.
 */
public class CalificarController {

    // ELEMENTOS DE LA INTERFAZ
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<Producto> comboElemento;
    @FXML private Slider sliderPuntaje;
    @FXML private TextArea txtComentario;
    @FXML private Button btnVolver;
    @FXML private Button btnCargar;
    @FXML private Button btnEnviar;

    // SERVICIOS
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal; // servicio de calificaciones

    // CONSTRUCTOR
    public CalificarController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP,
                               ISCategoria servicioCa, ISPago servicioPa,
                               ISSolicitud servicioS, ISCalificacion servicioCal) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
    }

    // Inicialización
    @FXML
    private void initialize() {
        comboTipo.setOnAction(e -> cargarElementosSegunTipo());
        comboElemento.setPromptText("Seleccione un curso o servicio");
    }

    /**
     * Carga los productos adquiridos según el tipo seleccionado (Curso o Servicio).
     */
    @FXML
    private void onCargar(ActionEvent event) {
        cargarElementosSegunTipo();
    }

    private void cargarElementosSegunTipo() {
        String tipo = comboTipo.getValue();
        Usuario cliente = SesionActual.getUsuarioActual();

        if (cliente == null) {
            mostrarAlerta("Error", "No hay usuario en sesión.");
            return;
        }
        if (tipo == null) {
            mostrarAlerta("Atención", "Selecciona un tipo (Curso o Servicio).");
            return;
        }

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                List<Producto> productosAdquiridos = servicioP.listarComprados(cliente.getIdUsuario());
                if (tipo.equals("Curso")) {
                    return productosAdquiridos.stream()
                            .filter(p -> p.getCategoria().getNombre().equalsIgnoreCase("Curso"))
                            .collect(Collectors.toList());
                } else if (tipo.equals("Servicio")) {
                    return productosAdquiridos.stream()
                            .filter(p -> p.getCategoria().getNombre().equalsIgnoreCase("Servicio"))
                            .collect(Collectors.toList());
                } else {
                    return productosAdquiridos;
                }
            }
        };

        task.setOnSucceeded(evt -> {
            List<Producto> productos = task.getValue();
            if (productos.isEmpty()) {
                mostrarAlerta("Información", "No tienes " + tipo.toLowerCase() + " disponibles para calificar.");
            }
            comboElemento.setItems(FXCollections.observableArrayList(productos));
        });

        task.setOnFailed(evt -> mostrarAlerta("Error", "Error al cargar los elementos disponibles."));
        new Thread(task).start();
    }

    /**
     * Envía la calificación seleccionada.
     */
    @FXML
    private void onEnviar(ActionEvent event) {
        Producto seleccionado = comboElemento.getValue();
        Usuario cliente = SesionActual.getUsuarioActual();
        int puntaje = (int) sliderPuntaje.getValue();
        String comentario = txtComentario.getText().trim();

        if (cliente == null) {
            mostrarAlerta("Error", "Debe iniciar sesión para calificar.");
            return;
        }
        if (seleccionado == null) {
            mostrarAlerta("Validación", "Seleccione un elemento para calificar.");
            return;
        }
        if (puntaje < 1 || puntaje > 5) {
            mostrarAlerta("Validación", "El puntaje debe estar entre 1 y 5.");
            return;
        }

        Calificacion nueva = new Calificacion();
        nueva.setCliente(cliente);
        nueva.setProducto(seleccionado);
        nueva.setPuntaje(puntaje);
        nueva.setComentario(comentario.isEmpty() ? "Sin comentario" : comentario);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return servicioCal.crearCalificacion(nueva);
            }
        };

        task.setOnSucceeded(evt -> {
            if (task.getValue()) {
                mostrarAlerta("Éxito", "Calificación enviada correctamente.");
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la calificación (es posible que ya exista una previa).");
            }
        });

        task.setOnFailed(evt -> mostrarAlerta("Error", "Error al enviar calificación: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    /**
     * Vuelve al catálogo del cliente.
     */
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/productosC.fxml"));
            Controlador factory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS, servicioCal);
            loader.setControllerFactory(factory::createController);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Catálogo");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver al catálogo: " + e.getMessage());
        }
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarCampos() {
        comboTipo.getSelectionModel().clearSelection();
        comboElemento.getItems().clear();
        txtComentario.clear();
        sliderPuntaje.setValue(3);
    }

    /**
     * Muestra un mensaje de alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
