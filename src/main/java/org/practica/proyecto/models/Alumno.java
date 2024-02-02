package org.practica.proyecto.models;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Alumno {


    //VARIABLES
    protected static ResultSet resultSet = null;
    protected static Statement statement = null;

    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String direccion;
    private String localidad;
    private String provincia;
    private Date fechaNacimiento;
    private Blob fotoPerfil;
    private int row;

    //CONSTRUCTORES


    public Alumno(String dni, String nombre, String apellido1, String apellido2, String direccion, String localidad, String provincia, Date fechaNacimiento, Blob fotoPerfil, int row) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.direccion = direccion;
        this.localidad = localidad;
        this.provincia = provincia;
        this.fechaNacimiento = fechaNacimiento;
        this.fotoPerfil = fotoPerfil;
        this.row = row;
    }

    public Alumno() {}

    //GETTER Y SETTERS

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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Blob getFotoPerfil() {
        return fotoPerfil;
    }

    public int getRow() {
        return row;
    }


    //FUNCIONES DE LA CLASE ALUMNO--------------------------------------------------------------------------------------

    //Bolean pasa saber que sql va a utilizar
    private static boolean resultSetInicializado = false;

    //Se comparará para que no sea el mismo filtro
    private static String filtroAnterior = "";

    //Dependiendo del filtro cambiará la sql
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
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_1"),
                        resultSet.getString("apellido_2"),
                        resultSet.getString("direccion"),
                        resultSet.getString("localidad"),
                        resultSet.getString("provincia"),
                        resultSet.getDate("fecha_nacimiento"),
                        resultSet.getBlob("foto_perfil"),
                        resultSet.getRow()

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
        int total;
        int count2 = 0;

        resultSet.beforeFirst();
        while (resultSet.next()) {
            count2++;
        }

        total = (int) Math.ceil((double) count2 / maxRegistros);

        System.out.println("Total de páginas: " + total);
        return total;
    }

    //Cuenta el total de Alumnos que trae el resultset
    public static int totalRegistros() throws SQLException {
        int total = 0;

        resultSet.absolute(0);
        while (resultSet.next()) {
            total++;
        }

        return total;
    }

    //Guarda el Alumno y ejecuta el resultset
    public void actualizarAlumno() {
        try {
            //Posiciona el resultset
            resultSet.absolute(getRow());

            // Actualiza los valores en el ResultSet
            resultSet.updateString("nombre", nombre.toUpperCase());
            resultSet.updateString("apellido_1", apellido1.toUpperCase());
            resultSet.updateString("apellido_2", apellido2.toUpperCase());
            resultSet.updateString("direccion", direccion.toUpperCase());
            resultSet.updateString("localidad", localidad.toUpperCase());
            resultSet.updateString("provincia", provincia.toUpperCase());
            resultSet.updateDate("fecha_nacimiento", fechaNacimiento);
            resultSet.updateBlob("foto_perfil",fotoPerfil);

            // Actualiza la fila en la base de datos
            resultSet.updateRow();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Elimina el Alumno y ejecuta el resultset
    public boolean eliminarAlumno(int row) {
        try {
            // Posiciona el resultset
            resultSet.absolute(row);

            // Elimina la row seleccionada
            resultSet.deleteRow();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar alumno: " + e.getMessage());
            return false;  // Indica que la eliminación falló
        }
    }

    //Inserta el Alumno y ejecuta el resultset
    public boolean insertarAlumno(){
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
            resultSet.updateDate("fecha_nacimiento", fechaNacimiento);
            resultSet.updateBlob("foto_perfil",fotoPerfil);

            // Insertar la nueva fila en la base de datos
            resultSet.insertRow();


        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    //Lo que contenga el resulset de datos lo exporta a CSV
    public static boolean exportToCSV() {
        // Crear un FileChooser para permitir al usuario elegir la ubicación y el nombre del archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        // Si el usuario selecciona un archivo y hace clic en "Guardar"
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                ResultSetMetaData metaData = resultSet.getMetaData();

                // Escribir encabezados
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnLabel(i);
                    if (!columnName.equalsIgnoreCase("FOTO_PERFIL")) {
                        writer.write(columnName);
                        if (i < metaData.getColumnCount()) {
                            writer.write(";"); // Delimitador personalizado
                        }
                    }
                }
                writer.newLine();

                resultSet.absolute(0);

                // Escribir datos
                while (resultSet.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnName(i);
                        if (!columnName.equalsIgnoreCase("FOTO_PERFIL")) {
                            String value = resultSet.getString(i);
                            if (columnName.equalsIgnoreCase("direccion")) {
                                // Aplicar filtros solo a la columna "direccion"
                                value = value.replace(",", " -"); // Reemplazar comas por guiones
                            }
                            writer.write(value);
                            if (i < metaData.getColumnCount()) {
                                writer.write(";"); // Delimitador personalizado
                            }
                        }
                    }
                    writer.newLine();
                }
                return true;
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //Lo que contenga el resulset de datos lo exporta a PDF
    public static boolean exportarToPDF(int row) throws SQLException {

        // Posiciona el resultset
        resultSet.absolute(row);

        Document document = new Document();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("alumnoPDF.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Crear un párrafo para la imagen
                Paragraph imageParagraph = new Paragraph();
                Blob fotoBlob = resultSet.getBlob("FOTO_PERFIL");
                if (fotoBlob != null) {
                    BufferedImage bufferedImage = ImageIO.read(fotoBlob.getBinaryStream());
                    Image image = Image.getInstance(bufferedImage, null);
                    image.scaleToFit(400, 400);
                    image.setAlignment(Element.ALIGN_CENTER);
                    imageParagraph.add(image);
                    document.add(imageParagraph);
                } else {
                    // Si la imagen es nula, cargar una imagen predeterminada
                    try (InputStream inputStream = Alumno.class.getResourceAsStream("/org/practica/proyecto/imagen/perfilSinFoto.png")) {
                        if (inputStream != null) {
                            BufferedImage defaultImage = ImageIO.read(inputStream);
                            if (defaultImage != null) {
                                Image defaultPdfImage = Image.getInstance(defaultImage, null);
                                defaultPdfImage.scaleToFit(400, 400);
                                defaultPdfImage.setAlignment(Element.ALIGN_CENTER);
                                imageParagraph.add(defaultPdfImage);
                                document.add(imageParagraph);
                            }
                        }
                    } catch (IOException | BadElementException e) {
                        e.printStackTrace();
                    }
                }

                // Espacio entre la imagen y el contenido de texto
                document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);

                // Crear un párrafo para los campos y valores
                Paragraph dataParagraph = new Paragraph();
                dataParagraph.setAlignment(Element.ALIGN_CENTER);

                String[] campos = {"DNI", "NOMBRE", "1º APELLIDO", "2º APELLIDO", "DIRECCIÓN", "LOCALIDAD", "PROVINCIA", "FECHA DE NACIMIENTO"};

                String[] valores = {
                        resultSet.getString("dni"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_1"),
                        resultSet.getString("apellido_2"),
                        resultSet.getString("direccion"),
                        resultSet.getString("localidad"),
                        resultSet.getString("provincia"),
                        resultSet.getDate("fecha_nacimiento").toString()
                };

                // Agregar cada campo y su valor con un estilo adecuado
                for (int i = 0; i < campos.length; i++) {
                    dataParagraph.add(Chunk.NEWLINE);
                    Chunk campoChunk = new Chunk(campos[i] + ": ", FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD));
                    Chunk valorChunk = new Chunk(valores[i].toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA, 15));

                    // Alinear campos y valores en el mismo renglón
                    dataParagraph.add(campoChunk);
                    dataParagraph.add(valorChunk);
                    dataParagraph.add(Chunk.NEWLINE);
                }

                document.add(dataParagraph);
                document.close();
                return true;
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }





}
