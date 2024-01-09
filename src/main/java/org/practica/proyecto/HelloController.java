package org.practica.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class LoginController {

    @FXML
    private JFXButton loginButton;

    // ...

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("otra-pagina.fxml"));
            Parent root = loader.load();

            // Obtén el Stage actual
            Stage currentStage = (Stage) loginButton.getScene().getWindow();

            // Crea una nueva ventana (Stage) para la nueva vista
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();

            // Cierra la ventana actual (Login)
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Resto de tu código...
}
