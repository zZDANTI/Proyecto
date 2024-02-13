package org.practica.proyecto.views;

import javafx.application.Application;
import javafx.stage.Stage;
import org.practica.proyecto.controllers.LoginController;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException, SQLException {

        /* SET GLOBAL max_allowed_packet=16777216; -- 16 MB en bytes
        * ALTER TABLE `profesor`
          ADD COLUMN `fecha_token` DATETIME;
        * */

        LoginController loginController = new LoginController();
        File archivo = new File("TOKEN_USUARIO.txt");
        if (archivo.exists()) {

           loginController.comprobarUser();

        } else {
            loginController.login();
        }


    }
}