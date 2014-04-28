package trabajo2;

import config.ConstantesConn;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class SQLJava {

    private Connection conexion;

    public boolean abrirConexion() {
        cargarDriver();
        if (conexion == null) {
            try { // Se establece la conexión con la base de datos
                conexion = DriverManager.getConnection("jdbc:oracle:thin:@" + ConstantesConn.BASEDATOS + ":" + ConstantesConn.PUERTO + ":xe", ConstantesConn.USUARIO, ConstantesConn.PASSWORD);
                return true;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "No hay conexión a la BD, saliendo de la aplicación");
                System.out.println("No hay conexión con la base de datos.");
                System.exit(-1);
                return false;
            }
        }
        try {
            if (conexion.isClosed()) {
                conexion = DriverManager.getConnection("jdbc:oracle:thin:@" + ConstantesConn.BASEDATOS + ":" + ConstantesConn.PUERTO + ":xe", ConstantesConn.USUARIO, ConstantesConn.PASSWORD);
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No hay conexión a la BD");
            System.out.println("No hay conexión con la base de datos.");
            System.exit(-1);
            return false;
        }
        return true;
    }

    public ResultSet ejecutarSelect(String consulta) throws SQLException {
        if (abrirConexion()) {
            Statement query = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            return query.executeQuery(consulta);
        }
        return null;
    }

    public void ejecutarUpdate(String consulta) throws SQLException {
        if (abrirConexion()) {
            Statement query = conexion.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            query.executeUpdate(consulta);
        }
    }

    public HashMap<Integer, Ciudad> listarCiudades() {
        HashMap<Integer, Ciudad> ciudades = new HashMap<>();
        String query = "SELECT id, nombre, (SELECT COUNT(1) FROM (TABLE(SELECT mis_escenarios from ciudad where id= c.id)  t2)) escenarios from ciudad c";
        try {
            ResultSet resultados = ejecutarSelect(query);
            while (resultados.next()) {
                ciudades.put(resultados.getInt("id"), new Ciudad(resultados.getInt("id"), resultados.getInt("escenarios"), resultados.getString("nombre")));
            }
            resultados.close();
        } catch (SQLException ex) {
            return null;
        }

        return ciudades;
    }

    public Escenario getEscenario(int idCiudad, int escenario) {
        return new Escenario(escenario, getEdificiosByIdCiudad(idCiudad, escenario), getHuecosByIdCiudad(idCiudad, escenario));
    }

    public HashMap<String, BufferedImage> getImagenesByIdCiudad(int idCiudad) {
        HashMap<String, BufferedImage> imagenes = new HashMap<>();
        String xml = "img.iconos";
        try {
            int i = 1;
            while (true) {
                String query = "SELECT EXISTSNODE(img.iconos,'//elemento[" + i + "]') existe, EXTRACTVALUE(img.iconos,'//elemento[" + i + "]/tipo') tipo, EXTRACTVALUE(img.iconos,'//elemento[" + i + "]/ruta') ruta  FROM icono img WHERE idciudad = " + idCiudad;
                i++;
                ResultSet consulta = ejecutarSelect(query);
                consulta.next();
                try {
                    if (consulta.getInt("existe") == 1) {
                        imagenes.put(consulta.getString("tipo").trim(), ImageIO.read(new File(consulta.getString("ruta"))));
                    } else {
                        break;
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "No se encntró la imagen " + consulta.getString("ruta") + " en el sistema", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {

        }

        return imagenes;
    }

    public void cerrarConexion() throws SQLException {
        conexion.close();
    }

    private String getNestedTableByIdCiudad(int id) {
        return "TABLE(SELECT mis_escenarios FROM ciudad WHERE id = " + id + ")  t2";
    }

    private String queryExtractValue(String xml, String topTag, String campoxml, int index) {
        String indexs = index == -1 ? "last()" : Integer.toString(index);
        return " EXTRACTVALUE(value(" + xml + "),'//" + topTag + "[" + indexs + "]/" + campoxml + "') " + campoxml;
    }

    private List<Elemento> getEdificiosByIdCiudad(int id, int escenario) {
        ArrayList<Elemento> edificios = new ArrayList<>();
        String ed = "edificio";
        String xml = "t2";
        try {
            String query = "SELECT " + queryExtractValue(xml, ed, "x1", -1) + ", " + queryExtractValue(xml, ed, "y1", -1) + " FROM " + getNestedTableByIdCiudad(id);
            ResultSet resultado = ejecutarSelect(query);

            if (!resultado.next()) {
                return null;
            }
            resultado.relative(escenario - 1);
            int x1f = resultado.getInt("x1");
            int y1f = resultado.getInt("y1");
            resultado.close();
            int x1 = -1, y1 = -1, i = 1;
            do {
                query = "SELECT " + queryExtractValue(xml, ed, "x1", i) + ", " + queryExtractValue(xml, ed, "y1", i) + ", "
                        + queryExtractValue(xml, ed, "x2", i) + ", " + queryExtractValue(xml, ed, "y2", i) + ", "
                        + queryExtractValue(xml, ed, "nombre", i) + ", " + queryExtractValue(xml, ed, "tipo", i) + " FROM " + getNestedTableByIdCiudad(id);
                resultado = ejecutarSelect(query);

                if (!resultado.next()) {
                    break;
                }
                resultado.relative(escenario - 1);
                x1 = resultado.getInt("x1");
                y1 = resultado.getInt("y1");
                Elemento edificio = new Elemento(x1, y1, resultado.getInt("x2"), resultado.getInt("y2"), resultado.getString("nombre").trim(), resultado.getString("tipo").trim(), i);
                edificios.add(edificio);
                resultado.close();
                i++;
            } while (!(x1f == x1 && y1 == y1f));

        } catch (SQLException ex) {
            Logger.getLogger(SQLJava.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return edificios;
    }

    private List<Elemento> getHuecosByIdCiudad(int id, int escenario) {
        ArrayList<Elemento> huecos = new ArrayList<>();
        String hueco = "hueco";
        String xml = "t2";
        try {
            String query = "SELECT " + queryExtractValue(xml, hueco, "x1", -1) + ", " + queryExtractValue(xml, hueco, "y1", -1) + " FROM " + getNestedTableByIdCiudad(id);
            ResultSet resultado = ejecutarSelect(query);

            if (!resultado.next()) {
                return null;
            }
            resultado.relative(escenario - 1);
            int x1f = resultado.getInt("x1");
            int y1f = resultado.getInt("y1");
            resultado.close();
            int x1 = -1, y1 = -1, i = 1;
            do {
                query = "SELECT " + queryExtractValue(xml, hueco, "x1", i) + ", " + queryExtractValue(xml, hueco, "y1", i) + ", "
                        + queryExtractValue(xml, hueco, "x2", i) + ", " + queryExtractValue(xml, hueco, "y2", i)
                        + " FROM " + getNestedTableByIdCiudad(id);
                resultado = ejecutarSelect(query);

                if (!resultado.next()) {
                    break;
                }
                resultado.relative(escenario - 1);
                x1 = resultado.getInt("x1");
                y1 = resultado.getInt("y1");
                Elemento edificio = new Elemento(x1, y1, resultado.getInt("x2"), resultado.getInt("y2"), hueco, hueco, i);
                huecos.add(edificio);
                resultado.close();
                i++;
            } while (!(x1f == x1 && y1 == y1f));

        } catch (SQLException ex) {
            Logger.getLogger(SQLJava.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return huecos;
    }

    private void cargarDriver() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("No se pudo cargar el driver JDBC");
        }
    }

}
