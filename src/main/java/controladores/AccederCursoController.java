package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.*;
import servicio.*;

import java.util.List;

// Controlador para la pantalla de acceso a un curso por parte del cliente.
public class AccederCursoController {

    // Elementos de la interfaz gráfica.
    @FXML private Label lblTituloCurso;
    @FXML private VBox contenedorMateriales;
    @FXML private ProgressBar barraProgreso;
    @FXML private Label lblProgreso;
    @FXML private Button btnCertificado;
    @FXML private Button btnVolver;

    // Servicios para la lógica y gestor de navegación.
    private final ISMaterial servicioMaterial;
    private final ISProgresoMaterial servicioProgreso;
    private final GestorPantallas gestorPantallas;

    // Curso actualmente accedido.
    private Producto cursoActual;

    // Constructor que recibe los servicios necesarios.
    public AccederCursoController(ISMaterial servicioMaterial, ISProgresoMaterial servicioProgreso, GestorPantallas gestorPantallas) {
        this.servicioMaterial = servicioMaterial;
        this.servicioProgreso = servicioProgreso;
        this.gestorPantallas = gestorPantallas;
    }

    // Setea el curso actual y carga sus materiales.
    public void setCurso(Producto curso) throws Exception {
        this.cursoActual = curso;
        if (lblTituloCurso != null) {
            lblTituloCurso.setText(curso.getTitulo());
        }
        cargarMateriales();
    }

    // Volver a la pantalla de productos del cliente.
    @FXML
    private void onVolver(ActionEvent event) { gestorPantallas.irProductosCliente(); }

    // Obtener certificado al completar el curso.
    @FXML
    private void onObtenerCertificado() {
        gestorPantallas.mostrarExito("Certificado", "¡Felicidades! Has completado el curso.\nTu certificado estará disponible pronto.");
    }

    // Carga los materiales del curso y los muestra en la interfaz.
    private void cargarMateriales() {
        if (servicioMaterial == null || servicioProgreso == null) {
            System.err.println("❌ Servicios no inicializados en AccederCursoController");
            return;
        }
        contenedorMateriales.getChildren().clear();

        // Obtener el usuario actual.
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            gestorPantallas.mostrarError("Sesión", "No hay usuario autenticado.");
            return;
        }

        // Listar y agregar materiales.
        List<Material> materiales = servicioMaterial.listarPorCurso(cursoActual.getIdProducto());
        for (Material material : materiales) {
            agregarMaterial(material, actual);
        }
        actualizarProgreso();
    }

    // Agrega un material a la interfaz con su respectivo botón de progreso.
    private void agregarMaterial(Material material, Usuario usuario) {
        // Tarjeta del material.
        HBox card = new HBox(16);
        card.getStyleClass().add("card-box");
        card.setFillHeight(true);

        // Contenido del material.
        VBox info = new VBox(6);
        Label titulo = new Label("📘 " + material.getTitulo());
        titulo.getStyleClass().add("card-title");
        Label tipo = new Label("Tipo: " + material.getTipo());
        tipo.getStyleClass().add("card-text");
        Label url = new Label("URL: " + material.getUrl());
        url.getStyleClass().add("card-text");
        url.setWrapText(true);
        url.setMaxWidth(350);
        info.getChildren().addAll(titulo, tipo, url);

        // Obtener o crear progreso.
        ProgresoMaterial progreso = servicioProgreso.obtener(usuario.getIdUsuario(), material.getIdMaterial());
        if (progreso == null) {
            progreso = servicioProgreso.crear(usuario, material);
        }

        // Botón para marcar como visto/no visto.
        Button btnToggle;
        if (progreso.isVisto()) {
            btnToggle = new Button("Pendiente por revisar");
            btnToggle.getStyleClass().add("btn-warning");
        } else {
            btnToggle = new Button("Marcar como visto");
            btnToggle.getStyleClass().add("btn-primary");
        }
        ProgresoMaterial finalProgreso = progreso;

        btnToggle.setOnAction(e -> {
            boolean nuevoEstado = !finalProgreso.isVisto();
            finalProgreso.setVisto(nuevoEstado);
            servicioProgreso.actualizar(finalProgreso);

            // Actualizar texto y estilo del botón.
            btnToggle.getStyleClass().clear();
            if (nuevoEstado) {
                btnToggle.setText("Pendiente por revisar");
                btnToggle.getStyleClass().add("btn-warning");
            } else {
                btnToggle.setText("Marcar como visto");
                btnToggle.getStyleClass().add("btn-primary");
            }
            actualizarProgreso();
        });
        // Contenedor del botón alineado a la derecha.
        VBox contBoton = new VBox(btnToggle);
        contBoton.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        card.getChildren().addAll(info, spacer, contBoton);

        // Agregar tarjeta al contenedor principal.
        contenedorMateriales.getChildren().add(card);
    }

    // Actualiza la barra de progreso y el estado del botón de certificado.
    private void actualizarProgreso() {
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

        // Habilitar el botón de certificado solo si se ha completado el 100%.
        btnCertificado.setDisable(porcentaje < 100);
    }
}
