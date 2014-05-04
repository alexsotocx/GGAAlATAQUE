package trabajo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import static trabajo2.Elemento.TAMANOPIXEL;

public class Taxi extends Point {

    private List<Point> ruta;
    private int index = 0;
    private boolean enCarrera4 = false;
    private boolean enCarrera5 = false;
    private int valorCarrera = 0;
    private boolean[] elementosAtravesados;

    public static List<Taxi> generarTaxisByCiudad(Ciudad c, int cantidad) {
        ArrayList<Taxi> taxis = new ArrayList<>();
        int[][] matriz = c.getMatrizActual();
        int x;
        int y;
        while (taxis.size() < cantidad) {
            x = (int) (Math.random() * 99) + 1;
            y = (int) (Math.random() * 99) + 1;
            if (matriz[y][x] == 0) {
                taxis.add(new Taxi(x, y));
                matriz[y][x] = 2;
            }
        }
        return taxis;
    }

    public static List<Taxi> getTaxisEnCarrera5(List<Taxi> taxis) {
        List<Taxi> taxisEnCarrera = new ArrayList<>();
        for (Taxi taxi : taxis) {
            if (taxi.isEnCarrera5()) {
                taxisEnCarrera.add(taxi);
            }
        }
        return taxisEnCarrera;
    }

    public static List<Taxi> getTaxisEnCarrera4(List<Taxi> taxis) {
        List<Taxi> taxisEnCarrera = new ArrayList<>();
        for (Taxi taxi : taxis) {
            if (taxi.isEnCarrera4()) {
                taxisEnCarrera.add(taxi);
            }
        }
        return taxisEnCarrera;
    }

    private Taxi(int x, int y) {
        super(x, y);
        ruta = new ArrayList<>();
    }

    public void dibujarTaxi(Graphics2D graficos) {
        Rectangle2D recDibujar = getRectanguloVisible();
        graficos.setStroke(new BasicStroke(1));
        if (!enCarrera5 && !enCarrera4) {
            graficos.setColor(Color.yellow);
        } else if (enCarrera5) {
            graficos.setColor(Color.red);
        } else {
            graficos.setColor(Color.cyan);
        }

        graficos.fill(recDibujar);
    }

    private Rectangle2D.Double getRectanguloVisible() {
        Rectangle2D.Double rec = new Rectangle2D.Double((this.x - 0.125) * TAMANOPIXEL, (this.y - 0.125) * TAMANOPIXEL, TAMANOPIXEL * 0.25, TAMANOPIXEL * 0.25);
        return rec;
    }

    /**
     * @return the ruta
     */
    public List<Point> getRuta() {
        return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(List<Point> ruta) {
        this.ruta = ruta;
        valorCarrera = ruta.size();
    }

    /**
     * @return the enCarrera
     */
    public boolean isEnCarrera() {
        return enCarrera5 || enCarrera4;
    }

    public boolean isEnCarrera4() {
        return enCarrera4;
    }

    public boolean isEnCarrera5() {
        return enCarrera5;
    }

    /**
     * @param enCarrera the enCarrera to set
     */
    public void setEnCarrera5(boolean enCarrera) {
        this.enCarrera5 = enCarrera;
    }

    public void setEnCarrera4(boolean enCarrera) {
        this.enCarrera4 = enCarrera;
    }

    public Point siguientePosicion() {
        if (ruta.size() >= 2) {
            return ruta.get(1);
        } else {
            return null;
        }
    }

    public void removerPosicion() {
        if (!ruta.isEmpty()) {
            ruta.remove(0);
        }
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void incrementarIndex() {
        index++;
    }

    public int getValorRuta() {
        return (valorCarrera * 100 + 2400);
    }

    public void getElementosAtravesados(List<Map.Entry<Elemento, HashSet<Point>>> elementos) {
        int huecos = 0, edificios = 0;
        for (int i = 0; i < elementosAtravesados.length; i++) {
            if (elementosAtravesados[i]) {
                Map.Entry entry = elementos.get(i);
                Elemento elemento = (Elemento) entry.getKey();
                if (elemento.getTipo().equals("hueco")) {
                    huecos++;
                } else {
                    edificios++;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Se atravesaron " + edificios + " edificios y " + huecos + " huecos en la ruta");
    }

    /**
     * @param elementosAtravesados the elementosAtravesados to set
     */
    public void setElementosAtravesados(boolean[] elementosAtravesados) {
        this.elementosAtravesados = elementosAtravesados;
    }

    public void calcularElementosAtravesados(List<Map.Entry<Elemento, HashSet<Point>>> elementos) {
        for (Point puntoRuta : ruta) {
            int i = 0;
            for (Map.Entry<Elemento, HashSet<Point>> pareja : elementos) {
                HashSet<Point> setPuntos = pareja.getValue();
                if (!elementosAtravesados[i] && setPuntos.contains(puntoRuta)) {
                    elementosAtravesados[i] = true;
                }
                i++;
            }
        }
    }
}
