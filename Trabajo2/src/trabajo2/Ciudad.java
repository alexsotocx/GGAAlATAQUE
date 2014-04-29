package trabajo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Ciudad {

    private int id;
    private int numeroEscenarios;
    private String nombre;
    private Escenario escenarioActual;
    private int[][] matrizActual;
    public static HashMap<String, TexturePaint> texturas;

    public Ciudad(int id, int numeroEscenarios, String nombre) {
        this.id = id;
        this.numeroEscenarios = numeroEscenarios;
        this.nombre = nombre;
        matrizActual = new int[101][101];
    }

    public static void dibujarSuelo(Graphics2D graficos) {
        Rectangle rectangulo = new Rectangle(0, 0, 100 * Elemento.TAMANOPIXEL, 100 * Elemento.TAMANOPIXEL);
        graficos.setPaint(texturas.get("calle"));
        graficos.fill(rectangulo);
        graficos.setColor(Color.black);
        graficos.setStroke(new BasicStroke(2));
        graficos.draw(rectangulo);

    }

    public static  void cargarTexturas() {
        texturas = new HashMap<>();
        texturas.put("calle", crearTextura("img/calle.png"));
        texturas.put("ladrillo", crearTextura("img/ladrillo.png"));
        texturas.put("hueco", crearTextura("img/hueco.png"));
    }

    private  static TexturePaint crearTextura(String ruta) {
        BufferedImage imgTextura = null;
        try {
            imgTextura = ImageIO.read(new File(ruta));
        } catch (Exception ex) {
            System.out.println("Error creando la textura");
        }
        BufferedImage imgTexPaint = new BufferedImage(imgTextura.getWidth(), imgTextura.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imgTexPaint.createGraphics();
        g2.drawImage(imgTextura, 0, 0, null);
        g2.dispose();
        return new TexturePaint(imgTexPaint, new Rectangle(imgTextura.getWidth(), imgTextura.getHeight()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumeroEscenarios() {
        return numeroEscenarios;
    }

    public void setNumeroEscenarios(int numeroEscenarios) {
        this.numeroEscenarios = numeroEscenarios;
    }

    public Escenario getEscenarioActual() {
        return escenarioActual;
    }

    public void setEscenarioActual(Escenario escenarioActual) {
        this.escenarioActual = escenarioActual;
        generarMatriz();
    }

    public int[][] getMatrizActual() {
        return matrizActual;
    }

    public void dibujar(Graphics2D graficos) {

        for (Elemento edificio : escenarioActual.getEdificios()) {
            edificio.dibujarEdificio(graficos);
        }
        for (Elemento hueco : escenarioActual.getHuecos()) {
            hueco.dibujarHueco(graficos);
        }

    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }

    private void generarMatriz() {
        for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 101; j++) {
                matrizActual[i][j] = 0;
            }
        }
        for (Elemento edificio : escenarioActual.getEdificios()) {
            Rectangle r = edificio.getRectangulo();
            for (int i = r.x+1; i < r.x + r.width; i++) {
                for (int j = r.y+1; j < r.y + r.height; j++) {
                    matrizActual[j][i] = 1;
                }
            }
        }
        for (Elemento hueco : escenarioActual.getHuecos()) {
            Rectangle r = hueco.getRectangulo();
            for (int i = r.x+1; i < r.x + r.width; i++) {
                for (int j = r.y+1; j < r.y + r.height; j++) {
                    matrizActual[j][i] = -1;
                }
            }
        }
        for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 101; j++) {
                if (i==0 || i==100 || j==0 || j==100) {
                    matrizActual[j][i] = 1;
                }
            }
        }
    }

}
