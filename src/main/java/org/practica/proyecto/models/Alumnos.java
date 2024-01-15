package org.practica.proyecto.models;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Alumnos {

    static Singleton singleton = new Singleton();
    private SimpleStringProperty dni;
    private SimpleStringProperty nombre;
    private SimpleStringProperty apellido1;
    private SimpleStringProperty apellido2;
    private SimpleStringProperty direccion;
    private SimpleStringProperty localidad;
    private SimpleStringProperty provincia;
    private SimpleObjectProperty<Date> fechaNacimiento;

    public Alumnos(String dni, String apellido1, String apellido2, String nombre, String direccion, String localidad, String provincia, Date fechaNacimiento) {
        this.dni = new SimpleStringProperty(dni);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido1 = new SimpleStringProperty(apellido1);
        this.apellido2 = new SimpleStringProperty(apellido2);
        this.direccion = new SimpleStringProperty(direccion);
        this.localidad = new SimpleStringProperty(localidad);
        this.provincia = new SimpleStringProperty(provincia);
        this.fechaNacimiento = new SimpleObjectProperty<>(fechaNacimiento);
    }



    public SimpleStringProperty dniProperty() {
        return dni;
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public SimpleStringProperty apellido1Property() {
        return apellido1;
    }

    public SimpleStringProperty apellido2Property() {
        return apellido2;
    }

    public SimpleStringProperty direccionProperty() {
        return direccion;
    }

    public SimpleStringProperty localidadProperty() {
        return localidad;
    }

    public SimpleStringProperty provinciaProperty() {
        return provincia;
    }

    public SimpleObjectProperty<Date> fechaNacimientoProperty() {
        return fechaNacimiento;
    }

    public static List<Alumnos> obtenerDatosDeAlumnos() {

        Connection connection = singleton.obtenerConexion();
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<Alumnos> listaAlumnos = new ArrayList<>();

        try {
            // Consulta SQL para obtener todos los datos de la tabla "alumno"
            preparedStatement = connection.prepareStatement("SELECT * FROM alumno");
            resultSet = preparedStatement.executeQuery();


            int count = 0;
            // Procesar los resultados
            while (resultSet.next()&& count < 3) {

                // Crear un nuevo objeto Alumnos para cada fila y almacenarlo en la lista
                Alumnos alumno = new Alumnos(

                        resultSet.getString("dni"),
                        resultSet.getString("apellido_1"),
                        resultSet.getString("apellido_2"),
                        resultSet.getString("nombre"),
                        resultSet.getString("direccion"),
                        resultSet.getString("localidad"),
                        resultSet.getString("provincia"),
                        resultSet.getDate("fecha_nacimiento")

                );
                count++;
                int rowNum = resultSet.getRow();
                System.out.println("Fila actual: " + rowNum +" "+  resultSet.getString("dni"));
                listaAlumnos.add(alumno);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar ResultSet y PreparedStatement en el bloque finally
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Devolver la lista de alumnos
        return listaAlumnos;
    }


}
