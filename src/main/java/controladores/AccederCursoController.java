package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modelo.Material;
import modelo.Producto;
import modelo.ProgresoMaterial;
import modelo.Usuario;
import servicio.ISMaterial;
import servicio.ISProgresoMaterial;

import java.util.List;

public class AccederCursoController {

    // ELEMENTOS FXML
    @FXML private Label lblTituloCurso;
    @FXML private VBox contenedorMateriales;
    @FXML private ProgressBar barraProgreso;
    @FXML private Label lblProgreso;
    @FXML private Button btnCertificado;

    // SERVICIOS (inyectados después)
    private ISMaterial servicioMaterial;
    private ISProgresoMaterial servicioProgreso;

    // GESTOR DE PANTALLAS
    private GestorPantallas gestorPantallas;

    // CURSO SELECCIONADO
    private Producto cursoActual;

    public AccederCursoController(ISMaterial servicioMaterial, ISProgresoMaterial servicioProgreso, GestorPantallas gestorPantallas) {
        this.servicioMaterial = servicioMaterial;
        this.servicioProgreso = servicioProgreso;
        this.gestorPantallas = gestorPantallas;
    }

    // ============================================================
    //     MÉTODOS DE CONFIGURACIÓN DESDE LA OTRA PANTALLA
    // ============================================================
    public void setCurso(Producto curso) throws Exception {
        this.cursoActual = curso;

        if (lblTituloCurso != null) {
            lblTituloCurso.setText(curso.getTitulo());
        }

        cargarMateriales();
    }

    // ============================================================
    //                 CARGA DE MATERIALES
    // ============================================================
    private void cargarMateriales() throws Exception {

        if (servicioMaterial == null || servicioProgreso == null) {
            System.err.println("❌ Servicios no inicializados en AccederCursoController");
            return;
        }

        contenedorMateriales.getChildren().clear();

        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            gestorPantallas.mostrarError("Sesión", "No hay usuario autenticado.");
            return;
        }

        List<Material> materiales = servicioMaterial.listarPorCurso(cursoActual.getIdProducto());

        for (Material m : materiales) {
            agregarMaterialUI(m, actual);
        }

        actualizarProgreso();
    }

    // ============================================================
    //             DIBUJA CADA MATERIAL EN EL SCROLLPANE
    // ============================================================
    private void agregarMaterialUI(Material material, Usuario usuario) {

        HBox fila = new HBox(12);
        fila.getStyleClass().add("material-item");

        Label lbl = new Label(material.getTitulo());
        lbl.getStyleClass().add("label-material");

        // OBTENER O CREAR EL PROGRESO
        ProgresoMaterial progreso = servicioProgreso.obtener(usuario.getIdUsuario(), material.getIdMaterial());

        if (progreso == null) {
            progreso = servicioProgreso.crear(usuario, material);
        }

        // BOTÓN DE MARCAR / DESMARCAR
        Button btnToggle = new Button(
                progreso.isVisto() ? "Completado ✔" : "Marcar como visto"
        );

        btnToggle.getStyleClass().add("btn-progreso");

        ProgresoMaterial finalProgreso = progreso;

        btnToggle.setOnAction(e -> {
            boolean nuevoEstado = !finalProgreso.isVisto();
            finalProgreso.setVisto(nuevoEstado);

            servicioProgreso.actualizar(finalProgreso);

            btnToggle.setText(nuevoEstado ? "Completado ✔" : "Marcar como visto");

            try {
                actualizarProgreso();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        fila.getChildren().addAll(lbl, btnToggle);
        contenedorMateriales.getChildren().add(fila);
    }

    // ============================================================
    //                    ACTUALIZAR PROGRESO
    // ============================================================
    private void actualizarProgreso() throws Exception {

        Usuario actual = SesionActual.getUsuarioActual();
        List<Material> materiales = servicioMaterial.listarPorCurso(cursoActual.getIdProducto());

        int total = materiales.size();
        int vistos = servicioProgreso.contarVistos(actual.getIdUsuario(), cursoActual.getIdProducto());

        if (total == 0) {
            barraProgreso.setProgress(0);
            lblProgreso.setText("0% completado");
            btnCertificado.setDisable(true);
            return;
        }

        double fraccion = (double) vistos / total;
        barraProgreso.setProgress(fraccion);

        int porcentaje = (int) (fraccion * 100);
        lblProgreso.setText(porcentaje + "% completado");

        // HABILITAR CERTIFICADO SOLO SI TODO ESTÁ COMPLETO
        btnCertificado.setDisable(porcentaje < 100);
    }

    // ============================================================
    //                     BOTÓN DE CERTIFICADO
    // ============================================================
    @FXML
    private void onObtenerCertificado() {
        gestorPantallas.mostrarExito(
                "Certificado",
                "¡Felicidades! Has completado el curso.\nTu certificado estará disponible pronto."
        );
    }
}
