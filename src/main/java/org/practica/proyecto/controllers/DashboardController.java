package org.practica.proyecto.controllers;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.controlsfx.control.Notifications;
import org.practica.proyecto.models.Alumno;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.practica.proyecto.models.Alumno.obtenerDatosDeAlumnos;

public class DashboardController {


    //BOTONES NAVEGACION
    public Button botonHome;
    public Button botonAlumnos;
    public Button botonPerfil;
    public Button botonAdd;

    //PANELES DE NAVEGACION

    public AnchorPane panelHome;

    public AnchorPane panelEditar;

    public AnchorPane panelAdd;

    public AnchorPane panelPerfil;

    //TABLA ALUMNOS
    @FXML
    public TableView<Alumno> tabla_alumnos;
    public TableColumn<Alumno, String> dni_tabla;
    public TableColumn<Alumno, String> nombre_tabla;
    public TableColumn<Alumno, String> apellido_1_tabla;
    public TableColumn<Alumno, String> apellido_2_tabla;
    public TableColumn<Alumno, String> direccion_tabla;
    public TableColumn<Alumno, String> localidad_tabla;
    public TableColumn<Alumno, String> provincia_tabla;
    public TableColumn<Alumno, String> fecha_nacimiento_tabla;
    public TableColumn<Alumno, Integer> rowRs;


    //DATOS DEL ALUMNO SELECCIONADO
    public TextField dniClick;
    public TextField provinciaClick;
    public TextField direccionClick;
    public TextField localidadClick;
    public TextField nombreClick;
    public TextField apellido_1Click;
    public TextField apellido_2Click;
    public DatePicker nacimientoClick;


    //PAGINADOR
    public TextField actualPag;
    
    //BOTONES AL GUARDAR ALUMNOS

    public Button botonGuardarAlumno;
    public Button botonEliminarAlumno;
    public Text numeroTotalPaginas;
    public Text numeroTotalAlumnos;

    //VARIABLES PREDETERMINADAS
    int maxRegistros = 10;
    int paginaActual = 1;
    String filtroAnterior = "";
    Boolean inicializado = true;

    //BUSCADOR Y MAXIMOS REGISTROS
    public TextField buscarAlumno;
    public ChoiceBox<Integer> myChoiceBox;
    public Integer[] elegirRegistros = {10,20,30,40,50};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //VARIABLES DE INSERTAR UN NUEVO ALUMNO

    public DatePicker insertarFecha;
    public TextField insertarProvincia;
    public TextField insertarLocalidad;
    public TextField insertarDireccion;
    public TextField insertarApellido2;
    public TextField insertarApellido1;
    public TextField insertarNombre;
    public TextField insertarDNI;


    //CODIGO DE LA APLICACION DASHBOARD---------------------------------------------------------------------------------

    // Arranca la clase con el initialize
    @FXML
    public void initialize(){

        if(inicializado){
            myChoiceBox.getItems().addAll(elegirRegistros);
            myChoiceBox.setValue(10);
            inicializado = false;
        }

        myChoiceBox.setOnAction(this::elegirRegistros);
        botonDesactivado();
        cargarDatos();
    }

    // Obtiene los datos y los inserta en la tabla
    private void cargarDatos() {


        limpiarAlumno();
        botonAlumnos.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 10,0,0,1); -fx-background-color: #181818;");
        if (buscarAlumno.getText() != null && !buscarAlumno.getText().isEmpty() && !buscarAlumno.getText().equals(filtroAnterior)) {
            filtroAnterior = buscarAlumno.getText();
            paginaActual=1;
            actualPag.setText(String.valueOf(paginaActual));
        }

        // Obtén los datos de los alumnos
        List<Alumno> listaAlumnos = obtenerDatosDeAlumnos(maxRegistros,paginaActual,buscarAlumno.getText());

        // Limpia la tabla
        tabla_alumnos.getItems().clear();

        // Agrega los datos a la tabla
        tabla_alumnos.getItems().addAll(listaAlumnos);


        // Configura las celdas de las columnas


        rowRs.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getRow()));
        dni_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDni()));
        nombre_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNombre()));
        apellido_1_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getApellido1()));
        apellido_2_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getApellido2()));
        direccion_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDireccion()));
        localidad_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLocalidad()));
        provincia_tabla.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getProvincia()));
        fecha_nacimiento_tabla.setCellValueFactory(cellData ->
                new SimpleStringProperty(fechaString(cellData.getValue().getFechaNacimiento())));


        try {
            numeroTotalPaginas.setText("Total paginas: "+Alumno.contPaginas(maxRegistros));
            numeroTotalAlumnos.setText("Total Alumnos: " + Alumno.totalRegistros());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        alumnoClick();
    }

    //Funcion para saber que alumno se ha clickeado en la tabla
    public void alumnoClick() {
        tabla_alumnos.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                Alumno alumnoSeleccionado = tabla_alumnos.getSelectionModel().getSelectedItem();
                if (alumnoSeleccionado != null) {
                    dniClick.setText(alumnoSeleccionado.getDni());
                    nombreClick.setText(alumnoSeleccionado.getNombre().toLowerCase());
                    apellido_1Click.setText(alumnoSeleccionado.getApellido1().toLowerCase());
                    apellido_2Click.setText(alumnoSeleccionado.getApellido2().toLowerCase());
                    direccionClick.setText(alumnoSeleccionado.getDireccion().toLowerCase());
                    localidadClick.setText(alumnoSeleccionado.getLocalidad().toLowerCase());
                    provinciaClick.setText(alumnoSeleccionado.getProvincia().toLowerCase());
                    nacimientoClick.setValue(alumnoSeleccionado.getFechaNacimiento().toLocalDate());
                }
            }
        });
    }

    //ACCIONES PARA EL GUARDADO,INSERTAR Y ELIMINADO DEL ALUMNO ------------------------------------------------------------------

    //Boton para guardar los datos de lo Alumnos que se hayan modificado de la tabla_
    public void guardarAlumnoSeleccionado() throws ParseException {
        Alumno alumnoSeleccionado = tabla_alumnos.getSelectionModel().getSelectedItem();
        int rowsAlumno = alumnoSeleccionado.getRow();
        Alumno alumno = new Alumno(dniClick.getText(),nombreClick.getText(),apellido_1Click.getText(),apellido_2Click.getText(),
                direccionClick.getText(),localidadClick.getText(),provinciaClick.getText(), (java.sql.Date) fechaDate(nacimientoClick.getValue()),rowsAlumno);
        // Llama un método para guardar los datos del alumno
        alumno.actualizarAlumno();
        limpiarAlumno();
        mostrarNotificacionConTitulo("Notificación","Alumno editado correctamente");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/guardarSonido.wav");
        cargarDatos();

    }

    //Boton para eliminar los datos de los Alumnos
    public void eliminarAlumnoSeleccionado(){
        Alumno alumnoSeleccionado = tabla_alumnos.getSelectionModel().getSelectedItem();
        int rowsAlumno = alumnoSeleccionado.getRow();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar a este alumno?");
        alert.setContentText("Alumno: " + alumnoSeleccionado.getNombre() + " "+ alumnoSeleccionado.getApellido1()+  " "+ alumnoSeleccionado.getApellido2()+
                " con DNI: " + alumnoSeleccionado.getDni());

        // Configurar botones OK y Cancelar
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Mostrar el diálogo y esperar la respuesta del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alumno alumno = new Alumno();
                alumno.eliminarAlumno(rowsAlumno);
                limpiarAlumno();
                mostrarNotificacionConTitulo("Notificación","Alumno eliminado correctamente");
                reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/eliminarSonido.wav");
                cargarDatos();
            } else {
                // El usuario canceló la operación
                System.out.println("Operación de eliminación cancelada.");
            }
        });



    }

    //Boton para inserta los datos del Alumno
    public void insertarAlumno(){


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿El DNI del Alumno es correcto? No se podrá cambiar");
        alert.setContentText("El DNI introducido es :" + insertarDNI.getText());

        // Configurar botones OK y Cancelar
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Mostrar el diálogo y esperar la respuesta del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                try {
                    Alumno alumno = new Alumno(insertarDNI.getText(),insertarNombre.getText(),insertarApellido1.getText(),insertarApellido2.getText(),
                            insertarDireccion.getText(),insertarLocalidad.getText(),insertarProvincia.getText(), (java.sql.Date) fechaDate(insertarFecha.getValue()),0);
                    alumno.insertarAlumno();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                limpiarAlumnoInsertado();
                mostrarNotificacionConTitulo("Notificación","Alumno eliminado correctamente");
                reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/guardarSonido.wav");
                cargarDatos();
            } else {
                // El usuario canceló la operación
                System.out.println("Operación de insercción cancelada.");
            }
        });

    }

    //HACE QUE CAMBIE EL NUMERO DEL PAGINADOR Y A LA VEZ SE LO MANDE AL RESULTSET---------------------------------------
    @FXML
    private void paginador(ActionEvent event) throws SQLException {
        Button botonPresionado = (Button) event.getSource();
        int totalPaginas = Alumno.contPaginas(maxRegistros);

        switch (botonPresionado.getId()) {
            case "primeraPag":
                // primera pagina
                paginaActual = 1;
                actualPag.setText(String.valueOf(paginaActual));
                break;
            case "anteriorPag":
                // Debe ser mayor a 1 para permitir retroceder desde la primera página
                if (paginaActual > 1) {
                    paginaActual--;
                    actualPag.setText(String.valueOf(paginaActual));
                }
                break;
            case "siguientePag":
                // Debe ser menor al total de páginas para permitir avanzar desde la última página
                if (paginaActual < totalPaginas) {
                    paginaActual++;
                    actualPag.setText(String.valueOf(paginaActual));
                }
                break;
            case "ultimaPag":
                // Lógica para ir a la última pagina
                paginaActual = totalPaginas;
                actualPag.setText(String.valueOf(paginaActual));
                break;
        }
        initialize();
    }

    //BOTONES PARA PODER NAVEGAR POR BARRA LATERAL----------------------------------------------------------------------

    //Boton navegacion muestra datos del Colegio sobre los alumnos
    @FXML
    private void botonHome() {
        setBotonActivo(botonHome, panelHome);
        botonAlumnos.setStyle("");
        botonAdd.setStyle("");
        botonPerfil.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");
    }

    //Boton navegacion muestra todos los Alumnos y poder editarlos o eliminarlos
    @FXML
    private void botonAlumnos() {
        setBotonActivo(botonAlumnos, panelEditar);
        botonHome.setStyle("");
        botonAdd.setStyle("");
        botonPerfil.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");
    }

    //Boton navegacion añadir Alumno
    @FXML
    private void botonAdd() {
        setBotonActivo(botonAdd, panelAdd);
        botonHome.setStyle("");
        botonAlumnos.setStyle("");
        botonPerfil.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");

    }

    //Boton navegacion perfil
    @FXML
    private void botonPerfil() {
        setBotonActivo(botonPerfil, panelPerfil);
        botonHome.setStyle("");
        botonAlumnos.setStyle("");
        botonAdd.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");
    }

    //Contiene todos los botenes de navegacion y le pone el color alrededor
    private void setBotonActivo(Button boton, AnchorPane panel) {
        // Aplicar el estilo al nuevo botón activo
        boton.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 10,0,0,1); -fx-background-color: #181818;");

        // Ocultar todos los paneles
        panelHome.setVisible(false);
        panelEditar.setVisible(false);
        panelAdd.setVisible(false);
        panelPerfil.setVisible(false);

        // Mostrar el panel correspondiente
        panel.setVisible(true);

    }

    //COMPLEMENTO PARA LA EDICION DEL ALUMNO----------------------------------------------------------------------------

    //limpia los datos donde se puede editar el alumno
    public void limpiarAlumno(){
        dniClick.clear();
        nombreClick.clear();
        apellido_1Click.clear();

        apellido_2Click.clear();
        direccionClick.clear();
        localidadClick.clear();
        provinciaClick.clear();
        nacimientoClick.setValue(null);
    }

    //Limpia los datos cuando inserta un usuario
    public void limpiarAlumnoInsertado(){
        insertarDNI.clear();
        insertarNombre.clear();
        insertarApellido1.clear();
        insertarApellido2.clear();
        insertarDireccion.clear();
        insertarLocalidad.clear();
        insertarProvincia.clear();
        insertarFecha.setValue(null);
    }

    //Si el campo del dni esta vacio se desactiva el boton de guardar y el de eliminar
    public void botonDesactivado() {
        botonGuardarAlumno.disableProperty().bind(dniClick.textProperty().isEmpty());
        botonEliminarAlumno.disableProperty().bind(dniClick.textProperty().isEmpty());


    }

    //El usuario puede cambiar cuantos registros quiere que aparezca
    public void elegirRegistros(ActionEvent event){

        maxRegistros = myChoiceBox.getValue();
        paginaActual=1;
        actualPag.setText(String.valueOf(paginaActual));
        cargarDatos();

    }

    //FUNCIONES QUE MODIFICAN VISUALMENTE-------------------------------------------------------------------------------
    //Traduce la fecha de la base de datos a string y formato ESP
    public String fechaString(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(fecha);
    }

    //Traduce la fecha para que pueda leerlo la base de datos en formato ENG
    public Date fechaDate(LocalDate fecha) throws ParseException {
        String fechaString = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        java.util.Date utilDate = dateFormat.parse(fechaString);
        return new java.sql.Date(utilDate.getTime());
    }

    //Notificacion sin titulo
    public static void mostrarNotificacion(String mensaje) {
        Scene scene = new Scene(new javafx.scene.layout.StackPane(), 300, 200);
        scene.getStylesheets().add(DashboardController.class.getResource("/org/practica/proyecto/css/style.css").toExternalForm());
        Notifications.create().text(mensaje)
                .darkStyle()
                .show();
    }

    //Notificacion con titulo
    public static void mostrarNotificacionConTitulo(String titulo, String mensaje) {
        Scene scene = new Scene(new javafx.scene.layout.StackPane(), 300, 200);
        scene.getStylesheets().add(Objects.requireNonNull(DashboardController.class.getResource("/org/practica/proyecto/css/style.css")).toExternalForm());
        Notifications.create().title(titulo).text(mensaje)
                .darkStyle()
                .show();
    }

    //Le pasas un sonido y lo reproduce
    public void reproducirSonido(String rutaArchivo) {
        try {
            Media media = new Media(new File(rutaArchivo).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Agregar un oyente para manejar el evento de finalización de reproducción
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose(); // Liberar recursos
            });

            // Reproducir el sonido en el hilo de JavaFX
            Platform.runLater(() -> mediaPlayer.play());

        } catch (Exception e) {
            e.printStackTrace(); // o manejo específico de la excepción
        }
    }




}
