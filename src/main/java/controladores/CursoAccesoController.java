package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import modelo.Curso;
import modelo.Usuario;
import servicio.SCompra;
import servicio.SCurso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CursoAccesoController {

    @FXML private ComboBox<Curso> cbCurso;
    @FXML private Label lblUsuario, lblEstado, lblCategoria, lblPrecio, lblAcceso;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnComprar, btnAcceder;

    private final SCurso sCurso = new SCurso();
    private final SCompra sCompra = new SCompra();
    private Usuario usuarioActual;

    private final NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    public void setUsuarioActual(Usuario u) {
        this.usuarioActual = u;
        if (lblUsuario != null) lblUsuario.setText(u != null && u.getEmail() != null ? u.getEmail() : "Invitado");
        if (cbCurso != null) onCursoSeleccionado(cbCurso.getValue());
    }

    @FXML
    public void initialize() {
        if (cbCurso == null) return;

        cbCurso.setConverter(new StringConverter<>() {
            @Override public String toString(Curso c) { return c == null ? "" : safe(c.getNombre()); }
            @Override public Curso fromString(String s) { return null; }
        });
        cbCurso.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Curso c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : safe(c.getNombre()));
            }
        });

        try {
            List<Curso> activos = sCurso.listarCursosActivos();
            if (activos != null && !activos.isEmpty()) cbCurso.getItems().setAll(activos);
        } catch (Exception ignored) {}

        cbCurso.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> onCursoSeleccionado(b));
        lblUsuario.setText(usuarioActual != null && usuarioActual.getEmail() != null ? usuarioActual.getEmail() : "Invitado");
        actualizarBotones(null, false);
    }

    private void onCursoSeleccionado(Curso curso) {
        if (curso == null) {
            limpiarDetalle();
            return;
        }
        lblEstado.setText(safe(curso.getEstado()));
        lblCategoria.setText(curso.getCategoria() != null ? safe(curso.getCategoria().getNombre()) : "—");
        try { lblPrecio.setText(money.format(curso.getPrecio())); }
        catch (Exception e) { lblPrecio.setText(String.valueOf(curso.getPrecio())); }
        txtDescripcion.setText(safe(curso.getDescripcion()));

        boolean tieneAcceso = false;
        try {
            tieneAcceso = (usuarioActual != null) && sCompra.tieneAcceso(usuarioActual.getIdUsuario(), curso.getIdCurso());
        } catch (Exception ignored) {}

        lblAcceso.setText(tieneAcceso ? "Acceso concedido" : "Sin acceso");
        actualizarBotones(curso, tieneAcceso);
    }

    private void actualizarBotones(Curso curso, boolean tieneAcceso) {
        boolean seleccionado = curso != null;
        btnAcceder.setDisable(!seleccionado || !tieneAcceso);
        btnComprar.setDisable(!seleccionado || tieneAcceso || usuarioActual == null);
    }

    private void limpiarDetalle() {
        lblEstado.setText("—");
        lblCategoria.setText("—");
        lblPrecio.setText("—");
        txtDescripcion.clear();
        lblAcceso.setText("—");
        actualizarBotones(null, false);
    }

    @FXML
    private void onComprar() {
        Curso curso = cbCurso.getValue();
        if (curso == null || usuarioActual == null) return;
        try {
            boolean aprobado = sCompra.comprarCursoConPagoSimulado(
                    usuarioActual.getIdUsuario(), curso.getIdCurso(), curso.getPrecio());
            if (aprobado) {
                info("Compra exitosa", "Pago APROBADO. Ya tienes acceso al curso.");
                onCursoSeleccionado(curso);
            } else {
                warn("Pago rechazado", "No fue posible aprobar el pago.");
            }
        } catch (Exception e) {
            error("Error en compra", e.getMessage());
        }
    }

    @FXML
    private void onAcceder() {
        Curso curso = cbCurso.getValue();
        if (curso == null) return;
        info("Acceso", "Aquí se abriría la vista de contenido del curso.");
    }

    @FXML
    private void onVolver() {
        // Implementa tu navegación real si usas un Navigator.
    }

    private static String safe(String s) { return (s == null || s.isBlank()) ? "—" : s; }
    private void info(String t, String m){ new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); }
    private void warn(String t, String m){ new Alert(Alert.AlertType.WARNING, m).showAndWait(); }
    private void error(String t, String m){ new Alert(Alert.AlertType.ERROR, m).showAndWait(); }
}
