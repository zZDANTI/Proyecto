package org.practica.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;


public class Login extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));

        //Crea una nueva escena (Scene) utilizando el nodo raíz (root) que se obtuvo al cargar el archivo FXML.
        Scene scene = new Scene(root);

        //Dependiendo lo que pongas en StageStyle cambia la ventana
        stage.initStyle(StageStyle.DECORATED);

        //Configuración para que la ventana no sea redimensionable (no se puede maximizar)
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}