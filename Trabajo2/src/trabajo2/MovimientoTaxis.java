/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 fasdfasfd
 */
package trabajo2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Tavo
 */
public class MovimientoTaxis extends Thread {

    public static final int dx[] = {-1, 0, 1, 0};
    public static final int dy[] = {0, 1, 0, -1};
    private List<Taxi> taxis;
    private Ciudad ciudad;
    private Graficador graficador;
    private ControladorAplicacion controladorAplicacion;
    public static boolean moverTaxis = false;
    public static boolean esperaInicio = false;
    private volatile Thread hilo;

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
        Thread hiloActual = Thread.currentThread();
        while (hilo == hiloActual) {
            if (esperaInicio) {
                try {
                    hiloActual.sleep(3000);
                    esperaInicio = false;
                } catch (InterruptedException ex) {
                    break;
                }
            }

            while (moverTaxis && !esperaInicio) {
                int[][] matriz = ciudad.getMatrizActual();
                int opcionElegida;
                ArrayList<Integer> nroOpciones;//aloja las opciones posibles... op1:Arriba op2:derecha op3:abajo op4:izquierda
                for (Taxi taxi : taxis) {
                    if (taxi.isEnCarrera5()) {
                        Point siguiente = taxi.siguientePosicion();
                        taxi.removerPosicion();
                        if (siguiente == null && taxi.getIndex() == 0) {
                            taxi.incrementarIndex();
                            controladorAplicacion.setDestination(taxi);
                            continue;
                        } else if (siguiente == null) {
                            controladorAplicacion.notificarFinCarrera(taxi);
                            continue;
                        }
                        taxi.setLocation(siguiente);
                        continue;
                    }
                    if (taxi.isEnCarrera4()) {
                        Point siguiente = taxi.siguientePosicion();
                        taxi.removerPosicion();
                        if (siguiente == null && taxi.getIndex() == 0) {
                            taxi.incrementarIndex();
                            controladorAplicacion.setDestination4(taxi);
                            continue;
                        } else if (siguiente == null) {
                            controladorAplicacion.notificarFinCarrera(taxi);
                            continue;
                        }
                        taxi.setLocation(siguiente);
                        continue;
                    }
                    nroOpciones = new ArrayList();
                    if ((taxi.x == 0 || taxi.x == 100) && (taxi.y == 0 || taxi.y == 100)) {
                        //no agrega opción
                    } else if (taxi.x == 0) {
                        if (ciudad.atraviesaElemento(taxi, new Point(taxi.x + 1, taxi.y)) == null) {
                            nroOpciones.add(3);
                        }
                    } else if (taxi.x == 100) {
                        if (ciudad.atraviesaElemento(taxi, new Point(taxi.x - 1, taxi.y))== null) {
                            nroOpciones.add(1);
                        }
                    } else if (taxi.y == 0) {
                        if (ciudad.atraviesaElemento(taxi, new Point(taxi.x, taxi.y + 1))== null) {
                            nroOpciones.add(2);
                        }
                    } else if (taxi.y == 100) {
                        if (ciudad.atraviesaElemento(taxi, new Point(taxi.x, taxi.y - 1))== null) {
                            nroOpciones.add(4);
                        }
                    } else {
                        for (int i = 0; i < 4; i++) {
                            if (Math.abs(matriz[taxi.y + dy[i]][taxi.x + dx[i]]) != 1 && matriz[taxi.y + dy[i]][taxi.x + dx[i]] != -9 && ciudad.atraviesaElemento(taxi, new Point(taxi.x + dx[i], taxi.y + dy[i])) == null) {
                                nroOpciones.add(i + 1);
                            }
                        }
                    }
                    opcionElegida = (nroOpciones.size() > 0) ? nroOpciones.get((int) (Math.random() * nroOpciones.size())) : 5;
                    if (opcionElegida != 5) {
                        taxi.y += dy[opcionElegida - 1];
                        taxi.x += dx[opcionElegida - 1];
                    } else {
                        //el taxi no se puede mover
                        JOptionPane.showMessageDialog(null, "El taxi no se puede mover. Ingrese una posición válida");
                        while (true) {
                            taxi.x = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la nueva posición x del taxi"));
                            taxi.y = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la nueva posición y del taxi"));
                            if (taxi.x < 0 || taxi.x > 100 || taxi.y < 0 || taxi.y > 100) {
                                JOptionPane.showMessageDialog(null, "El taxi no puede estar fuera del escenario");
                                continue;
                            }
                            break;
                        }
                    }
                }
                if (moverTaxis) {
                    graficador.graficarTaxis(taxis);
                }
                break;
            }
            try {
                hiloActual.sleep(3000);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    @Override
    public void start() {
        hilo = new Thread(this);
        hilo.start();
    }

    public void stopThread() {
        Thread hiloParar = hilo;
        hilo = null;
        hiloParar.interrupt();
    }

    public boolean isRunning() {
        return hilo != null;
    }

    /**
     * @return the controladorAplicacion
     */
    public ControladorAplicacion getControladorAplicacion() {
        return controladorAplicacion;
    }

    /**
     * @param controladorAplicacion the controladorAplicacion to set
     */
    public void setControladorAplicacion(ControladorAplicacion controladorAplicacion) {
        this.controladorAplicacion = controladorAplicacion;
    }
}
