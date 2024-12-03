package org.practica.proyecto.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class Graficos {

    protected static ResultSet resultSet = null;
    protected static Statement statement = null;
    private String cantidadBuscada;
    private int cantidadAlumnos;

    public Graficos(String cantidadBuscada, int cantidadAlumnos) {
        this.cantidadBuscada = cantidadBuscada;
        this.cantidadAlumnos = cantidadAlumnos;
    }

    public Graficos() {
    }

    public String getCantidadBuscada() {
        return cantidadBuscada;
    }

    public int getCantidadAlumnos() {
        return cantidadAlumnos;
    }

    public List<Graficos> graficoAnios() throws SQLException {

        Connection connection;
        try {
            connection = Singleton.obtenerConexion();
            String consultaSQL = "SELECT YEAR(fecha_nacimiento) AS año_nacimiento, COUNT(*) AS cantidad_alumnos FROM alumno GROUP BY YEAR(fecha_nacimiento)";

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(consultaSQL);

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        List<Graficos> resultados = new ArrayList<>();

            while (resultSet.next()) {
                cantidadBuscada = resultSet.getString("año_nacimiento");
                cantidadAlumnos = resultSet.getInt("cantidad_alumnos");

                Graficos graficos = new Graficos(cantidadBuscada, cantidadAlumnos);
                resultados.add(graficos);
            }


        return resultados;
    }

    public List<Graficos> graficoProvincia() throws SQLException {
        Connection connection;
        try {
            connection = Singleton.obtenerConexion();
            String consultaSQL = "SELECT provincia, COUNT(*) AS cantidad_alumnos FROM alumno GROUP BY provincia;";

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultSet = statement.executeQuery(consultaSQL);

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        List<Graficos> resultados = new ArrayList<>();

        while (resultSet.next()) {
            String localidad = resultSet.getString("provincia");
            int cantidadAlumnos = resultSet.getInt("cantidad_alumnos");

            Graficos graficos = new Graficos(localidad, cantidadAlumnos);
            resultados.add(graficos);
        }

        return resultados;
    }




}

