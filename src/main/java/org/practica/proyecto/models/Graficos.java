package org.practica.proyecto.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class Graficos {

    protected static ResultSet resultSet2 = null;
    protected static Statement statement2 = null;
    private int anioNacimiento;
    private int cantidadAlumnos;

    public Graficos(int anioNacimiento, int cantidadAlumnos) {
        this.anioNacimiento = anioNacimiento;
        this.cantidadAlumnos = cantidadAlumnos;
    }

    public Graficos() {
    }

    public int getAnioNacimiento() {
        return anioNacimiento;
    }


    public int getCantidadAlumnos() {
        return cantidadAlumnos;
    }


    public List<Graficos> obtenerDatosPorAnio() throws SQLException {

        Connection connection;
        try {
            connection = Singleton.obtenerConexion();
            String consultaSQL = "SELECT YEAR(fecha_nacimiento) AS año_nacimiento, COUNT(*) AS cantidad_alumnos FROM alumno GROUP BY YEAR(fecha_nacimiento)";

            statement2 = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet2 = statement2.executeQuery(consultaSQL);

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        List<Graficos> resultados = new ArrayList<>();

            while (resultSet2.next()) {
                anioNacimiento = resultSet2.getInt("año_nacimiento");
                cantidadAlumnos = resultSet2.getInt("cantidad_alumnos");

                Graficos graficos = new Graficos(anioNacimiento, cantidadAlumnos);
                resultados.add(graficos);
            }


        return resultados;
    }

}

