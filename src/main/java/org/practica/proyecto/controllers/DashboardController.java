package org.practica.proyecto.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import io.github.gleidson28.GNAvatarView;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.practica.proyecto.models.Alumno;
import org.practica.proyecto.models.Graficos;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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

    public ImageView imagenAlumno;
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
    public Button deseleccionarAlumno;
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
    public GNAvatarView avatarView;

    //INSERTAR ALUMNO
    public Button botonInsertarAlumno;

    //TEXTO NOTIFICACIONES
    public Text textoNotif;
    public AnchorPane fondoNotif;


    //GRAFICOS ALUMNO
    public PieChart quesitosLocalidad;
    public StackedBarChart<String, Number> barraAlumno;

    //PDF
    public Button botonPDF;


    //CODIGO DE LA APLICACION DASHBOARD---------------------------------------------------------------------------------

    // Arranca la clase con el initialize
    @FXML
    void initialize(){

        if(inicializado){
            actualizacion();
            myChoiceBox.getItems().addAll(elegirRegistros);
            myChoiceBox.setValue(10);
            inicializado = false;
        }
        validacion();
        myChoiceBox.setOnAction(this::elegirRegistros);
        botonDesactivado();
        cargarDatos();


        // Ruta de la imagen (asegúrate de que la ruta sea correcta y la imagen exista)
        String imageUrl = "/org/practica/proyecto/imagen/foto-perfil.png";


        // Crear una instancia de Image de JavaFX
        javafx.scene.image.Image javafxImage = new javafx.scene.image.Image("file:///" + imageUrl);


        // Establecer la imagen en el GNAvatarView
        avatarView.setImage(javafxImage);

    }

    // Obtiene los datos y los inserta en la tabla
    public void cargarDatos() {


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

        try {
            datosGrafico();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //ACCIONES PARA EL GUARDADO,INSERTAR Y ELIMINADO DEL ALUMNO --------------------------------------------------------

    //Boton para guardar los datos de lo Alumnos que se hayan modificado de la tabla_
    public void guardarAlumnoClick() throws ParseException {
        Alumno alumnoSeleccionado = tabla_alumnos.getSelectionModel().getSelectedItem();
        int rowsAlumno = alumnoSeleccionado.getRow();
        Alumno alumno = new Alumno(dniClick.getText(),nombreClick.getText(),apellido_1Click.getText(),apellido_2Click.getText(),
                direccionClick.getText(),localidadClick.getText(),provinciaClick.getText(), (java.sql.Date) fechaDate(nacimientoClick.getValue()),rowsAlumno);
        // Llama un método para guardar los datos del alumno
        alumno.actualizarAlumno();
        limpiarAlumno();
        notificacion(true,"Alumno editado correctamente");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/guardarSonido.wav");
        cargarDatos();

    }

    //Boton para eliminar los datos de los Alumnos
    public void eliminarAlumnoClick(){
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
                boolean eliminacionExitosa = alumno.eliminarAlumno(rowsAlumno);
                if (eliminacionExitosa) {
                    notificacion(true,"Alumno eliminado correctamente");
                    reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/eliminarSonido.wav");
                    limpiarAlumno();
                    cargarDatos();
                } else {
                    // Código para manejar el caso de eliminación fallida
                    notificacion(false,"Error al eliminar el alumno. Contiene claves ajenas.");
                }

            } else {
                // El usuario canceló la operación
                notificacion(false,"Operación de eliminación cancelada.");
            }
        });
    }

    //Boton para inserta los datos del Alumno
    public void insertarAlumno() throws SQLException {
        // Verificar si el DNI tiene un formato válido
        if (!validarFormatoDNI(insertarDNI.getText())) {
            notificacion(false, "El formato del DNI no es válido.");
            return;
        }

        // Agregar más validaciones según sea necesario...

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("¿El DNI del Alumno es correcto? No se podrá cambiar");
        alert.setContentText("El DNI introducido es: " + insertarDNI.getText());

        // Configurar botones OK y Cancelar
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Mostrar el diálogo y esperar la respuesta del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Realizar la inserción del alumno
                    Alumno alumno = new Alumno(insertarDNI.getText(), insertarNombre.getText(), insertarApellido1.getText(),
                            insertarApellido2.getText(), insertarDireccion.getText(), insertarLocalidad.getText(),
                            insertarProvincia.getText(), (java.sql.Date) fechaDate(insertarFecha.getValue()), 0);
                    if (alumno.insertarAlumno()){
                        limpiarAlumnoInsertado();
                        notificacion(true, "Alumno insertado correctamente");
                        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/guardarSonido.wav");
                        cargarDatos();
                    }else{
                        notificacion(false, "El DNI introducido ya existe");
                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // El usuario canceló la operación
                notificacion(false, "Operación de insercción cancelada.");
            }
        });
        datosGrafico();
    }

    //HACE QUE CAMBIE EL NUMERO DEL PAGINADOR Y A LA VEZ SE LO MANDE AL RESULTSET---------------------------------------

    //Cambia el numero del paginador dependiendo del boton pulsado
    @FXML
    public void paginador(ActionEvent event) throws SQLException {
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
        cargarDatos();
    }

    //EL numero que se inserte en el textField del pafinado lo cambia
    @FXML
    private void numeroInsertadoPag() throws SQLException {
        int totalPaginas = Alumno.contPaginas(maxRegistros);

        actualPag.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!actualPag.getText().isEmpty()) {

                    int pag = Integer.parseInt(actualPag.getText());

                    if (pag <= 0) {
                        notificacion(false, "El número de página ingresado es inferior al mínimo permitido.");
                    } else if (pag > totalPaginas) {
                        notificacion(false, "El número de página ingresado supera el máximo permitido.");
                    } else {
                        paginaActual = pag;
                        actualPag.setText(String.valueOf(paginaActual));
                        cargarDatos();
                    }
                } else {
                    notificacion(false, "Ingrese un valor válido para la página.");
                }
            }
        });
    }

    //BOTONES PARA PODER NAVEGAR POR BARRA LATERAL----------------------------------------------------------------------

    //Boton navegacion muestra datos del Colegio sobre los alumnos
    @FXML
    public void botonHome() {
        setBotonActivo(botonHome, panelHome);
        botonAlumnos.setStyle("");
        botonAdd.setStyle("");
        botonPerfil.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");
    }

    //Boton navegacion muestra todos los Alumnos y poder editarlos o eliminarlos
    @FXML
    public void botonAlumnos() {
        setBotonActivo(botonAlumnos, panelEditar);
        botonHome.setStyle("");
        botonAdd.setStyle("");
        botonPerfil.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");
    }

    //Boton navegacion añadir Alumno
    @FXML
    public void botonAdd() {
        setBotonActivo(botonAdd, panelAdd);
        botonHome.setStyle("");
        botonAlumnos.setStyle("");
        botonPerfil.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");

    }

    //Boton navegacion perfil
    @FXML
    public void botonPerfil() {
        setBotonActivo(botonPerfil, panelPerfil);
        botonHome.setStyle("");
        botonAlumnos.setStyle("");
        botonAdd.setStyle("");
        reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/deslizarSonido.wav");
    }

    //Contiene todos los botenes de navegacion y le pone el color alrededor
    public void setBotonActivo(Button boton, AnchorPane panel) {
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

        tabla_alumnos.getSelectionModel().clearSelection();

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

    //Si algun campo de insertar o actualizar Alumno está vacio no se activará
    public void botonDesactivado() {
        BooleanBinding camposVacios = dniClick.textProperty().isEmpty()
                .or(nombreClick.textProperty().isEmpty())
                .or(apellido_1Click.textProperty().isEmpty())
                .or(direccionClick.textProperty().isEmpty())
                .or(localidadClick.textProperty().isEmpty())
                .or(provinciaClick.textProperty().isEmpty())
                .or(nacimientoClick.valueProperty().isNull());

        botonGuardarAlumno.disableProperty().bind(camposVacios);
        botonPDF.disableProperty().bind(camposVacios);
        botonEliminarAlumno.disableProperty().bind(camposVacios);
        deseleccionarAlumno.disableProperty().bind(camposVacios);

        BooleanBinding camposInsertarVacios = insertarDNI.textProperty().isEmpty()
                .or(insertarNombre.textProperty().isEmpty())
                .or(insertarApellido1.textProperty().isEmpty())
                .or(insertarLocalidad.textProperty().isEmpty())
                .or(insertarProvincia.textProperty().isEmpty())
                .or(insertarDireccion.textProperty().isEmpty())
                .or(insertarFecha.valueProperty().isNull());

        botonInsertarAlumno.disableProperty().bind(camposInsertarVacios);
    }

    //Valida que todos los campo para introducir en inserta o en editar sea toodo correcto
    public void validacion(){
        //Paginador
        soloNumeros(actualPag);

        //ActualizarAlumno
        soloLetras(nombreClick);
        soloLetras(apellido_1Click);
        soloLetras(apellido_2Click);
        soloLetras(localidadClick);
        soloLetras(provinciaClick);
        validarFecha(nacimientoClick);

        //ActualizarAlumno
        validarDNI(insertarDNI);
        soloLetras(insertarNombre);
        soloLetras(insertarApellido1);
        soloLetras(insertarApellido2);
        soloLetras(insertarLocalidad);
        soloLetras(insertarProvincia);
        validarFecha(insertarFecha);

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
        Date utilDate = dateFormat.parse(fechaString);
        return new java.sql.Date(utilDate.getTime());
    }

    //Notificacion diseñada
    public void notificacion(Boolean validacion, String mensaje) {

        // Configurar la notificación
        if (validacion){
            fondoNotif.setStyle("-fx-background-color: #00ff00");  // Verde
            reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/guardarSonido.wav");
        }else{
            fondoNotif.setStyle("-fx-background-color: #ff0000;");  // Rojo
            reproducirSonido("src/main/resources/org/practica/proyecto/sonidos/errorSonido.wav");
        }

        textoNotif.setText(mensaje);
        fondoNotif.setVisible(true);

        // Configurar animación de desplazamiento
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), fondoNotif);
        translateTransition.setFromY(-fondoNotif.getHeight());
        translateTransition.setToY(0);

        // Configurar animación de opacidad (aparecer)
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), fondoNotif);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Configurar animación de opacidad (desaparecer)
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), fondoNotif);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Iniciar la animación de aparición y desaparición
        translateTransition.play();
        fadeIn.play();

        // Configurar la duración total de la notificación
        Duration notificacionDuration = Duration.seconds(1.5);

        // Configurar la línea de tiempo para desaparecer después de un tiempo
        fadeOut.setDelay(notificacionDuration);
        fadeOut.play();
    }

    //Le pasas un sonido y lo reproduce
    public void reproducirSonido(String rutaArchivo) {
        Platform.runLater(() -> {
            try {
                Media media = new Media(new File(rutaArchivo).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setOnEndOfMedia(() -> {
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.dispose(); // Liberar recursos
                });

                mediaPlayer.play();
            } catch (Exception e) {
                System.err.println("Error al reproducir el sonido: " + e.getMessage());
                e.printStackTrace();
                // Agregar lógica de manejo de excepciones específica si es necesario
            }
        });
    }

    //VALIDACIONES------------------------------------------------------------------------------------------------------

    //Valida que solo puedan entrar numeros
    public void soloNumeros(TextField textField) {

        TextFormatter<Object> formatter = new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            } else {
                return null; // Rechazar el cambio si no es un número
            }
        });

        textField.setTextFormatter(formatter);

    }

    //Valida que solo puedan poner caracteres
    public void soloLetras(TextField textField) {

        // Configurar el TextFormatter para permitir solo letras
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (Pattern.matches("[a-zA-Z ]*", newText)) {
                return change;
            } else {
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    //Valida un dni español
    public void validarDNI(TextField textField) {
        // Configurar el TextFormatter para validar DNI español
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // El DNI español tiene 8 dígitos seguidos de una letra (mayúscula)
            if (Pattern.matches("\\d{0,8}[a-zA-Z]?", newText)) {
                return change;
            } else {
                return null;
            }
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    //Valida fecha entre 2 años atras y 125 maximo
    public void validarFecha(DatePicker datePicker) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate today = LocalDate.now();
                LocalDate fechaInicio = today.minusYears(2);
                LocalDate fechaLimite = today.minusYears(125);

                // Deshabilitar fechas más de 2 años en el pasado y fechas más de 125 años en el pasado
                setDisable(empty || date.isAfter(fechaInicio) || date.isBefore(fechaLimite));
            }
        });
    }

    //Valida que el formato del DNI sea Expañol
    private boolean validarFormatoDNI(String dni) {
        if (dni.length() == 9) { // DNI debe tener 9 caracteres (8 números + 1 letra)
            String numeros = dni.substring(0, 8);
            String letra = dni.substring(8);

            // Verificar que los primeros 8 caracteres sean números
            if (numeros.matches("\\d+")) {
                // Verificar que el último caracter sea una letra
                if (letra.matches("[a-zA-Z]")) {
                    return true; // El formato es válido
                }
            }
        }
        return false; // El formato no es válido
    }

    //ACCIONES DE EXPORTACIONES-----------------------------------------------------------------------------------------

    //Al darle al boton exporta el resultSet a CSV
    public void botonExportarCSV() {
       if (Alumno.exportToCSV()){
           notificacion(true,"CSV exportado exitosamente!");
       }else{
           notificacion(false,"La exportación ha sido cancelada.");
       }
    }

    //Exporta el objeto seleccionado de la tabla a pdf
    public void exportarAlumnoPDF() {
        Document document = new Document();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("alumnoPDF.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Crear un párrafo para la imagen
                Paragraph imageParagraph = new Paragraph();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imagenAlumno.getImage(), null);
                Image image = Image.getInstance(bufferedImage, null);
                image.scaleToFit(400, 400);
                image.setAlignment(Element.ALIGN_CENTER);
                imageParagraph.add(image);
                document.add(imageParagraph);

                // Espacio entre la imagen y el contenido de texto
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

                // Crear un párrafo para los campos y valores
                Paragraph dataParagraph = new Paragraph();
                dataParagraph.setAlignment(Element.ALIGN_CENTER);

                String[] campos = {"DNI", "NOMBRE", "1º APELLIDO", "2º APELLIDO", "DIRECCIÓN", "LOCALIDAD", "PROVINCIA", "FECHA DE NACIMIENTO"};
                String[] valores = {dniClick.getText(), nombreClick.getText(), apellido_1Click.getText(), apellido_2Click.getText(),
                        direccionClick.getText(), localidadClick.getText(), provinciaClick.getText(),
                        String.valueOf(nacimientoClick.getValue())};

                // Agregar cada campo y su valor con un estilo adecuado
                for (int i = 0; i < campos.length; i++) {
                    dataParagraph.add(Chunk.NEWLINE);
                    Chunk campoChunk = new Chunk(campos[i] + ": ", FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD));
                    Chunk valorChunk = new Chunk(valores[i].toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA, 15));

                    // Alinear campos y valores en el mismo renglón
                    dataParagraph.add(campoChunk);
                    dataParagraph.add(valorChunk);
                    dataParagraph.add(Chunk.NEWLINE);
                }

                document.add(dataParagraph);
                document.close();
                notificacion(true, "PDF exportado exitosamente!");
            } catch (DocumentException | java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            notificacion(false, "La exportación a PDF ha sido cancelada.");
        }
    }

    //GRAFICOS----------------------------------------------------------------------------------------------------------

    //Carga los datos de la consultas que hay en graficos
    public void datosGrafico() throws SQLException {
        Graficos graficos= new Graficos();
        List<Graficos> listGraficos = graficos.graficoAnios();

        // Limpiar los datos existentes en el StackedBarChart
        barraAlumno.getData().clear();

        //Inserta los datos con un for each
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Graficos grafico : listGraficos) {
            series.getData().add(new XYChart.Data<>(String.valueOf(grafico.getCantidadBuscada()), grafico.getCantidadAlumnos()));
        }

        // Añade los datos
        barraAlumno.getData().add(series);

        series.setName("Alumnos agrupados por año de nacimiento");


        // Mostrar los valores en etiquetas
        for (XYChart.Series<String, Number> unaSerie : barraAlumno.getData()) {
            for (XYChart.Data<String, Number> data : unaSerie.getData()) {
                Label label = new Label(data.getYValue().toString());
                StackPane stackPane = (StackPane) data.getNode();
                stackPane.getChildren().add(label);
                label.setStyle("-fx-text-fill: white; -fx-font-size: 15pt;");

                CategoryAxis xAxis = (CategoryAxis) barraAlumno.getXAxis();
                NumberAxis yAxis = (NumberAxis) barraAlumno.getYAxis();

                // Cambiar color de los datos en el eje X (inferior)
                xAxis.setStyle("-fx-tick-label-fill: white;");

                // Cambiar color de los datos en el eje Y (lateral)
                yAxis.setStyle("-fx-tick-label-fill: white;");
            }
        }



        // Limpiar los datos existentes en el PieChart
        quesitosLocalidad.getData().clear();

        // Obtener datos para el PieChart
        List<Graficos> pieChartData2 = graficos.graficoProvincia();

        // Crear datos para el PieChart y mostrar valores en etiquetas
        for (Graficos grafico : pieChartData2) {
            PieChart.Data newData = new PieChart.Data(grafico.getCantidadBuscada(), grafico.getCantidadAlumnos());
            quesitosLocalidad.getData().add(newData); // Agregar nuevo dato
        }

        // Aplicar estilo CSS para cambiar el color del texto en los quesitos y el título a blanco
        quesitosLocalidad.getStylesheets().add("data:text/css," +
                ".chart-pie-label {-fx-fill: white;}" +
                ".chart-title {-fx-text-fill: white;}"
        );

    }

    //ACTUALIZACION-----------------------------------------------------------------------------------------------------

    //Comprueba si hay actualizaciones
    public void actualizacion() {
        GitHub github;
        try {
            github = new GitHubBuilder().build(); // No es necesario proporcionar un token de acceso
            GHRepository repository = github.getRepository("zZDANTI/Proyecto");
            String latestVersion = ""; // Inicializar con una cadena vacía

            // Verificar si el repositorio es null
            if (repository == null) {
                System.out.println("El repositorio no fue encontrado en GitHub.");
                return;
            }

            GHRelease latestRelease = repository.getLatestRelease();

            if (latestRelease != null) {
                latestVersion = latestRelease.getTagName();
                String currentVersion = "1.0"; // Versión actual de la aplicación

                System.out.println("Current Version: " + currentVersion);

                if (!latestVersion.equals(currentVersion)) {
                    mostrarNotificacionDeActualizacion();
                }
            } else {
                // Tratar el caso en el que no hay versión de release disponible
                System.out.println("No se encontró ninguna versión de release para el repositorio.");
            }

            System.out.println("Latest Version: " + latestVersion); // Imprimir latestVersion fuera del bloque else

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Notifica en pantalla que hay una nueva version
    private void mostrarNotificacionDeActualizacion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualización Disponible");
        alert.setHeaderText("¡Hay una nueva versión disponible!");
        alert.setContentText("Por favor, descargue la última versión desde nuestro sitio web.");

        alert.showAndWait();
    }

}
