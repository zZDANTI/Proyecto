module org.practica.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens org.practica.proyecto to javafx.fxml;
    exports org.practica.proyecto;
}