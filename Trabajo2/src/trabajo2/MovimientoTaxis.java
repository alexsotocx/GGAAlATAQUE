/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 fasdfasfd
 */
package trabajo2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tavo
 */
public class MovimientoTaxis extends Thread {

    private List<Taxi> taxis;
    private Ciudad ciudad;
    private Graficador graficador;
    public static boolean moverTaxis = false;
    public static boolean esperaInicio = false;
    public static MovimientoTaxis mover;
    private final int dx[] = {-1, 0, 1, 0};
    private final int dy[] = {0, 1, 0, -1};

    public MovimientoTaxis(List<Taxi> taxis, Ciudad ciudad, Graficador graficador) {
        this.taxis = taxis;
        this.ciudad = ciudad;
        this.graficador = graficador;
    }

    public MovimientoTaxis() {
    }

    public void setTaxis(List<Taxi> taxis) {
        this.taxis = taxis;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public void setGraficador(Graficador graficador) {
        this.graficador = graficador;
    }

    @Override
    public void run() {
        while (true) {
            if (esperaInicio) {
                try {
                    Thread.sleep(3000);
                    esperaInicio = false;
                } catch (InterruptedException ex) {
                    Logger.getLogger(MovimientoTaxis.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            while (moverTaxis && !esperaInicio) {
                int[][] matriz = ciudad.getMatrizActual();
                int opcionElegida;
                ArrayList<Integer> nroOpciones;//aloja las opciones posibles... op1:Arriba op2:derecha op3:abajo op4:izquierda
                for (Taxi taxi : taxis) {
                    matriz[taxi.y][taxi.x] = 0;
                    nroOpciones = new ArrayList();
                    for (int i = 0; i < 4; i++) {
                        if (matriz[taxi.y + dy[i]][taxi.x + dx[i]] != 1) {
                            nroOpciones.add(i + 1);
                        }
                    }
                    opcionElegida = (nroOpciones.size() > 0) ? nroOpciones.get((int) (Math.random() * nroOpciones.size())) : 5;
                    if (opcionElegida != 5) {
                        taxi.y += dy[opcionElegida - 1];
                        taxi.x += dx[opcionElegida - 1];
                        matriz[taxi.y][taxi.x] = 2;
                    }
                }
                if (moverTaxis) {
                    graficador.graficarTaxis(taxis);
                }
                break;
            }
            try {
                Thread.sleep(2700);
            } catch (InterruptedException ex) {
                Logger.getLogger(MovimientoTaxis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
