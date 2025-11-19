package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import modelo.*;
import servicio.*;
import java.util.List;

// Controlador para gestionar la pantalla de solicitud de productos (cursos o servicios).
public class SolicitudProductoController {

    // Elementos de la interfaz gráfica.
    @FXML private ToggleGroup tipoProductoGroup;
    @FXML private ScrollPane scrollForm;
    @FXML private Button btnVolver;
    @FXML private Button btnEnviarSolicitud;
    @FXML private RadioButton rbCurso;
    @FXML private RadioButton rbServicio;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDuracionCurso;
    @FXML private TextField txtNivelDificultad;
    @FXML private TextField txtCertificacion;
    @FXML private TextField txtDuracionServicio;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtModalidad;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private VBox boxCurso;
    @FXML private VBox boxServicio;
    @FXML private Label lblConfirmacion;

    // Servicios para la lógica y gestor de navegación.
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISSolicitud servicioS;
    private final GestorPantallas gestorPantallas;

    // Tipo de producto predeterminado (si viene de otra pantalla).
    private String tipoPredeterminado;

    // Constructor con inyección de dependencias.
    public SolicitudProductoController(ISProducto servicioP, ISCategoria servicioCa, ISSolicitud servicioS,GestorPantallas gestorPantallas) {
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioS = servicioS;
        this.gestorPantallas = gestorPantallas;
    }

    // Setter para tipo predeterminado.
    public void setTipoPredeterminado(String tipo) { this.tipoPredeterminado = tipo; }

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

        // Cargar categorías.
        try {
            List<Categoria> categorias = servicioCa.listarCategorias();
            if (categorias != null && !categorias.isEmpty()) {
                for (Categoria categoria : categorias) {
                    cmbCategoria.getItems().add(categoria.getNombre());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }

        // Seleccionar tipo predeterminado.
        configurarTipo();
    }

    // Acción al enviar solicitud.
    @FXML
    public void onEnviarSolicitud(ActionEvent event) {
        Usuario usuarioActual = SesionActual.getUsuarioActual();
        if (usuarioActual == null) {
            gestorPantallas.mostrarError("Error", "Debes iniciar sesión antes de enviar una solicitud.");
            return;
        }

        // Validar campos obligatorios.
        if (txtTitulo.getText().isEmpty() || txtDescripcion.getText().isEmpty() ||
                txtPrecio.getText().isEmpty() || cmbCategoria.getValue() == null) {
            gestorPantallas.mostrarAlerta("Campos vacíos", "Por favor completa todos los campos obligatorios.");
            return;
        }

        if (!validarCampos()) { return; }

        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            Categoria categoria = servicioCa.buscarPorNombre(cmbCategoria.getValue());
            if (categoria == null) {
                gestorPantallas.mostrarError("Error", "Categoría no válida.");
                return;
            }

            Producto nuevoProducto;
            // Crear producto según el tipo seleccionado.
            if (rbCurso.isSelected()) {
                int duracion = Integer.parseInt(txtDuracionCurso.getText());
                String nivel = txtNivelDificultad.getText();
                String certificacion = txtCertificacion.getText();

                nuevoProducto = new Curso(0, txtTitulo.getText(), txtDescripcion.getText(), "pendiente", precio,
                        usuarioActual, categoria, null, duracion, nivel, certificacion);
            } else if (rbServicio.isSelected()) {
                int duracion = Integer.parseInt(txtDuracionServicio.getText());
                String ubicacion = txtUbicacion.getText();
                String modalidad = txtModalidad.getText();

                nuevoProducto = new Servicio(0, txtTitulo.getText(), txtDescripcion.getText(), "pendiente", precio,
                        usuarioActual, categoria, duracion, ubicacion, modalidad);
            } else {
                gestorPantallas.mostrarError("Error", "Selecciona un tipo de producto (Curso o Servicio).");
                return;
            }

            // Guardar el producto antes de crear la solicitud.
            boolean productoGuardado = servicioP.crearProducto(nuevoProducto);
            if (!productoGuardado || nuevoProducto.getIdProducto() == 0) {
                gestorPantallas.mostrarAlerta("Error", "No se pudo registrar el producto asociado.");
                return;
            }

            // Crear la solicitud asociada.
            Solicitud solicitud = new Solicitud();
            solicitud.setSolicitante(usuarioActual);
            solicitud.setEmprendedor(usuarioActual);
            solicitud.setProductoAsociado(nuevoProducto);
            solicitud.setReclutador(null);
            solicitud.setEstado("PENDIENTE");
            solicitud.setMensaje(txtDescripcion.getText());

            // Guardar la solicitud.
            servicioS.crearSolicitud(solicitud);

            gestorPantallas.mostrarExito("Éxito", "Solicitud enviada correctamente. Espera aprobación.");
            limpiarFormulario();
            onVolver(new ActionEvent());

        } catch (NumberFormatException e) {
            gestorPantallas.mostrarError("Error", "Verifica los valores numéricos (precio o duración).");
        } catch (Exception e) {
            gestorPantallas.mostrarError("Error", "No se pudo enviar la solicitud: " + e.getMessage());
        }
    }

    // Navegar de vuelta al panel del emprendedor.
    @FXML
    public void onVolver(ActionEvent actionEvent) { gestorPantallas.irEmprendedor(); }

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

    // Configurar el tipo de producto al inicializar.
    private void configurarTipo() {
        if (tipoPredeterminado != null) {
            seleccionarTipo(tipoPredeterminado);
            rbCurso.setDisable(true);
            rbServicio.setDisable(true);
        }
    }

    // Seleccionar el tipo de producto (curso/servicio) en la pantalla de solicitudes.
    public void seleccionarTipo(String tipo) {
        if (tipo.equalsIgnoreCase("curso")) {
            rbCurso.setSelected(true);
        } else if (tipo.equalsIgnoreCase("servicio")) {
            rbServicio.setSelected(true);
        }
    }

    // Validar campos del formulario antes de enviar la solicitud.
    private boolean validarCampos() {
        if (!txtTitulo.getText().matches("[A-Za-zÁÉÍÓÚáéíóúñÑ0-9 ]+")) {
            gestorPantallas.mostrarAlerta("Título inválido", "El título debe contener solo letras, números y espacios.");
            return false;
        }

        if (txtDescripcion.getText().length() < 10) {
            gestorPantallas.mostrarAlerta("Descripción insuficiente", "La descripción debe tener al menos 10 caracteres.");
            return false;
        }

        if (!txtPrecio.getText().matches("\\d+(\\.\\d{1,2})?")) {
            gestorPantallas.mostrarAlerta("Precio inválido", "El precio debe ser un número válido (ej. 40 ó 40.50).");
            return false;
        }

        // Validación específica según tipo
        if (rbCurso.isSelected()) {
            if (!txtDuracionCurso.getText().matches("\\d+")) {
                gestorPantallas.mostrarAlerta("Duración inválida", "La duración del curso debe ser un número entero.");
                return false;
            }

            if (!txtNivelDificultad.getText().matches("[A-Za-zÁÉÍÓÚáéíóúñÑ ]+")) {
                gestorPantallas.mostrarAlerta("Nivel inválido", "El nivel debe contener solo letras.");
                return false;
            }
        }

        if (rbServicio.isSelected()) {
            if (!txtDuracionServicio.getText().matches("\\d+") || Integer.parseInt(txtDuracionServicio.getText()) <= 0) {
                gestorPantallas.mostrarAlerta("Duración inválida", "La duración del servicio debe ser un número entero.");
                return false;
            }
        }

        return true;
    }
}
