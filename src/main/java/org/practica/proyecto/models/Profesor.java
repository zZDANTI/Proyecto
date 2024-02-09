package org.practica.proyecto.models;

import java.sql.*;

public class Profesor {


    //VARIABLES

    protected static ResultSet resultSet = null;
    protected static PreparedStatement preparedStatement = null;
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String direccion;
    private String localidad;
    private String provincia;
    private Date fechaIngreso;
    private String contrasenya;
    private Blob fotoPerfil;

    //CONSTRUCTORES


    public Profesor(String dni, String nombre, String apellido1, String apellido2, String direccion, String localidad, String provincia, Date fechaIngreso, String contrasenya, Blob fotoPerfil) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.direccion = direccion;
        this.localidad = localidad;
        this.provincia = provincia;
        this.fechaIngreso = fechaIngreso;
        this.contrasenya = contrasenya;
        this.fotoPerfil = fotoPerfil;
    }


    public Profesor() {

    }

    //GETTERS Y SETTERS

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public Blob getFotoPerfil() {
        return fotoPerfil;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setFotoPerfil(Blob fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    //FUNCIONES PROFESOR

    public Profesor checkUser(String inputUsuario, String inputContrasenya) {
        Connection connection;
        String consultaSQL = "SELECT * FROM profesor WHERE dni = ? AND contrasenya = ?";

        try {
            // Obtener la conexión desde el Singleton
            connection = Singleton.obtenerConexion();

            // Preparar la consulta SQL
            preparedStatement = connection.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, inputUsuario);
            preparedStatement.setString(2, inputContrasenya);

            // Ejecutar la consulta
            resultSet = preparedStatement.executeQuery();

            // Verificar si se encontró un profesor con las credenciales proporcionadas
            if (resultSet.next()) {
                Profesor profesor = new Profesor(
                        resultSet.getString("dni"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_1"),
                        resultSet.getString("apellido_2"),
                        resultSet.getString("direccion"),
                        resultSet.getString("localidad"),
                        resultSet.getString("provincia"),
                        resultSet.getDate("fecha_ingreso"),
                        resultSet.getString("contrasenya"),
                        resultSet.getBlob("foto_perfil")
                );
                return profesor;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error al ejecutar la consulta.");
        }

        return null;
    }

    public boolean actualizarProfesor() {
        try {

            resultSet.absolute(1);

            // Actualiza los valores en el ResultSet
            resultSet.updateString("nombre", nombre.toUpperCase());
            resultSet.updateString("apellido_1", apellido1.toUpperCase());
            resultSet.updateString("apellido_2", apellido2.toUpperCase());
            resultSet.updateString("direccion", direccion.toUpperCase());
            resultSet.updateString("localidad", localidad.toUpperCase());
            resultSet.updateString("provincia", provincia.toUpperCase());
            resultSet.updateBlob("foto_perfil",fotoPerfil);

            // Actualiza la fila en la base de datos
            resultSet.updateRow();

            return true;

        } catch (SQLException e ) {
            String mensaje = e.getMessage();
            System.out.println("Error " + mensaje);

        }
        return false;
    }


}