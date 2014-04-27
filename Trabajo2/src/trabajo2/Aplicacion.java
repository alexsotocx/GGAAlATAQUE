package trabajo2;

import vistas.VistaPrincipal;

public class Aplicacion {

    public static void main(String args[]) {
        VistaPrincipal vistaPrincipal = new VistaPrincipal();
        Graficador graficador = new Graficador();
        SQLJava dbHelper = new SQLJava();
        ControladorAplicacion controladorAplicacion = new ControladorAplicacion(vistaPrincipal, dbHelper, graficador);
        controladorAplicacion.inicializar();
        Ciudad.cargarTexturas();
    }
}
