package org.practica.proyecto.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.practica.proyecto.controllers.LoginController;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        /*
        ALTER TABLE `alumno`
        ADD COLUMN `FOTO_PERFIL` MEDIUMBLOB;

        SET GLOBAL max_allowed_packet=16777216; -- 16 MB en bytes

        ALTER TABLE alumno MODIFY COLUMN foto_perfil MEDIUMBLOB;

        ALTER TABLE `profesor`
        ADD COLUMN `contrasenya` VARCHAR(255);

        ALTER TABLE `profesor`
        ADD COLUMN `admin` BOOLEAN DEFAULT FALSE;

        a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3


         */

        stage = new Stage();
        Parent parentLogin = FXMLLoader.load(Objects.requireNonNull(LoginController.class.getResource("/org/practica/proyecto/login-view.fxml")));

        //Crea una nueva escena (Scene) utilizando el nodo raíz (root) que se obtuvo al cargar el archivo FXML.
        Scene login = new Scene(parentLogin);

        login.getStylesheets().add(Objects.requireNonNull(LoginController.class.getResource("/org/practica/proyecto/css/style.css")).toExternalForm());


        //Dependiendo lo que pongas en StageStyle cambia la ventana
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(login);
        stage.show();

    }
}