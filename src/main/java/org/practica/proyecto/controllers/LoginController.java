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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

import static org.practica.proyecto.controllers.DashboardController.reproducirSonido;
import static org.practica.proyecto.models.Profesor.insertarFechaToken;
import static org.practica.proyecto.models.Profesor.obtenerFechaToken;


public class LoginController {


    private static final String claveEncriptacion = "1234567890123456"; // Clave de encriptación, debe tener 16 caracteres.

    public TextField errorNoti;

    @FXML
    private CustomTextField textUser;

    @FXML
    private CustomPasswordField textPass;

    private Stage stage;

    //Ventana del login
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
    public boolean comprobarUser() throws IOException, ParseException, SQLException {

        String usuario;
        String contrasenya;
        Profesor profesorModel = new Profesor();

        if (textUser == null || textPass == null ) {

            String[] tokens = obtenerTokenUsuario();

            if(!(tokens.length >= 2)) {
                login();
                File archivo = new File("TOKEN_USUARIO.txt");
                archivo.delete();
                return false;
            }

            // Acceder al primer elemento
            usuario = tokens[0];

            // Acceder al segundo elemento
            contrasenya = tokens[1];


            LocalDateTime fechaHoraActual = LocalDateTime.now();
            // Convertir LocalDateTime a Timestamp
            Timestamp fechaToken = Timestamp.valueOf(fechaHoraActual);

            // Realizar la verificación del usuario en la base de datos
            Profesor usuarioVerificado = profesorModel.checkUser(usuario, contrasenya);

            if (usuarioVerificado != null) {
                if (obtenerFechaToken().before(fechaToken)){
                System.out.println("La fecha del archivo es anterior a la fecha actual.");
                // Realizar acciones específicas si la fecha del archivo es anterior a la fecha actual
                login();
                File archivo = new File("TOKEN_USUARIO.txt");
                archivo.delete();
                return false;
                }
            }
        }else{
            // Obtener los datos del usuario y la contraseña
            usuario = textUser.getText();
            contrasenya = textPass.getText();
        }


        // Realizar la verificación del usuario en la base de datos
        Profesor usuarioVerificado = profesorModel.checkUser(usuario, contrasenya);


        // Aquí puedes realizar acciones adicionales según el resultado de la verificación
        if (usuarioVerificado != null) {

            if (!(textUser == null)){
                // Cerrar la ventana de inicio de sesión
                Stage ventanaLogin = (Stage) textUser.getScene().getWindow();
                ventanaLogin.close();
            }


            crearTokenUsuario(usuario,contrasenya);

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

            try {
                mostrarTooltipError(errorNoti,"Credenciales incorrectas. Por favor, inténtalo de nuevo.");
            }catch (NullPointerException e){
                login();
                File archivo = new File("TOKEN_USUARIO.txt");
                archivo.delete();
                return false;
            }


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

    //Crea el token en el archivo
    public void crearTokenUsuario(String usuario, String contrasena) throws SQLException {

        Path filePath = Paths.get("TOKEN_USUARIO.txt");
        try {

            String contenidoEncriptado = encriptar(usuario + ";" + contrasena);

            try {
                Files.write(filePath, contenidoEncriptado.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new IOException("Error al guardar el token y el nombre de usuario", e);
            }

            insertarFechaToken();
            System.out.println("Archivo creado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Coge el token guardado en el archivo
    public String[] obtenerTokenUsuario() throws IOException {
        Path filePath = Paths.get("TOKEN_USUARIO.txt");

        if (!Files.exists(filePath)) {
            return null;
        }

        try {
            byte[] contenido = Files.readAllBytes(filePath);
            String contenidoDesencriptado = desencriptar(new String(contenido));
            return contenidoDesencriptado.split(";");
        } catch (IOException e) {
            throw new IOException("Error al obtener el token y el nombre de usuario", e);
        }
    }

    //Encripta al guardar los datos en el archivo
    public String encriptar(String datos) throws IOException {
        try {
            Key clave = new SecretKeySpec(claveEncriptacion.getBytes(), "AES");
            Cipher cifrador = Cipher.getInstance("AES");
            cifrador.init(Cipher.ENCRYPT_MODE, clave);
            byte[] datosEncriptados = cifrador.doFinal(datos.getBytes());
            return Base64.getEncoder().encodeToString(datosEncriptados);
        } catch (Exception e) {
            throw new IOException("Error al encriptar los datos", e);
        }
    }

    //Desencripta al coger los datos del archivo
    public String desencriptar(String datosEncriptados) throws IOException {
        try {
            Key clave = new SecretKeySpec(claveEncriptacion.getBytes(), "AES");
            Cipher cifrador = Cipher.getInstance("AES");
            cifrador.init(Cipher.DECRYPT_MODE, clave);
            byte[] datosDesencriptados = cifrador.doFinal(Base64.getDecoder().decode(datosEncriptados));
            return new String(datosDesencriptados);
        } catch (Exception e) {
            login();
            File archivo = new File("TOKEN_USUARIO.txt");
            archivo.delete();
        }
        return datosEncriptados;
    }

}
