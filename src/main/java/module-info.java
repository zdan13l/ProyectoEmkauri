module fis.jave.emkauri {
    requires javafx.controls;
    requires javafx.fxml;

    exports fis.jave.emkauri;

    opens controladores to javafx.fxml;
}
