package org.practica.proyecto.models;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Alumno {


    //Variables
    protected static ResultSet resultSet = null;
    protected static Statement statement = null;

    private SimpleStringProperty dni;
    private SimpleStringProperty nombre;
    private SimpleStringProperty apellido1;
    private SimpleStringProperty apellido2;
    private SimpleStringProperty direccion;
    private SimpleStringProperty localidad;
    private SimpleStringProperty provincia;
    private SimpleObjectProperty<Date> fechaNacimiento;


    //CONSTRUCTORES
    public Alumno(String dni, String apellido1, String apellido2, String nombre, String direccion, String localidad, String provincia, Date fechaNacimiento) {
        this.dni = new SimpleStringProperty(dni);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido1 = new SimpleStringProperty(apellido1);
        this.apellido2 = new SimpleStringProperty(apellido2);
        this.direccion = new SimpleStringProperty(direccion);
        this.localidad = new SimpleStringProperty(localidad);
        this.provincia = new SimpleStringProperty(provincia);
        this.fechaNacimiento = new SimpleObjectProperty<>(fechaNacimiento);
    }

    public Alumno() {

    }

    //GETTER Y SETTERS

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

    static {

    }

    //FUNCIONES DE LA CLASE ALUMNO

    //Inicia la sql del resultset
    private static boolean resultSetInicializado = false;
    private static String filtroAnterior = "";

    private static void initResultSet(String filtro) {
        Connection connection;
        try {
            connection = Singleton.obtenerConexion();
            String consultaSQL = "SELECT * FROM alumno";

            if (!resultSetInicializado && (filtro == null || filtro.isEmpty())) {
                // Inicializar el ResultSet sin filtro
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                resultSet = statement.executeQuery(consultaSQL);
                System.out.println("Datos cogidos de la base de datos de alumno");
                resultSetInicializado = true;
            }
            if (filtro != null && !filtro.isEmpty() && !filtro.equals(filtroAnterior)) {
                // Inicializar el ResultSet con filtro si el filtro actual es diferente al anterior
                consultaSQL += " WHERE dni LIKE ? OR nombre LIKE ? OR apellido_1 LIKE ? OR apellido_2 LIKE ? OR direccion LIKE ? OR localidad LIKE ? OR provincia LIKE ? OR fecha_nacimiento LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(consultaSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    for (int i = 1; i <= 8; i++) {
                        preparedStatement.setString(i, "%" + filtro + "%");
                    }
                    resultSet = preparedStatement.executeQuery();
                    System.err.println("Datos filtrados cogidos de la base de datos de alumno");
                    resultSetInicializado = false;
                    filtroAnterior = filtro; // Almacenar el nuevo filtro actual

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Carga de la base de datos al resultset todos los datos de la consulta que se haya pedido
    public static List<Alumno> obtenerDatosDeAlumnos(int maxRegistros,int paginaActual,String filtro){
        initResultSet(filtro);
        List<Alumno> listaAlumnos = new ArrayList<>();
        try {
            int count = 0;
            posicionarResultSet(maxRegistros, paginaActual);
            // Procesar los resultados
            while (resultSet.next()&& count < maxRegistros) {

                // Crear un nuevo objeto Alumnos para cada fila y almacenarlo en la lista
                Alumno alumno = new Alumno(
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

                listaAlumnos.add(alumno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Devolver la lista de alumnos
        return listaAlumnos;
    }

    //Posiciona el cursor del resulset dependiendo en que pagina está
    public static void posicionarResultSet(int registrosPorPagina, int paginaActual) throws SQLException {
        // Calcula la posición inicial para la página actual

        int posicionInicial = (paginaActual - 1) * registrosPorPagina;

        // Mueve el cursor al inicio del conjunto de resultados si es de tipo SCROLL_SENSITIVE
        resultSet.beforeFirst();

        // Salta a la posición inicial
        for (int i = 0; i < posicionInicial; i++) {
            if (!resultSet.next()) {
                // Manejar casos donde la posición excede el tamaño total de resultados
                throw new SQLException("La posición especificada excede el tamaño total de resultados.");
            }
        }
    }

    //Cuenta el total de pagina de todos los registros que se haya traido
    public static int contPaginas(int maxRegistros) throws SQLException {
        int total = 0;
        int count2 = 0;

        resultSet.beforeFirst();
        while (resultSet.next()) {
            count2++;
        }

        total = (int) Math.ceil((double) count2 / maxRegistros);

        System.out.println("Total de páginas: " + total);
        return total;
    }




}