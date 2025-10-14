module fis.jave.emkauri {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;
    requires java.desktop;

    opens controladores to javafx.fxml;
    opens modelo to javafx.base;

    exports fis.jave.emkauri;
    exports controladores;
    exports modelo;
}
