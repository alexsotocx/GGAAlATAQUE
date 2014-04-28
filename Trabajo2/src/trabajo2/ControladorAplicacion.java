/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajo2;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public ControladorAplicacion(VistaPrincipal interfaz, SQLJava bdHelper, Graficador graficador) {
        this.bdHelper = bdHelper;
        this.interfaz = interfaz;
        this.graficador = graficador;
        this.taxis = new ArrayList<>();
    }

    public void inicializar() {
        graficador.setPanelGraficador(interfaz.getPanelGrafico());
        ciudades = bdHelper.listarCiudades();
        MovimientoTaxis.mover = new MovimientoTaxis();
        MovimientoTaxis.mover.start();
        iniciarInterfaz();
    }

    private void iniciarInterfaz() {

        interfaz.getBotonGraficar().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Ciudad ciudad = (Ciudad) interfaz.getCiudadSeleccionada();
                try {
                    Elemento.setImagenes(bdHelper.getImagenesByIdCiudad(ciudad.getId()));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(interfaz, "No se encontraron algunas imagenes","Error",JOptionPane.ERROR_MESSAGE);
                }
                int escenario = interfaz.getEscenarioSeleccionado();
                ciudad.setEscenarioActual(bdHelper.getEscenario(ciudad.getId(), escenario));
                graficador.graficarCiudad(ciudad);
                interfaz.setLabelCiudad(ciudad.getNombre());
                interfaz.setLabelEscenario(escenario);
                interfaz.setLabelTaxis(0);
                MovimientoTaxis.moverTaxis = false;
                interfaz.getBotonPararTaxis().setText("Parar");
                MovimientoTaxis.mover.setTaxis(new ArrayList());

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
                        MovimientoTaxis.mover.setCiudad(ciudad);
                        MovimientoTaxis.mover.setTaxis(taxis);
                        MovimientoTaxis.mover.setGraficador(graficador);
                        MovimientoTaxis.mover.esperaInicio = true;
                        interfaz.getBotonPararTaxis().setText("Parar");
                        MovimientoTaxis.moverTaxis = true;

                    } catch (HeadlessException | NumberFormatException e) {
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

}
