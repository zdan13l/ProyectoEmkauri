package controladores;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Material;
import servicio.SMaterial;

public class MaterialesController {

    // Simulamos que venimos de un curso con id = 1 (ajusta según navegación real)
    private int idCursoActual = 1;

    @FXML private TextField txtTitulo;
    @FXML private TextField txtUrl;
    @FXML private ChoiceBox<String> cbTipo;
    @FXML private ListView<Material> listView;
    @FXML private Button btnSubir;
    @FXML private Button btnEliminar;

    private final SMaterial sMaterial = SMaterial.getInstance();

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList("LINK", "PDF", "VIDEO", "OTRO"));
        cbTipo.getSelectionModel().select("LINK");
        refrescarLista();
    }

    @FXML
    public void onSubir(ActionEvent e) {
        try {
            String titulo = txtTitulo.getText();
            String url = txtUrl.getText();
            String tipo = cbTipo.getValue();

            sMaterial.subirMaterial(idCursoActual, titulo, url, tipo);
            txtTitulo.clear();
            txtUrl.clear();
            cbTipo.getSelectionModel().select("LINK");
            refrescarLista();
            showInfo("Material cargado correctamente.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void onEliminar(ActionEvent e) {
        Material sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selecciona un material.");
            return;
        }
        boolean ok = sMaterial.eliminar(idCursoActual, sel.getIdMaterial());
        if (ok) {
            refrescarLista();
            showInfo("Material eliminado.");
        } else {
            showError("No se pudo eliminar.");
        }
    }

    private void refrescarLista() {
        listView.setItems(FXCollections.observableArrayList(sMaterial.listarPorCurso(idCursoActual)));
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    // Permite que otra pantalla pase el idCurso real
    public void setIdCurso(int idCurso) {
        this.idCursoActual = idCurso;
        refrescarLista();
    }
}