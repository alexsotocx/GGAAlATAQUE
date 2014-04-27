package trabajo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Elemento {

    public static final int TAMANOPIXEL = 30;
    private int id;
    private Rectangle rectangulo;
    private String nombre;
    private String tipo;
    private static HashMap<String, BufferedImage> images;

    public Elemento(int x1, int y1, int x2, int y2, String nombre, String tipo, int id) {
        int xini = Math.min(x1, x2);
        int yini = Math.min(y1, y2);
        this.rectangulo = new Rectangle(xini, yini, Math.abs(x2 - x1), Math.abs(y2 - y1));
        this.nombre = nombre;
        this.tipo = tipo;
        this.id = id;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void dibujarEdificio(Graphics2D graficos) {
        Rectangle recDibujar = getRectanguloVisible();

        graficos.setStroke(new BasicStroke(2));
        graficos.setPaint(Ciudad.texturas.get("ladrillo"));
        graficos.fill(recDibujar);
        graficos.setColor(Color.black);
        graficos.draw(recDibujar);
        pintarImagen(graficos, recDibujar);
        graficos.drawString(this.nombre, recDibujar.x + 5, recDibujar.y + recDibujar.height - 5);
    }

    public void dibujarHueco(Graphics2D graficos) {
        Rectangle recDibujar = getRectanguloVisible();
        graficos.setPaint(Ciudad.texturas.get("hueco"));
        graficos.fill(recDibujar);
    }

    /**
     * @return the rectangulo
     */
    public Rectangle getRectangulo() {
        return rectangulo;
    }

    /**
     * @param rectangulo the rectangulo to set
     */
    public void setRectangulo(Rectangle rectangulo) {
        this.rectangulo = rectangulo;
    }

    private Rectangle getRectanguloVisible() {
        Rectangle rec = new Rectangle(rectangulo);
        rec.setBounds(rec.x * TAMANOPIXEL, (rec.y) * TAMANOPIXEL, rec.width * TAMANOPIXEL, rec.height * TAMANOPIXEL);
        return rec;
    }

    public static void rellenarImagenes(int id) {
        try {
            //Esto se va a llenar desde la base de datos pero me dio pereza a esta hora
            images = new HashMap<>();
            images.put("Hospital", ImageIO.read(new File("img/iconohospital.jpg")));
            images.put("Museo", ImageIO.read(new File("img/iconoMuseo.jpg")));
            images.put("Cementerio", ImageIO.read(new File("img/iconoCem.jpg")));
        } catch (IOException ex) {
            Logger.getLogger(Elemento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void pintarImagen(Graphics2D graficos, Rectangle recVisible) {
        try {
            BufferedImage img = images.get(this.tipo);
            int x = img.getWidth();
            graficos.drawImage(img, recVisible.x + (recVisible.width / 2) - x / 2, (recVisible.y + 10), null);
        } catch (NullPointerException e) {

        }

    }
}
