package trabajo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Elemento {

    public static final int TAMANOPIXEL = 30;
    private int id;
    private Rectangle rectangulo;
    private String nombre;
    private String tipo;
    private static HashMap<String, BufferedImage> imagenes;
    
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
        FontMetrics fontMetrics = graficos.getFontMetrics();
        Rectangle2D stringRectangle;
        int x = fontMetrics.stringWidth(nombre);
        x = x / nombre.length();
        int w = recDibujar.width / x;
        String subnombre = nombre.substring(0, Math.min(nombre.length(), w - 1));
        stringRectangle = fontMetrics.getStringBounds(subnombre, graficos);
        graficos.drawString(subnombre, recDibujar.x + (recDibujar.width / 2) - ((int) stringRectangle.getWidth() / 2), recDibujar.y + recDibujar.height - 5);

    }

    public void dibujarHueco(Graphics2D graficos) {
        Rectangle recDibujar = getRectanguloVisible();
        graficos.setPaint(Ciudad.texturas.get("hueco"));
        graficos.fill(recDibujar);
    }

    public Rectangle getRectangulo() {
        return rectangulo;
    }

    public boolean isclicked(Point2D punto) {
        return getRectanguloVisible().contains(punto);
    }

    public void setRectangulo(Rectangle rectangulo) {
        this.rectangulo = rectangulo;
    }

    public static void setImagenes(HashMap<String, BufferedImage> imagenes) {
        Elemento.imagenes = imagenes;
    }

    private Rectangle getRectanguloVisible() {
        Rectangle rec = new Rectangle(rectangulo);
        rec.setBounds(rec.x * TAMANOPIXEL, (rec.y) * TAMANOPIXEL, rec.width * TAMANOPIXEL, rec.height * TAMANOPIXEL);
        return rec;
    }

    private void pintarImagen(Graphics2D graficos, Rectangle recVisible) {
        try {
            BufferedImage img = imagenes.get(this.tipo);
            int x = img.getWidth();
            graficos.drawImage(img, recVisible.x + (recVisible.width / 2) - x / 2, (recVisible.y + 10), null);
        } catch (NullPointerException e) {

        }

    }

}
