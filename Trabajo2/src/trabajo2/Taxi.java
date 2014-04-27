package trabajo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import static trabajo2.Elemento.TAMANOPIXEL;

public class Taxi extends java.awt.Point {

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

    private Taxi(int x, int y) {
        super(x, y);
    }

    public void dibujarTaxi(Graphics2D graficos) {
        Rectangle2D recDibujar = getRectanguloVisible();
        graficos.setStroke(new BasicStroke(1));
        graficos.setColor(Color.yellow);
        graficos.fill(recDibujar);
    }

    private Rectangle2D.Double getRectanguloVisible() {
        Rectangle2D.Double rec = new Rectangle2D.Double((this.x-0.125)*TAMANOPIXEL, (this.y-0.125)*TAMANOPIXEL, TAMANOPIXEL*0.25, TAMANOPIXEL*0.25);
        return rec;
    }

}
