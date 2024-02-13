module org.practica.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires javafx.media;
    requires itextpdf;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires GNAvatarView;
    requires java.sql.rowset;


    opens org.practica.proyecto to javafx.fxml;

    exports org.practica.proyecto.models;
    exports org.practica.proyecto.controllers;
    opens org.practica.proyecto.controllers to javafx.fxml;
    exports org.practica.proyecto.views;
    opens org.practica.proyecto.views to javafx.fxml;
}
