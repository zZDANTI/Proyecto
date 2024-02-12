package org.practica.proyecto.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.practica.proyecto.controllers.LoginController;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException {

        LoginController loginController = new LoginController();

        File archivo = new File("TOKEN_USUARIO.txt");
        if (archivo.exists()) {

           loginController.comprobarUser();

        } else {
            loginController.login();
        }


    }
}