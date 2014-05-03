package trabajo2;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vistas.VistaPrincipal;

/**
 *
 * @author usuario
 */
public class ControladorAplicacion {

    private VistaPrincipal interfaz;
    private SQLJava bdHelper;
    private Graficador graficador;
    private HashMap<Integer, Ciudad> ciudades;
    private List<Taxi> taxis;
    private MovimientoTaxis movimientoTaxis;

    public ControladorAplicacion(VistaPrincipal interfaz, SQLJava bdHelper, Graficador graficador) {
        this.bdHelper = bdHelper;
        this.interfaz = interfaz;
        this.graficador = graficador;
        this.taxis = new ArrayList<>();
        this.movimientoTaxis = new MovimientoTaxis();
    }

    public void inicializar() {
        graficador.setPanelGraficador(interfaz.getPanelGrafico());
        ciudades = bdHelper.listarCiudades();
        movimientoTaxis.setControladorAplicacion(this);
        iniciarInterfaz();
    }

    private void iniciarInterfaz() {

        interfaz.getBotonGraficar().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Ciudad ciudad = (Ciudad) interfaz.getCiudadSeleccionada();
                Elemento.setImagenes(bdHelper.getImagenesByIdCiudad(ciudad.getId()));
                int escenario = interfaz.getEscenarioSeleccionado();
                ciudad.setEscenarioActual(bdHelper.getEscenario(ciudad.getId(), escenario));
                graficador.graficarCiudad(ciudad);
                interfaz.setLabelCiudad(ciudad.getNombre());
                interfaz.setLabelEscenario(escenario);
                interfaz.setLabelTaxis(0);
                MovimientoTaxis.moverTaxis = false;
                interfaz.getBotonPararTaxis().setText("Parar");
                movimientoTaxis.setTaxis(new ArrayList());
                if (movimientoTaxis.isRunning()) {
                    movimientoTaxis.stopThread();
                }
            }
        });

        interfaz.getBotonPararTaxis().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (interfaz.getBotonPararTaxis().getText().equals("Parar")) {
                    MovimientoTaxis.moverTaxis = false;
                    interfaz.getBotonPararTaxis().setText("Iniciar");
                } else {
                    interfaz.getBotonPararTaxis().setText("Parar");
                    MovimientoTaxis.moverTaxis = true;
                }
            }
        });

        interfaz.getBotonGraficarTaxis().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Ciudad ciudad = graficador.getCiudad();
                if (ciudad != null) {

                    if (movimientoTaxis.isRunning()) {
                        movimientoTaxis.stopThread();
                    }
                    int cantTaxis = 0;
                    int[][] matriz = ciudad.getMatrizActual();
                    int cantMax = 0;
                    for (int i = 0; i < 101; i++) {
                        for (int j = 0; j < 101; j++) {
                            if (matriz[j][i] == 2) {
                                matriz[j][i] = 0;
                            }
                            if (matriz[j][i] == 0) {
                                cantMax++;
                            }
                        }
                    }
                    try {
                        cantTaxis = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad de taxis"));
                        if (cantTaxis < 1) {
                            cantTaxis = 1;
                        } else if (cantTaxis > cantMax) {
                            cantTaxis = cantMax;
                        }

                        taxis = Taxi.generarTaxisByCiudad(ciudad, cantTaxis);
                        graficador.graficarTaxis(taxis);
                        interfaz.setLabelTaxis(cantTaxis);
                        MovimientoTaxis.esperaInicio = true;
                        movimientoTaxis.setCiudad(ciudad);
                        movimientoTaxis.setTaxis(taxis);
                        movimientoTaxis.setGraficador(graficador);
                        interfaz.getBotonPararTaxis().setText("Parar");
                        MovimientoTaxis.moverTaxis = true;
                        movimientoTaxis.start();

                    } catch (HeadlessException | NumberFormatException e) {
                        if (!movimientoTaxis.isRunning()) {
                            movimientoTaxis.start();
                        }
                        JOptionPane.showMessageDialog(interfaz, "Error, texto ingresado no es un numero", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(interfaz, "Error, la ciudad todavía no ha sido graficada", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        interfaz.getComboxCiudad().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Ciudad ciudad = (Ciudad) interfaz.getCiudadSeleccionada();
                if (ciudad != null) {
                    interfaz.fillComboxEscenarios(ciudad.getNumeroEscenarios());
                }
            }
        });

        interfaz.getBotonRutaCorta().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (interfaz.getBotonPararTaxis().getText().equals("Parar") || graficador.getTaxis().isEmpty()) {
                    JOptionPane.showMessageDialog(interfaz, "No hay taxis o no están parados");
                    return;
                }
                try {
                    int x1 = Integer.parseInt(JOptionPane.showInputDialog("X inicial"));
                    int y1 = Integer.parseInt(JOptionPane.showInputDialog("Y inicial"));
                    if ((x1 == 0 || x1 == 100) && (y1 == 0 || y1 == 100)) {
                        JOptionPane.showMessageDialog(interfaz, "El taxi no puede recorrer el borde del escenario");
                        return;
                    }
                    if (graficador.getCiudad().getMatrizActual()[y1][x1]==-1) {
                        JOptionPane.showMessageDialog(interfaz, "El usuario no puede estar en un hueco");
                        return;
                    }
                    Taxi taxiMasCercano = getTaxiMasCercano(x1, y1);
                    Ciudad ciudad = graficador.getCiudad();
                    List<Point> rutaCorta = ciudad.getRutaMasCortaBFS(new Point(x1, y1), taxiMasCercano);
                    if (!rutaCorta.isEmpty()) {
                        taxiMasCercano.setEnCarrera5(true);
                        taxiMasCercano.setRuta(rutaCorta);
                        graficador.dibujarRutaMasCercana();
                        interfaz.getBotonPararTaxis().setText("Parar");
                        MovimientoTaxis.moverTaxis = true;
                    } else {
                        JOptionPane.showMessageDialog(interfaz, "Error, no existe ruta posible");
                    }
                } catch (HeadlessException | NumberFormatException ex) {
                    if (!movimientoTaxis.isRunning()) {
                        movimientoTaxis.start();
                    }
                    JOptionPane.showMessageDialog(interfaz, "Error, texto ingresado no es un numero", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        interfaz.getBotonAtravesarEdificio().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (interfaz.getBotonPararTaxis().getText().equals("Parar") || graficador.getTaxis().isEmpty()) {
                    JOptionPane.showMessageDialog(interfaz, "No hay taxis o no están parados");
                    return;
                }
                try {
                    int x1 = Integer.parseInt(JOptionPane.showInputDialog("X inicial"));
                    int y1 = Integer.parseInt(JOptionPane.showInputDialog("Y inicial"));
                    /*if ((x1 == 0 || x1 == 100) && (y1 == 0 || y1 == 100)) {
                        JOptionPane.showMessageDialog(interfaz, "El taxi no puede recorrer el borde del escenario");
                        return;
                    }*/
                    if (graficador.getCiudad().getMatrizActual()[y1][x1]==-1) {
                        JOptionPane.showMessageDialog(interfaz, "El usuario no puede estar en un hueco");
                        return;
                    }
                    Taxi taxiMasCercano = getTaxiMasCercano(x1, y1);
                    Point edificioMasCercano = getEdificioMasCercano(taxiMasCercano.x, taxiMasCercano.y);
                    Ciudad ciudad = graficador.getCiudad();
                    List<Point> rutaCorta = ciudad.getRutaMasCortaEdificio(new Point(x1, y1), edificioMasCercano, taxiMasCercano);
                    if (!rutaCorta.isEmpty()) {
                        taxiMasCercano.setEnCarrera4(true);
                        taxiMasCercano.setRuta(rutaCorta);
                        graficador.dibujarRutaMasCercana();
                        interfaz.getBotonPararTaxis().setText("Parar");
                        MovimientoTaxis.moverTaxis = true;
                    } else {
                        JOptionPane.showMessageDialog(interfaz, "Error, no existe ruta posible");
                    }
                } catch (HeadlessException | NumberFormatException ex) {
                    if (!movimientoTaxis.isRunning()) {
                        movimientoTaxis.start();
                    }
                    JOptionPane.showMessageDialog(interfaz, "Error, texto ingresado no es un numero", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        interfaz.fillCiudadCombox(ciudades);
        interfaz.setVisible(true);
    }

    public VistaPrincipal getVistaPrincipal() {
        return interfaz;
    }

    public void setVistaPrincipal(VistaPrincipal vistaPrincipal) {
        this.interfaz = vistaPrincipal;
    }

    public SQLJava getBdHelper() {
        return bdHelper;
    }

    public void setBdHelper(SQLJava bdHelper) {
        this.bdHelper = bdHelper;
    }

    public Graficador getGraficador() {
        return graficador;
    }

    public void setGraficador(Graficador graficador) {
        this.graficador = graficador;
    }

    private Taxi getTaxiMasCercano(int x1, int y1) {
        taxis = graficador.getTaxis();
        Taxi taxiCercano = null;
        double distMinima = Double.MAX_VALUE;
        double distActual = 0.0;
        for (Taxi taxi : taxis) {
            if (taxi.isEnCarrera()) {
                continue;
            }
            distActual = Math.sqrt(Math.abs(x1 - taxi.x) * Math.abs(x1 - taxi.x) + Math.abs(y1 - taxi.y) * Math.abs(y1 - taxi.y));
            if (distActual < distMinima) {
                distMinima = distActual;
                taxiCercano = taxi;
            }
        }
        return taxiCercano;
    }

    public Point getEdificioMasCercano(int x1, int y1) {
        int[][] matriz = graficador.getCiudad().getMatrizActual();
        List<Point> unos = new ArrayList();
        for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 101; j++) {
                if (matriz[j][i] == 1) {
                    unos.add(new Point(i, j));
                }
            }
        }
        Point p = new Point();
        double distMinima = 140.007142675f;
        double distActual;
        for (Point uno : unos) {
            distActual = Math.sqrt(Math.abs(x1 - uno.x) * Math.abs(x1 - uno.x) + Math.abs(y1 - uno.y) * Math.abs(y1 - uno.y));
            if (distActual < distMinima) {
                distMinima = distActual;
                p = uno;
            }
        }
        return p;
    }


    public void setDestination(Taxi taxi) {
        try {
            int x2 = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el destino x2"));
            int y2 = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el destino y2"));
            if (graficador.getCiudad().getMatrizActual()[y2][x2]==-1) {
                JOptionPane.showMessageDialog(interfaz, "El usuario no puede estar en un hueco");
                return;
            }
            Ciudad ciudad = graficador.getCiudad();
            taxi.setRuta(ciudad.getRutaMasCortaBFS(new Point(x2, y2), taxi));
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(interfaz, "Error, texto ingresado no es un numero", "Error", JOptionPane.ERROR_MESSAGE);
            this.setDestination(taxi);
        }
    }
    public void setDestination4(Taxi taxi) {
        try {
            int x2 = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el destino x2"));
            int y2 = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el destino y2"));
            if (graficador.getCiudad().getMatrizActual()[y2][x2]==-1) {
                JOptionPane.showMessageDialog(interfaz, "El usuario no puede estar en un hueco");
                return;
            }
            Ciudad ciudad = graficador.getCiudad();
            taxi.setRuta(ciudad.getRutaMasCortaEdificio(new Point(x2, y2), getEdificioMasCercano(taxi.x, taxi.y), taxi));
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(interfaz, "Error, texto ingresado no es un numero", "Error", JOptionPane.ERROR_MESSAGE);
            this.setDestination4(taxi);
        }
    }

    public void notificarFinCarrera(Taxi taxi) {
        taxi.setEnCarrera5(false);
        taxi.setEnCarrera4(false);
        taxi.setIndex(0);
        JOptionPane.showMessageDialog(interfaz, "Se finalizo la carrera\nValor de la carrera: $" + taxi.getValorRuta());
    }

}
