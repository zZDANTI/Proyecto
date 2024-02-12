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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static org.practica.proyecto.controllers.DashboardController.reproducirSonido;

public class LoginController {


    public TextField errorNoti;

    @FXML
    private CustomTextField textUser;

    @FXML
    private CustomPasswordField textPass;

    private Stage stage;

    public void login() throws IOException {
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

    @FXML
    public boolean comprobarUser() throws IOException, ParseException {

        String usuario;
        String contrasenya;

        if (textUser == null || textUser.getText() == null || textUser.getText().isEmpty() ||
                textPass == null || textPass.getText() == null || textPass.getText().isEmpty()) {
            String[] tokens = obtenerTokenUsuario();
            String fecha;

            // Acceder al primer elemento
            usuario = tokens[0];

            // Acceder al segundo elemento
            contrasenya = tokens[1];

            fecha = tokens[2];

            System.out.println("Fecha guardada" + fecha);

            // Comparar la fecha con la fecha actual
            SimpleDateFormat formato = new SimpleDateFormat("HH.mm.ss dd-MM-yyyy");

            Date fechaArchivoDate = formato.parse(fecha);
            Date fechaActual = new Date();

            // Comparar las fechas
            if (fechaArchivoDate.compareTo(fechaActual) < 0) {
                System.out.println("La fecha del archivo es anterior a la fecha actual.");
                // Realizar acciones específicas si la fecha del archivo es anterior a la fecha actual
                login();
                File archivo = new File("TOKEN_USUARIO.txt");
                archivo.delete();
                return false;
            }

        }else{
            // Obtener los datos del usuario y la contraseña
            usuario = textUser.getText();
            contrasenya = textPass.getText();
        }



        // Realizar la verificación del usuario en la base de datos
        Profesor profesorModel = new Profesor();
        Profesor usuarioVerificado = profesorModel.checkUser(usuario, contrasenya);



        // Aquí puedes realizar acciones adicionales según el resultado de la verificación
        if (usuarioVerificado != null) {

            if (!(textUser == null)){
                // Cerrar la ventana de inicio de sesión
                Stage ventanaLogin = (Stage) textUser.getScene().getWindow();
                ventanaLogin.close();
            }


            crearArchivoUsuario(usuario,contrasenya);

            System.out.println("Inicio de sesión exitoso");

            try {
                // Cargar el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/practica/proyecto/dashboard-view.fxml"));
                Parent parentDash = fxmlLoader.load();

                // Obtener el controlador del panel de control
                DashboardController dashboardController = fxmlLoader.getController();

                // Pasar el objeto Profesor al controlador del panel de control
                dashboardController.perfilProfesor(usuarioVerificado);

                dashboardController.admin(usuarioVerificado.getAdmin());

                // Crear la escena y agregar hoja de estilos CSS
                Scene dashBoard = new Scene(parentDash);
                dashBoard.getStylesheets().add(getClass().getResource("/org/practica/proyecto/css/style.css").toExternalForm());

                // Crear y configurar el escenario (Stage)
                Stage stage = new Stage();
                stage.setScene(dashBoard);
                stage.setTitle("Dashboard"); // Establecer el título de la ventana

                stage.setMinWidth(1250);
                stage.setMinHeight(850);

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

    public void crearArchivoUsuario(String usuario, String contrasena) {
        try {
            // Obtener la fecha y hora actual
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss dd-MM-yyyy");

            // Crear un objeto Calendar y establecer la fecha y hora actual
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Sumar una hora
            calendar.add(Calendar.DAY_OF_MONTH, 30);

            // Obtener la nueva fecha y hora
            Date nuevaFechaHora = calendar.getTime();
            String nuevaFechaHoraFormateada = dateFormat.format(nuevaFechaHora);

            // Crear el archivo y escribir los datos
            try (FileWriter fw = new FileWriter("TOKEN_USUARIO.txt");
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(usuario + ";" + contrasena + ";" + nuevaFechaHoraFormateada);
            }

            System.out.println("Archivo creado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] obtenerTokenUsuario() throws IOException {
        Path filePath = Paths.get("TOKEN_USUARIO.txt");

        if (!Files.exists(filePath)) {
            return null;
        }

        try {
            byte[] contenido = Files.readAllBytes(filePath);
            String contenidoDesencriptado = new String(contenido);
            return contenidoDesencriptado.split(";");
        } catch (IOException e) {
            throw new IOException("Error al obtener el token y el nombre de usuario", e);
        }
    }

}
