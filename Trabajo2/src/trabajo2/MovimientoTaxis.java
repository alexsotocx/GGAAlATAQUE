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
    public static long inicioEsperaInicial=0;
    public static long finEsperaInicial=3000;
    public static MovimientoTaxis mover;

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
            System.out.println("Esperainicio= "+esperaInicio);
            if(esperaInicio){
                while ((MovimientoTaxis.finEsperaInicial - MovimientoTaxis.inicioEsperaInicial) < 3000) {
                    MovimientoTaxis.finEsperaInicial = System.currentTimeMillis();
                //System.out.println("Inicio= "+horaInicioCiclo/1000);
                    //System.out.println("Fin= "+horaFinUltimoCiclo/1000);
                   // System.out.println("Diferencia= "+(finEsperaInicial - inicioEsperaInicial)/1000);
                }
                esperaInicio=false;
            }
            //moverTaxis = true;
            long horaFinUltimoCiclo = 3;
            long horaInicioCiclo = 4;

            while (moverTaxis&&!esperaInicio) {
                horaInicioCiclo = System.currentTimeMillis();
                int[][] matriz = ciudad.getMatrizActual();
                int opcionElegida;
                ArrayList<Integer> nroOpciones;//aloja las opciones posibles... op1:Arriba op2:derecha op3:abajo op4:izquierda
                for (Taxi t : taxis) {
                    matriz[t.y][t.x] = 0;
                    nroOpciones = new ArrayList();

                    if (matriz[t.y][t.x - 1] != 1) {
                        nroOpciones.add(1);
                    }
                    if (matriz[t.y + 1][t.x] != 1) {
                        nroOpciones.add(2);
                    }
                    if (matriz[t.y][t.x + 1] != 1) {
                        nroOpciones.add(3);
                    }
                    if (matriz[t.y - 1][t.x] != 1) {
                        nroOpciones.add(4);
                    }
                    //Sxstem.out.println(nroOpciones.size());

                    opcionElegida = (nroOpciones.size() > 0) ? nroOpciones.get((int) (Math.random() * nroOpciones.size())) : 5;
                    //System.out.println(opcionElegida);
                    switch (opcionElegida) {
                        case 1:
                            t.x -= 1;
                            break;
                        case 2:
                            t.y += 1;
                            break;
                        case 3:
                            t.x += 1;
                            break;
                        case 4:
                            t.y -= 1;
                            break;
                    }
                    matriz[t.y][t.x] = 2;

                }
            //System.out.println("Graficar");
            /*for (int i = 0; i < matriz.length; i++) {
                 for (int j = 0; j < matriz[0].length; j++) {
                 System.out.print(matriz[i][j] + " ");
                 }
                 System.out.println("");
                 }*/
                while ((horaInicioCiclo - horaFinUltimoCiclo) < 3000) {
                    horaInicioCiclo = System.currentTimeMillis();
                //System.out.println("Inicio= "+horaInicioCiclo/1000);
                    //System.out.println("Fin= "+horaFinUltimoCiclo/1000);
                    //System.out.println("Diferencia= "+(horaInicioCiclo - horaFinUltimoCiclo)/1000);
                }
                if (moverTaxis) {
                    graficador.graficarTaxis(taxis);
                    horaFinUltimoCiclo = System.currentTimeMillis();
                }

            }
            
            //System.out.println(".");
        }
    }

   
}
