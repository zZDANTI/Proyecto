package org.practica.proyecto.models;

import java.sql.*;

public class Singleton {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/universidad";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "";

    private static Connection conexion;

    public static Connection obtenerConexion() {
        if (conexion == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
                System.out.println("Conexión exitosa a la base de datos.");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al conectar a la base de datos.");
            }
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al cerrar la conexión.");
            }
        }
    }
/*
    public static void main(String[] args) {
        try {
            Connection conexion = obtenerConexion();

            // Ejemplo de consulta
            String consultaSQL = "SELECT * FROM alumno";
            try (PreparedStatement statement = conexion.prepareStatement(consultaSQL);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Aquí puedes procesar los resultados de la consulta
                    String dni = resultSet.getString("dni");
                    String nombre = resultSet.getString("nombre");
                    System.out.println("DNI: " + dni + ", Nombre: " + nombre);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error al ejecutar la consulta.");
            }

            // Puedes realizar otras operaciones en la base de datos aquí, como inserciones o actualizaciones

            // Cerrar la conexión al finalizar
            cerrarConexion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 */
}
