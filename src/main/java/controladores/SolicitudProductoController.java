package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelo.*;
import servicio.*;
import java.io.IOException;
import java.util.List;

// Controlador para la pantalla de solicitud de productos (cursos o servicios).
public class SolicitudProductoController {

    // Elementos FXML del formulario.
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnEnviarSolicitud;
    @FXML
    private RadioButton rbCurso;
    @FXML
    private RadioButton rbServicio;
    @FXML
    private ToggleGroup tipoProductoGroup;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField txtPrecio;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private VBox boxCurso;
    @FXML
    private VBox boxServicio;
    @FXML
    private TextField txtDuracionCurso;
    @FXML
    private TextField txtNivelDificultad;
    @FXML
    private TextField txtCertificacion;
    @FXML
    private TextField txtDuracionServicio;
    @FXML
    private TextField txtUbicacion;
    @FXML
    private TextField txtModalidad;
    @FXML
    private Label lblConfirmacion;

    // Servicios inyectados.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    // Tipo de producto predeterminado (si viene de otra pantalla).
    private String tipoPredeterminado;

    // Constructor con inyección de dependencias.
    public SolicitudProductoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    // Inicialización del formulario.
    @FXML
    public void initialize() {
        rbCurso.setDisable(false);
        rbServicio.setDisable(false);

        // Cambiar visibilidad de paneles según el tipo de producto seleccionado.
        tipoProductoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == rbCurso) {
                boxCurso.setVisible(true);
                boxCurso.setManaged(true);
                boxServicio.setVisible(false);
                boxServicio.setManaged(false);
            } else if (newVal == rbServicio) {
                boxServicio.setVisible(true);
                boxServicio.setManaged(true);
                boxCurso.setVisible(false);
                boxCurso.setManaged(false);
            }
        });

        // Cargar categorías desde la base de datos.
        try {
            List<Categoria> categorias = servicioCa.listarCategorias();
            if (categorias != null && !categorias.isEmpty()) {
                for (Categoria cat : categorias) {
                    cmbCategoria.getItems().add(cat.getNombre());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }

        // Seleccionar tipo predeterminado si viene de la pantalla anterior.
        configurarTipo();
    }

    // Acción al enviar solicitud.
    @FXML
    public void onEnviarSolicitud(ActionEvent event) {
        Usuario usuarioActual = SesionActual.getUsuarioActual();
        if (usuarioActual == null) {
            mostrarAlerta("Error", "Debes iniciar sesión antes de enviar una solicitud.");
            return;
        }

        // Validar campos obligatorios.
        if (txtTitulo.getText().isEmpty() || txtDescripcion.getText().isEmpty() ||
                txtPrecio.getText().isEmpty() || cmbCategoria.getValue() == null) {
            mostrarAlerta("Campos vacíos", "Por favor completa todos los campos obligatorios.");
            return;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            Categoria categoria = servicioCa.buscarPorNombre(cmbCategoria.getValue());
            if (categoria == null) {
                mostrarAlerta("Error", "La categoría seleccionada no existe en el sistema.");
                return;
            }

            Producto nuevoProducto;

            // Crear producto según el tipo seleccionado.
            if (rbCurso.isSelected()) {
                int duracion = Integer.parseInt(txtDuracionCurso.getText());
                String nivel = txtNivelDificultad.getText();
                String certificacion = txtCertificacion.getText();

                nuevoProducto = new Curso(0, txtTitulo.getText(), txtDescripcion.getText(), precio,
                        usuarioActual, categoria, null, duracion, nivel, certificacion);

            } else if (rbServicio.isSelected()) {
                int duracion = Integer.parseInt(txtDuracionServicio.getText());
                String ubicacion = txtUbicacion.getText();
                String modalidad = txtModalidad.getText();

                nuevoProducto = new Servicio(0, txtTitulo.getText(), txtDescripcion.getText(), precio,
                        usuarioActual, categoria, duracion, ubicacion, modalidad);
            } else {
                mostrarAlerta("Error", "Selecciona un tipo de producto (Curso o Servicio).");
                return;
            }

            // Guardar el producto antes de crear la solicitud
            boolean productoGuardado = servicioP.crearProducto(nuevoProducto);
            if (!productoGuardado || nuevoProducto.getIdProducto() == 0) {
                mostrarAlerta("Error", "No se pudo registrar el producto asociado.");
                return;
            }

            // Crear la solicitud asociada.
            Solicitud solicitud = new Solicitud();
            solicitud.setSolicitante(usuarioActual);
            solicitud.setEmprendedor(usuarioActual);
            solicitud.setProductoAsociado(nuevoProducto);
            solicitud.setReclutador(null);
            solicitud.setEstado("PENDIENTE");
            solicitud.setMensaje("Solicitud para el producto: " + nuevoProducto.getTitulo());

            servicioS.crearSolicitud(solicitud);

            mostrarAlerta("Éxito", "Solicitud enviada correctamente. Espera aprobación.");
            limpiarFormulario();
            volverAlPanelEmprendedor();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Verifica los valores numéricos (precio o duración).");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo enviar la solicitud: " + e.getMessage());
        }
    }

    // Limpiar formulario después de enviar solicitud.
    private void limpiarFormulario() {
        txtTitulo.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        cmbCategoria.setValue(null);
        rbCurso.setSelected(false);
        rbServicio.setSelected(false);
        boxCurso.setVisible(false);
        boxServicio.setVisible(false);
        lblConfirmacion.setText("");
    }

    // Volver al panel del emprendedor.
    @FXML
    public void onVolver(ActionEvent actionEvent) {
        volverAlPanelEmprendedor();
    }

    // Navegar de vuelta al panel del emprendedor.
    private void volverAlPanelEmprendedor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/emprendedor.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Panel Emprendedor");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver al panel del emprendedor.");
        }
    }

    // Mostrar alertas de información.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Setter para tipo predeterminado.
    public void setTipoPredeterminado(String tipo) {
        this.tipoPredeterminado = tipo;
    }

    // Configurar el tipo de producto seleccionado al iniciar.
    public void configurarTipo() {
        if ("curso".equalsIgnoreCase(tipoPredeterminado)) {
            rbCurso.setSelected(true);
        } else if ("servicio".equalsIgnoreCase(tipoPredeterminado)) {
            rbServicio.setSelected(true);
        }
    }
}
