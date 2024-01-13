package org.practica.proyecto.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.practica.proyecto.models.Alumnos;

import java.util.Date;
import java.util.List;

import static org.practica.proyecto.models.Alumnos.obtenerDatosDeAlumnos;

public class DashboardController {
    public Button home;

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



}
