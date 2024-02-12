package org.practica.proyecto.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.practica.proyecto.models.Profesor;

import java.io.IOException;

import static org.practica.proyecto.controllers.DashboardController.reproducirSonido;

public class LoginController {


    public TextField errorNoti;

    @FXML
    private CustomTextField textUser;

    @FXML
    private CustomPasswordField textPass;

    private Stage stage;


    @FXML
    public boolean comprobarUser() {
        // Obtener los datos del usuario y la contraseña
        String usuario = textUser.getText();
        String contrasenya = textPass.getText();

        // Realizar la verificación del usuario en la base de datos
        Profesor profesorModel = new Profesor();
        Profesor usuarioVerificado = profesorModel.checkUser(usuario, contrasenya);

        // Aquí puedes realizar acciones adicionales según el resultado de la verificación
        if (usuarioVerificado != null) {

            // Cerrar la ventana de inicio de sesión
            Stage ventanaLogin = (Stage) textUser.getScene().getWindow();
            ventanaLogin.close();

            System.out.println("Inicio de sesión exitoso");

            try {
                // Cargar el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/practica/proyecto/dashboard-view.fxml"));
                Parent parentDash = fxmlLoader.load();

                // Crear la escena y agregar hoja de estilos CSS
                Scene dashBoard = new Scene(parentDash);
                dashBoard.getStylesheets().add(getClass().getResource("/org/practica/proyecto/css/style.css").toExternalForm());

                // Crear y configurar el escenario (Stage)
                Stage stage = new Stage();
                stage.setScene(dashBoard);
                stage.setTitle("Dashboard"); // Establecer el título de la ventana

                // Aplicar el estilo de decoración de la ventana
                stage.initStyle(StageStyle.DECORATED);

                // Mostrar la ventana
                stage.show();
            } catch (IOException e) {
                // Manejar la excepción de carga del archivo FXML
                e.printStackTrace();
            }

            return true;

        } else {

            mostrarTooltipError(errorNoti,"Credenciales incorrectas. Por favor, inténtalo de nuevo.");
        }

        return false;
    }

    // Método para mostrar el Tooltip en caso de error
    public void mostrarTooltipError(TextField campo, String mensaje) {
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/errorSonido.wav");

        Tooltip tooltip = new Tooltip(mensaje);
        tooltip.show(campo, campo.localToScreen(0, 0).getX() + campo.getWidth() / 2, campo.localToScreen(0, 0).getY() + campo.getHeight() / 2);

        // Establecer el tamaño de fuente
        tooltip.setStyle("-fx-font-size: 13px;");

        // Configurar un Timeline para ocultar el Tooltip después de la duración especificada
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tooltip.hide();
            }
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    // Método para minimizar la ventana
    @FXML
    private void minimizeWindow() {
        this.stage = (Stage) errorNoti.getScene().getWindow();
        stage.setIconified(true);
    }

    // Método para cerrar la ventana
    @FXML
    private void closeWindow() {
        this.stage = (Stage) errorNoti.getScene().getWindow();
        stage.close();
    }
}
