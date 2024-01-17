package org.practica.proyecto.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.practica.proyecto.models.Alumno;
import java.sql.SQLException;
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
    public TableColumn<Alumno, Date> fecha_nacimiento_tabla;


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

    int maxRegistros = 20;
    int paginaActual = 1;

    public TextField buscarAlumno;


    // Arranca la clase con el initialize
    @FXML
    public void initialize() throws SQLException {
        cargarDatos();
    }


    // Obtiene los datos y los inserta en la tabla
    private void cargarDatos() {


        if(buscarAlumno.getText().isEmpty()){

            botonAlumnos.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 10,0,0,1); -fx-background-color: #181818;");

            // Obtén los datos de los alumnos
            List<Alumno> listaAlumnos = obtenerDatosDeAlumnos(maxRegistros, paginaActual);


            // Limpia la tabla
            tabla_alumnos.getItems().clear();

            // Agrega los datos a la tabla
            tabla_alumnos.getItems().addAll(listaAlumnos);

            // Configura las celdas de las columnas
            dni_tabla.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
            nombre_tabla.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
            apellido_1_tabla.setCellValueFactory(cellData -> cellData.getValue().apellido1Property());
            apellido_2_tabla.setCellValueFactory(cellData -> cellData.getValue().apellido2Property());
            direccion_tabla.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
            localidad_tabla.setCellValueFactory(cellData -> cellData.getValue().localidadProperty());
            provincia_tabla.setCellValueFactory(cellData -> cellData.getValue().provinciaProperty());
            fecha_nacimiento_tabla.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().fechaNacimientoProperty().getValue())
            );
            alumnoClick();
        }else{
            // Limpia la tabla
            tabla_alumnos.getItems().clear();


        }


    }


    //Funcion para saber que alumno se ha clickeado en la tabla
    public void alumnoClick() {
        tabla_alumnos.setRowFactory(tv -> {
            TableRow<Alumno> alumnoClickeado = new TableRow<>();
            alumnoClickeado.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    Alumno alumnoSeleccionado = alumnoClickeado.getItem();
                    if (alumnoSeleccionado != null) {
                        //System.out.println("Clic en la fila. Alumno seleccionado: " + alumnoSeleccionado);
                        System.out.println(dniClick.getText());
                        setTextIfNotNull(dniClick, alumnoSeleccionado.dniProperty());
                        setTextIfNotNull(nombreClick, alumnoSeleccionado.nombreProperty());
                        setTextIfNotNull(apellido_1Click, alumnoSeleccionado.apellido1Property());
                        setTextIfNotNull(apellido_2Click, alumnoSeleccionado.apellido2Property());
                        setTextIfNotNull(direccionClick, alumnoSeleccionado.direccionProperty());
                        setTextIfNotNull(localidadClick, alumnoSeleccionado.localidadProperty());
                        setTextIfNotNull(provinciaClick, alumnoSeleccionado.provinciaProperty());
                        nacimientoClick.setValue(alumnoSeleccionado.fechaNacimientoProperty().get().toLocalDate());
                    }else{
                        System.out.println("nulo");
                    }
                }
            });
            return alumnoClickeado;
        });
    }

    private void setTextIfNotNull(TextInputControl control, StringProperty property) {
        if (property != null && property.get() != null) {
            control.setText(property.get());
        } else {
            control.clear();
        }
    }

    //paginador con los botones de acciones
    @FXML
    private void paginador(ActionEvent event) throws SQLException {
        Button botonPresionado = (Button) event.getSource();
        int totalPaginas = Alumno.contPanginas(maxRegistros);

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

    //Boton para guardar los datos de lo clientes que se hayan modificado de la tabla
    public void guardarAlumno() {

        if (!dniClick.getText().isEmpty()) {
            System.out.println(dniClick.getText());
        } else {
            System.out.println("No hay alumnos");
        }

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

        //Limpia lo seleccionado en la tabla
        dniClick.clear();
        nombreClick.clear();
        apellido_1Click.clear();

        apellido_2Click.clear();
        direccionClick.clear();
        localidadClick.clear();
        provinciaClick.clear();
        nacimientoClick.setValue(null);

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


}
