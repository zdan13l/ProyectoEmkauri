package controladores;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Curso;
import modelo.Material;
import servicio.ISCurso;

import java.util.List;

public class AccesoCursoController implements IControlador {

    // Inyección tardía (Scene Builder requiere ctor sin args)
    private ISCurso sCurso;
    private Integer idEmprendedor; // quién está gestionando

    // --- UI: Cursos
    @FXML private TableView<Curso> tblCursos;
    @FXML private TableColumn<Curso, Number> colCId;
    @FXML private TableColumn<Curso, String> colCTitulo;
    @FXML private TableColumn<Curso, String> colCCategoria;
    @FXML private TableColumn<Curso, String> colCPrecio;

    // --- UI: Materiales
    @FXML private TableView<Material> tblMateriales;
    @FXML private TableColumn<Material, Number> colMId;
    @FXML private TableColumn<Material, String> colMTitulo;
    @FXML private TableColumn<Material, String> colMTipo;
    @FXML private TableColumn<Material, String> colMUrl;

    @FXML private TextField txtMTitulo, txtMUrl;
    @FXML private ComboBox<String> cbMTipo;

    private final ObservableList<Curso> cursos = FXCollections.observableArrayList();
    private final ObservableList<Material> materiales = FXCollections.observableArrayList();

    public AccesoCursoController() { /* ctor vacío para Scene Builder */ }

    @Override
    public void setServicios(Object... servicios) {
        // Esperamos: [0]=ISCurso, [1]=Integer idEmprendedor
        if (servicios != null && servicios.length >= 2 && servicios[0] instanceof ISCurso && servicios[1] instanceof Integer) {
            this.sCurso = (ISCurso) servicios[0];
            this.idEmprendedor = (Integer) servicios[1];
            refrescarCursosSeguro();
        }
    }

    @FXML
    public void initialize() {
        // Config tabla cursos
        if (colCId != null) colCId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdProducto()));
        if (colCTitulo != null) colCTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        if (colCCategoria != null) colCCategoria.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCategoria() == null ? "" : c.getValue().getCategoria().getNombre()
        ));
        if (colCPrecio != null) colCPrecio.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPrecio())));
        if (tblCursos != null) {
            tblCursos.setItems(cursos);
            tblCursos.getSelectionModel().selectedItemProperty().addListener((obs, old, cur) -> {
                if (cur != null) cargarMaterialesDeCurso(cur.getIdProducto());
            });
        }

        // Config tabla materiales
        if (colMId != null) colMId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdMaterial()));
        if (colMTitulo != null) colMTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        if (colMTipo != null) colMTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        if (colMUrl != null) colMUrl.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUrl()));
        if (tblMateriales != null) tblMateriales.setItems(materiales);

        // Combo tipo
        if (cbMTipo != null) cbMTipo.setItems(FXCollections.observableArrayList("VIDEO", "PDF", "EXAMEN", "LINK", "OTRO"));

        // Si ya inyectaron servicio, refrescamos
        refrescarCursosSeguro();
    }

    // ======= Cursos =======
    @FXML public void onRefrescarCursos() { refrescarCursos(); }

    private void refrescarCursosSeguro() {
        if (sCurso != null && idEmprendedor != null) refrescarCursos();
    }

    private void refrescarCursos() {
        try {
            List<Curso> lista = sCurso.listarCursosDeEmprendedor(idEmprendedor);
            cursos.setAll(lista);
            materiales.clear();
        } catch (Exception e) { error(e); }
    }

    // ======= Materiales =======
    private void cargarMaterialesDeCurso(int idCurso) {
        try {
            materiales.setAll(sCurso.listarMateriales(idCurso));
        } catch (Exception e) { error(e); }
    }

    @FXML public void onNuevoMaterial() {
        if (txtMTitulo != null) txtMTitulo.clear();
        if (cbMTipo != null) cbMTipo.getSelectionModel().clearSelection();
        if (txtMUrl != null) txtMUrl.clear();
        if (tblMateriales != null) tblMateriales.getSelectionModel().clearSelection();
    }

    @FXML public void onAgregarMaterial() {
        Curso selCurso = tblCursos == null ? null : tblCursos.getSelectionModel().getSelectedItem();
        if (selCurso == null) { warn("Selecciona un curso"); return; }
        try {
            Material m = fromFormMaterial(0);
            m = sCurso.crearMaterial(selCurso.getIdProducto(), m);
            materiales.add(0, m);
            info("Material agregado");
        } catch (Exception e) { error(e); }
    }

    @FXML public void onActualizarMaterial() {
        Material sel = tblMateriales == null ? null : tblMateriales.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Selecciona un material"); return; }
        try {
            Material upd = fromFormMaterial(sel.getIdMaterial());
            sCurso.actualizarMaterial(upd);
            cargarMaterialesDeCurso(tblCursos.getSelectionModel().getSelectedItem().getIdProducto());
            info("Material actualizado");
        } catch (Exception e) { error(e); }
    }

    @FXML public void onEliminarMaterial() {
        Material sel = tblMateriales == null ? null : tblMateriales.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Selecciona un material"); return; }
        try {
            sCurso.eliminarMaterial(sel.getIdMaterial());
            materiales.remove(sel);
            info("Material eliminado");
        } catch (Exception e) { error(e); }
    }

    @FXML public void onRefrescarMateriales() {
        Curso sel = tblCursos == null ? null : tblCursos.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Selecciona un curso"); return; }
        cargarMaterialesDeCurso(sel.getIdProducto());
    }

    // ======= Helpers =======
    private Material fromFormMaterial(int idMaterial) {
        String titulo = txtMTitulo.getText().trim();
        String tipo = cbMTipo.getValue();
        String url = txtMUrl.getText().trim();
        if (titulo.isEmpty() || tipo == null || tipo.isEmpty() || url.isEmpty()) {
            throw new IllegalArgumentException("Completa Título, Tipo y URL.");
        }
        Material m = new Material();
        m.setIdMaterial(idMaterial);
        m.setTitulo(titulo);
        m.setTipo(tipo);
        m.setUrl(url);
        return m;
    }

    private void info(String m){ new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    private void warn(String m){ new Alert(Alert.AlertType.WARNING, m).showAndWait(); }
    private void error(Exception e){ new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait(); e.printStackTrace(); }
}
