package org.practica.proyecto.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private Singleton singleton;
    private String usuario;
    private String contrasenya;
    //private String token;
    //private String tokenValido;

    public User(String usuario, String contrasenya) {
        this.singleton = new Singleton();
        this.usuario = usuario;
        this.contrasenya = contrasenya;
    }

    public User checkUser(String inputUsuario, String inputContrasenya) {
        Connection connection = singleton.obtenerConexion();


        // Consulta SQL para verificar si el usuario y la contraseña coinciden
        String consultaSQL = "SELECT * FROM usuarios WHERE usuario = ? AND contrasenya = ?";
        try (PreparedStatement statement = connection.prepareStatement(consultaSQL)) {
            statement.setString(1, inputUsuario);
            statement.setString(2, inputContrasenya);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Si hay resultados, crea y devuelve un objeto User
                    User user = new User(inputUsuario, inputContrasenya);
                    // Establecer otros atributos según sea necesario
                    //user.setToken(resultSet.getString("token"));
                    //user.setTokenValido(resultSet.getString("tokenValido"));
                    return user;
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
