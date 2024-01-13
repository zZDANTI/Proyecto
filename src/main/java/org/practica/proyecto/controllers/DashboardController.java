package org.practica.proyecto.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.practica.proyecto.models.Alumnos;

import java.util.Date;
import java.util.List;

import static org.practica.proyecto.models.Alumnos.obtenerDatosDeAlumnos;

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
    public TableView<Alumnos> tabla_alumnos;
    public TableColumn<Alumnos, String> dni_tabla;
    public TableColumn<Alumnos, String> nombre_tabla;
    public TableColumn<Alumnos, String> apellido_1_tabla;
    public TableColumn<Alumnos, String> apellido_2_tabla;
    public TableColumn<Alumnos, String> direccion_tabla;
    public TableColumn<Alumnos, String> localidad_tabla;
    public TableColumn<Alumnos, String> provincia_tabla;
    public TableColumn<Alumnos, Date> fecha_nacimiento_tabla;




    // Instancia de la clase Alumnos


    @FXML
    public void initialize() {

        // Obtén los datos de los alumnos
        List<Alumnos> listaAlumnos = obtenerDatosDeAlumnos();

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

    }

    //BOTONES PARA PODER NAVEGAR
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




}
