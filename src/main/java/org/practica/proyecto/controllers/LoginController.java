package org.practica.proyecto.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.practica.proyecto.models.User;

public class LoginController {

    @FXML
    private CustomTextField textUser;

    @FXML
    private CustomPasswordField textPass;

    @FXML
    private Button loginButton;

    @FXML
    private void loginUser(ActionEvent event) {
        // Obtener los datos del usuario y la contraseña
        String usuario = textUser.getText();
        String contrasenya = textPass.getText();

        // Realizar la verificación del usuario en la base de datos
        User userModel = new User(usuario, contrasenya);
        User usuarioVerificado = userModel.checkUser(usuario, contrasenya);

        // Aquí puedes realizar acciones adicionales según el resultado de la verificación
        if (usuarioVerificado != null) {
            // El usuario es válido, puedes navegar a otra vista, mostrar un mensaje, etc.
            System.out.println("Inicio de sesión exitoso");
        } else {
            // El usuario no es válido, puedes mostrar un mensaje de error, etc.
            System.out.println("Inicio de sesión fallido");
        }
    }
}
