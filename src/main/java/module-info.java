module fis.jave.emkauri {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;

    exports fis.jave.emkauri;

    opens controladores to javafx.fxml;
}
