package org.practica.proyecto.controllers;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.practica.proyecto.models.Alumno;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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


    int maxRegistros = 10;
    int paginaActual = 1;
    String filtroAnterior = "";
    Boolean inicializado = true;

    //BUSCADOR Y MAXIMOS REGISTROS
    public TextField buscarAlumno;
    public ChoiceBox<Integer> myChoiceBox;
    public Integer[] elegirRegistros = {10,20,30,40,50};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");




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

    //ACCIONES PARA EL GUARDADO O ELIMINADO DEL ALUMNO

    //Boton para guardar los datos de lo clientes que se hayan modificado de la tabla
    public void guardarAlumnoSeleccionado() throws ParseException {
        Alumno alumnoSeleccionado = tabla_alumnos.getSelectionModel().getSelectedItem();


        int rowsAlumno = alumnoSeleccionado.getRow();
        Alumno alumno = new Alumno(dniClick.getText(),nombreClick.getText(),apellido_1Click.getText(),apellido_2Click.getText(),
                direccionClick.getText(),localidadClick.getText(),provinciaClick.getText(), (java.sql.Date) fechaDate(nacimientoClick.getValue()),rowsAlumno);
        // Llama un método para guardar los datos del alumno
        alumno.guardarAlumno();
        limpiarAlumno();
        cargarDatos();

    }

    public void eliminarAlumnoSeleccionado(){
        Alumno alumnoSeleccionado = tabla_alumnos.getSelectionModel().getSelectedItem();
        int rowsAlumno = alumnoSeleccionado.getRow();

        Alumno alumno = new Alumno();
        alumno.eliminarAlumno(rowsAlumno);
        limpiarAlumno();
        cargarDatos();
    }

    //HACE QUE CAMBIE EL NUMERO Y A LA VEZ SE LO MANDE AL RESULTSET
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

    //BOTONES PARA PODER NAVEGAR POR LA NAV
    @FXML
    private void botonHome() {
        setBotonActivo(botonHome, panelHome);
        botonAlumnos.setStyle("");
        botonAdd.setStyle("");
        botonPerfil.setStyle("");
    }

    @FXML
    private void botonAlumnos() {
        setBotonActivo(botonAlumnos, panelEditar);
        botonHome.setStyle("");
        botonAdd.setStyle("");
        botonPerfil.setStyle("");
    }

    @FXML
    private void botonAdd() {
        setBotonActivo(botonAdd, panelAdd);
        botonHome.setStyle("");
        botonAlumnos.setStyle("");
        botonPerfil.setStyle("");

    }

    @FXML
    private void botonPerfil() {
        setBotonActivo(botonPerfil, panelPerfil);
        botonHome.setStyle("");
        botonAlumnos.setStyle("");
        botonAdd.setStyle("");
    }

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


    //COMPLEMENTO PARA LA EDICION DEL ALUMNO

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

    public String fechaString(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(fecha);
    }

    public Date fechaDate(LocalDate fecha) throws ParseException {
        String fechaString = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        java.util.Date utilDate = dateFormat.parse(fechaString);
        return new java.sql.Date(utilDate.getTime());
    }



}
