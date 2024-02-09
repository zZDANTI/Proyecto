package org.practica.proyecto.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Profesor {
    private Singleton singleton;
    private String dni;
    private String contrasenya;
    //private String token;
    //private String tokenValido;


    public Profesor(String usuario, String contrasenya) {
        this.dni = usuario;
        this.contrasenya = contrasenya;
    }

    public Profesor() {

    }

    public Profesor checkUser(String inputUsuario, String inputContrasenya) {
        Connection connection = singleton.obtenerConexion();


        // Consulta SQL para verificar si el usuario y la contraseña coinciden
        String consultaSQL = "SELECT * FROM profesor WHERE dni = ? AND contrasenya = ?";
        try (PreparedStatement statement = connection.prepareStatement(consultaSQL)) {
            statement.setString(1, inputUsuario);
            statement.setString(2, inputContrasenya);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Si hay resultados, crea y devuelve un objeto User
                    Profesor profesor = new Profesor(inputUsuario, inputContrasenya);
                    // Establecer otros atributos según sea necesario
                    //user.setToken(resultSet.getString("token"));
                    //user.setTokenValido(resultSet.getString("tokenValido"));
                    return profesor;
                } else {
                    // Si no hay resultados, devuelve null
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al ejecutar la consulta.");
        }
    }

}
