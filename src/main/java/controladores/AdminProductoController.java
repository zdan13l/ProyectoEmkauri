package controladores;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import java.util.List;
import servicio.*;
import modelo.*;

// Controlador para la administración de productos (cursos o servicios).
public class AdminProductoController {

    // Elementos de la interfaz gráfica.
    @FXML private TextField txtTitulo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtDuracionCurso;
    @FXML private TextField txtDuracionServicio;
    @FXML private TextField txtNivel;
    @FXML private TextField txtCertificacion;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtModalidad;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;
    @FXML private Button btnAgregarMaterial;
    @FXML private Button btnEliminarMaterial;
    @FXML private TableView<Material> tablaMateriales;
    @FXML private TableColumn<Material, String> colTituloMat;
    @FXML private TableColumn<Material, String> colTipoMat;
    @FXML private TableColumn<Material, String> colUrlMat;

    // Servicios para la lógica y gestor de navegación.
    private final ISProducto servicioP;
    private final ISMaterial servicioM;
    private final GestorPantallas gestorPantallas;

    // Producto (curso o servicio) actualmente cargado.
    private Producto productoSeleccionado;

    // Lista observable de materiales asociados al curso.
    private final ObservableList<Material> materiales = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public AdminProductoController(ISProducto servicioP, ISMaterial servicioM, GestorPantallas gestorPantallas) {
        this.servicioP = servicioP;
        this.servicioM = servicioM;
        this.gestorPantallas = gestorPantallas;
    }

    // Carga el producto en la interfaz para su edición.
    public void setProducto(Producto producto) throws Exception {
        this.productoSeleccionado = producto;
        if (producto == null) {
            gestorPantallas.mostrarError("Error", "No se pudo cargar el producto.");
            return;
        }

        // Datos comunes.
        txtTitulo.setText(producto.getTitulo());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));

        // Especificidades para un curso.
        if (producto instanceof Curso curso) {
            txtDuracionCurso.setText(String.valueOf(curso.getDuracionCurso()));
            txtNivel.setText(curso.getNivelDificultad());
            txtCertificacion.setText(curso.getCertificacion());

            // Cargar materiales desde la base de datos.
            List<Material> materialesBD = servicioM.listarPorCurso(curso.getIdProducto());
            materiales.setAll(materialesBD);
        }

        // Especificidades para un servicio.
        if (producto instanceof Servicio serv) {
            txtDuracionServicio.setText(String.valueOf(serv.getDuracionServicio()));
            txtUbicacion.setText(serv.getUbicacion());
            txtModalidad.setText(serv.getModalidad());
        }
    }

    // Inicialización del controlador.
    @FXML
    public void initialize() {
        if (tablaMateriales != null) {
            colTituloMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
            colTipoMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
            colUrlMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUrl()));
            tablaMateriales.setItems(materiales);
        }
    }

    // Guardar los cambios realizados al producto.
    @FXML
    private void onGuardar() {
        if (productoSeleccionado == null) {
            gestorPantallas.mostrarError("Error", "No hay producto cargado.");
            return;
        }

        // Validar campos antes de guardar.
        if (!validarCampos()) { return; }

        try {
            // Datos comunes
            productoSeleccionado.setTitulo(txtTitulo.getText());
            productoSeleccionado.setDescripcion(txtDescripcion.getText());
            productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText()));

            // Datos específicos para Curso.
            if (productoSeleccionado instanceof Curso curso) {
                curso.setDuracionCurso(Integer.parseInt(txtDuracionCurso.getText()));
                curso.setNivelDificultad(txtNivel.getText());
                curso.setCertificacion(txtCertificacion.getText());

                // Obtener materiales originales de BD.
                List<Material> originales = servicioM.listarPorCurso(curso.getIdProducto());

                // Eliminar materiales que ya no existen.
                for (Material viejo : originales) {
                    boolean aunExiste = materiales.stream().anyMatch(m -> m.getIdMaterial() == viejo.getIdMaterial());
                    if (!aunExiste) { servicioM.eliminar(viejo.getIdMaterial()); }
                }

                // Insertar o actualizar materiales.
                for (Material material : materiales) {
                    if (material.getIdMaterial() == 0) {
                        servicioM.insertar(material, curso.getIdProducto());
                    } else {
                        servicioM.modificar(material);
                    }
                }
            }

            // Datos específicos para Servicio.
            if (productoSeleccionado instanceof Servicio serv) {
                serv.setDuracionServicio(Integer.parseInt(txtDuracionServicio.getText()));
                serv.setUbicacion(txtUbicacion.getText());
                serv.setModalidad(txtModalidad.getText());
            }

            // Actualizar producto en la base de datos.
            servicioP.actualizarProducto(productoSeleccionado);
            gestorPantallas.mostrarExito("Guardado", "Los cambios fueron actualizados correctamente.");
            onVolver();
        } catch (Exception e) {
            gestorPantallas.mostrarError("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    // Volver a la pantalla de productos del emprendedor.
    @FXML
    private void onVolver() { gestorPantallas.irProductosEmprendedor(); }

    // Agregar nuevo material a la lista.
    @FXML
    private void onAgregarMaterial() {
        Material nuevo = gestorPantallas.mostrarDialogoMaterial();
        if (nuevo != null) { materiales.add(nuevo); }
    }

    // Eliminar material seleccionado de la lista.
    @FXML
    private void onEliminarMaterial() {
        Material seleccionado = tablaMateriales.getSelectionModel().getSelectedItem();
        if (seleccionado != null) { materiales.remove(seleccionado); }
    }

    // Válida los campos del formulario.
    private boolean validarCampos() {
        // Título del producto.
        if (!txtTitulo.getText().matches("[A-Za-zÁÉÍÓÚáéíóúñÑ0-9 ]+")) {
            gestorPantallas.mostrarAlerta("Título inválido", "El título solo puede contener letras, números y espacios.");
            return false;
        }

        // Descripción del producto.
        if (txtDescripcion.getText().length() < 10) {
            gestorPantallas.mostrarAlerta("Descripción muy corta", "La descripción debe tener mínimo 10 caracteres.");
            return false;
        }

        // Precio del producto.
        if (!txtPrecio.getText().matches("\\d+(\\.\\d{1,2})?")) {
            gestorPantallas.mostrarAlerta("Precio inválido", "El precio debe ser un número válido.");
            return false;
        }

        // Validaciones específicas para Curso.
        if (productoSeleccionado instanceof Curso) {
            // Duración del curso.
            if (!txtDuracionCurso.getText().matches("\\d+")) {
                gestorPantallas.mostrarAlerta("Duración inválida", "La duración del curso debe ser un número entero.");
                return false;
            }

            // Nivel de dificultad.
            if (!txtNivel.getText().matches("[A-Za-zÁÉÍÓÚáéíóúñÑ ]+")) {
                gestorPantallas.mostrarAlerta("Nivel inválido", "El nivel solo puede contener letras.");
                return false;
            }
        }

        // Validaciones específicas para Servicio.
        if (productoSeleccionado instanceof Servicio) {
            // Duración del servicio.
            if (!txtDuracionServicio.getText().matches("\\d+") || Integer.parseInt(txtDuracionServicio.getText()) <= 0) {
                gestorPantallas.mostrarAlerta("Duración inválida", "La duración del servicio debe ser un número mayor a cero.");
                return false;
            }
        }
        return true;
    }
}
