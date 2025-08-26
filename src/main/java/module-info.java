module fis.jave.emkauri {
    requires javafx.controls;
    requires javafx.fxml;


    opens fis.jave.emkauri to javafx.fxml;
    exports fis.jave.emkauri;
}