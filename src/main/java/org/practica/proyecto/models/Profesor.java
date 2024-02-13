package org.practica.proyecto.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;

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
    private int admin;

    //CONSTRUCTORES


    public Profesor(String dni, String nombre, String apellido1, String apellido2, String direccion, String localidad, String provincia, Date fechaIngreso, String contrasenya, Blob fotoPerfil, int admin) {
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
        this.admin = admin;
    }

    public Profesor() {

    }

    //GETTERS Y SETTERS

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public Blob getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Blob fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }


    //FUNCIONES PROFESOR


    //Comprueba si hay un usuario con los datos introducidos
    public Profesor checkUser(String inputUsuario, String inputContrasenya) {
        Connection connection;
        String consultaSQL = "SELECT * FROM profesor WHERE dni = ? AND contrasenya = ?";

        try {
            // Obtener la conexión desde el Singleton
            connection = Singleton.obtenerConexion();

            // Preparar la consulta SQL
            preparedStatement = connection.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1, inputUsuario);
            preparedStatement.setString(2, hashPassword(inputContrasenya));

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
                        resultSet.getBlob("foto_perfil"),
                        resultSet.getInt("admin")
                );
                return profesor;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error al ejecutar la consulta.");
        }

        return null;
    }

    //Modifica los datos de un profesor
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

    //Crea un usuario profesor nuevo
    public boolean insertarProfesor(){

        try {
            // Mover el cursor a la fila de inserción
            resultSet.moveToInsertRow();

            resultSet.updateString("dni", dni.toUpperCase());
            resultSet.updateString("nombre", nombre.toUpperCase());
            resultSet.updateString("apellido_1", apellido1.toUpperCase());
            resultSet.updateString("apellido_2", apellido2.toUpperCase());
            resultSet.updateString("direccion", direccion.toUpperCase());
            resultSet.updateString("localidad", localidad.toUpperCase());
            resultSet.updateString("provincia", provincia.toUpperCase());
            resultSet.updateString("contrasenya",hashPassword(contrasenya));
            resultSet.updateInt("admin",admin);
            resultSet.updateDate("fecha_ingreso", fechaIngreso);
            resultSet.updateBlob("foto_perfil",fotoPerfil);

            // Insertar la nueva fila en la base de datos
            resultSet.insertRow();

        } catch (SQLException e) {
            return false;
        }

        return true;

    }

    //Hashea la contraseña
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());

            // Convertir el hash a una cadena hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Timestamp obtenerFechaToken() {
        try {

            resultSet.absolute(1);

            return resultSet.getTimestamp("fecha_token");

        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir la traza de la excepción (puedes cambiar esto según tus necesidades)
            return null; // Devolver un valor predeterminado o manejar el error de otra manera según tu lógica de negocio
        }
    }

    public static void insertarFechaToken() {

        try {
            // Mover el cursor a la fila de inserción
            resultSet.absolute(1);

            // Obtener la fecha y hora actual
            LocalDateTime fechaHoraActual = LocalDateTime.now();

            // Agregar 15 minutos a la fecha y hora actual
            LocalDateTime fechaHoraToken = fechaHoraActual.plusMinutes(15);

            // Convertir LocalDateTime a Timestamp
            Timestamp nuevaFechaToken = Timestamp.valueOf(fechaHoraToken);

            resultSet.updateTimestamp("fecha_token",nuevaFechaToken);

            // Insertar la nueva fila en la base de datos
            resultSet.updateRow();

        } catch (SQLException e) {

        }




    }
}
