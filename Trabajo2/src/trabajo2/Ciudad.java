package trabajo2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Ciudad {

    private int id;
    private int numeroEscenarios;
    private String nombre;
    private Escenario escenarioActual;
    private int[][] matrizActual;
    public static HashMap<String, TexturePaint> texturas;
    public static final int GROSOR_LINEA = Elemento.TAMANOPIXEL / 6;

    public Ciudad(int id, int numeroEscenarios, String nombre) {
        this.id = id;
        this.numeroEscenarios = numeroEscenarios;
        this.nombre = nombre;
        matrizActual = new int[101][101];
    }

    public static void dibujarSuelo(Graphics2D graficos) {
        Rectangle rectangulo = new Rectangle(0, 0, 100 * Elemento.TAMANOPIXEL, 100 * Elemento.TAMANOPIXEL);
        graficos.setPaint(texturas.get("calle"));
        graficos.fill(rectangulo);
        graficos.setColor(Color.black);
        graficos.setStroke(new BasicStroke(2));
        graficos.draw(rectangulo);

    }

    public static void cargarTexturas() {
        texturas = new HashMap<>();
        texturas.put("calle", crearTextura("img/calle.png"));
        texturas.put("ladrillo", crearTextura("img/ladrillo.png"));
        texturas.put("hueco", crearTextura("img/hueco.png"));
    }

    private static TexturePaint crearTextura(String ruta) {
        BufferedImage imgTextura = null;
        try {
            imgTextura = ImageIO.read(new File(ruta));
        } catch (Exception ex) {
            System.out.println("Error creando la textura");
        }
        BufferedImage imgTexPaint = new BufferedImage(imgTextura.getWidth(), imgTextura.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imgTexPaint.createGraphics();
        g2.drawImage(imgTextura, 0, 0, null);
        g2.dispose();
        return new TexturePaint(imgTexPaint, new Rectangle(imgTextura.getWidth(), imgTextura.getHeight()));
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

    public int getNumeroEscenarios() {
        return numeroEscenarios;
    }

    public void setNumeroEscenarios(int numeroEscenarios) {
        this.numeroEscenarios = numeroEscenarios;
    }

    public Escenario getEscenarioActual() {
        return escenarioActual;
    }

    public void setEscenarioActual(Escenario escenarioActual) {
        this.escenarioActual = escenarioActual;
        generarMatriz();
    }

    public int[][] getMatrizActual() {
        return matrizActual;
    }

    public void dibujar(Graphics2D graficos) {

        for (Elemento edificio : escenarioActual.getEdificios()) {
            edificio.dibujarEdificio(graficos);
        }
        for (Elemento hueco : escenarioActual.getHuecos()) {
            hueco.dibujarHueco(graficos);
        }

    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }

    /**
     * Devulve la ruta más corta de un lugar a otro en la ciudad que llama este
     * metodo
     *
     * @param inicio punto inicial
     * @param fin punto final
     * @return lista de rectangulos a dibujar para mostrar la ruta más corta
     */
    public List<Point> getRutaMasCortaBFS(Point inicio, Point fin) {
        //Donde se van a guardar los próximos nodos(puntos) a recorrer
        Queue<Point> q = new LinkedList<>();
        q.offer(inicio);
        Point recorrido[][] = new Point[101][101]; //Donde se va a guardar el recorrido, se guarda como se llego a este nodo
        int tamanoRecorrido[][] = new int[101][101];
        while (!q.isEmpty()) {
            Point nodo = q.poll();
            if (nodo.equals(fin)) {
                break;
            }
            //Recorrer todos los vecinos y encolarlos si nos podemos mover
            for (int i = 0; i < 4; i++) {
                Point nodoVecino = new Point(nodo);
                nodoVecino.move(nodo.x + MovimientoTaxis.dx[i], nodo.y + MovimientoTaxis.dy[i]);
                if (puedoMoverme(tamanoRecorrido, nodoVecino)) {
                    q.offer(nodoVecino);
                    tamanoRecorrido[nodoVecino.y][nodoVecino.x] = 1 + tamanoRecorrido[nodo.y][nodo.x];
                    recorrido[nodoVecino.y][nodoVecino.x] = nodo;
                }
            }
        }
        if (tamanoRecorrido[fin.y][fin.x] == 0) {
            return null;
        } else {
            return construirRuta(recorrido, inicio, fin);
        }
    }

    private List<Point> construirRuta(Point[][] recorrido, Point inicio, Point fin) {
        List<Point> ruta = new ArrayList<>();
        Point puntoActual = new Point(fin);
        Point puntoAnterior = recorrido[fin.y][fin.x];
        while (!puntoAnterior.equals(inicio)) {
            ruta.add(puntoActual);
            puntoActual = puntoAnterior;
            puntoAnterior = recorrido[puntoAnterior.y][puntoAnterior.x];
        }
        ruta.add(puntoActual);
        ruta.add(inicio);
        return ruta;
    }

    public static List<Rectangle> crearRutaGrafica(List<Point> ruta) {
        List<Rectangle> rutaVisible = new ArrayList<>();
        for (int i = 0; i < ruta.size() - 1; i++) {
            Point puntoActual = ruta.get(i);
            Point puntoAnterior = ruta.get(i + 1);
            int posX = Math.min(puntoActual.x, puntoAnterior.x) * Elemento.TAMANOPIXEL - GROSOR_LINEA / 2;
            int posY = Math.min(puntoActual.y, puntoAnterior.y) * Elemento.TAMANOPIXEL - GROSOR_LINEA / 2;
            int ancho = (puntoActual.x == puntoAnterior.x) ? GROSOR_LINEA : Math.abs(puntoActual.x - puntoAnterior.x) * Elemento.TAMANOPIXEL + GROSOR_LINEA;
            int largo = (puntoActual.y == puntoAnterior.y) ? GROSOR_LINEA : Math.abs(puntoActual.y - puntoAnterior.y) * Elemento.TAMANOPIXEL + GROSOR_LINEA;
            rutaVisible.add(new Rectangle(posX, posY, ancho, largo));
        }
        return rutaVisible;
    }

    private boolean puedoMoverme(int[][] tamanoRecorrido, Point nodoAux) {
        return nodoAux.x >= 0 && nodoAux.x < 101 && nodoAux.y >= 0 && nodoAux.y < 101 && tamanoRecorrido[nodoAux.y][nodoAux.x] == 0 && Math.abs(matrizActual[nodoAux.y][nodoAux.x]) != 1;
    }

//    public List<Rectangle> getRutaMasCortaEdificio(Point inicio, Point medio, Point fin) {
//        //Donde se van a guardar los próximos nodos(puntos) a recorrer
//        List<Rectangle> rutaEdifAPersona;
//        List<Rectangle> rutaTaxiAEdif;
//        Queue<Point> q = new LinkedList<>();
//        q.offer(inicio);
//        Point recorrido[][] = new Point[101][101]; //Donde se va a guardar el recorrido, se guarda como se llego a este nodo
//        int tamanoRecorrido[][] = new int[101][101];
//        while (!q.isEmpty()) {
//            Point nodo = q.poll();
//            if (nodo.equals(medio)) {
//                break;
//            }
//            //Recorrer todos los vecinos y encolarlos si nos podemos mover
//            for (int i = 0; i < 4; i++) {
//                Point nodoVecino = new Point(nodo);
//                nodoVecino.move(nodo.x + MovimientoTaxis.dx[i], nodo.y + MovimientoTaxis.dy[i]);
//                if (puedoMovermeEdificio(tamanoRecorrido, nodoVecino)) {
//                    q.offer(nodoVecino);
//                    tamanoRecorrido[nodoVecino.y][nodoVecino.x] = 1 + tamanoRecorrido[nodo.y][nodo.x];
//                    recorrido[nodoVecino.y][nodoVecino.x] = nodo;
//                }
//            }
//        }
////        rutaEdifAPersona = construirRutaEdificio(recorrido, inicio, medio, true);
//        int aux1 = tamanoRecorrido[fin.y][fin.x];
//        q = new LinkedList<>();
//        q.offer(medio);
//        recorrido = new Point[101][101];
//        tamanoRecorrido = new int[101][101];
//        while (!q.isEmpty()) {
//            Point nodo = q.poll();
//            if (nodo.equals(fin)) {
//                break;
//            }
//            //Recorrer todos los vecinos y encolarlos si nos podemos mover
//            for (int i = 0; i < 4; i++) {
//                Point nodoVecino = new Point(nodo);
//                nodoVecino.move(nodo.x + MovimientoTaxis.dx[i], nodo.y + MovimientoTaxis.dy[i]);
//                if (puedoMovermeEdificio(tamanoRecorrido, nodoVecino)) {
//                    q.offer(nodoVecino);
//                    tamanoRecorrido[nodoVecino.y][nodoVecino.x] = 1 + tamanoRecorrido[nodo.y][nodo.x];
//                    recorrido[nodoVecino.y][nodoVecino.x] = nodo;
//                }
//            }
//        }
//        rutaTaxiAEdif = construirRutaEdificio(recorrido, medio, fin, false);
//        for (int i = 0; i < rutaEdifAPersona.size(); i++) {
//            rutaTaxiAEdif.add(rutaEdifAPersona.get(i));
//        }
//        if (aux1 + tamanoRecorrido[fin.y][fin.x] == 0) {
//            return null;
//        } else {
//            return rutaTaxiAEdif;
//        }
//    }

    private boolean puedoMovermeEdificio(int[][] tamanoRecorrido, Point nodoAux) {
        return nodoAux.x > 0 && nodoAux.x < 100 && nodoAux.y > 0 && nodoAux.y < 100 && tamanoRecorrido[nodoAux.y][nodoAux.x] == 0;
    }

//    private List<Rectangle> construirRutaEdificio(Point[][] recorrido, Point inicio, Point fin, boolean esPrimeraLLamada) {
//        List<Rectangle> rectangulos = new ArrayList<>();
//        List<Point> ruta = new ArrayList<>();
//        Point puntoActual = new Point(fin);
//        Point puntoAnterior = recorrido[fin.y][fin.x];
//        while (!puntoAnterior.equals(inicio)) {
//            ruta.add(puntoActual);
//            rectangulos.add(crearRutaGrafica(puntoActual, puntoAnterior));
//            puntoActual = puntoAnterior;
//            puntoAnterior = recorrido[puntoAnterior.y][puntoAnterior.x];
//        }
//        ruta.add(puntoActual);
//        ruta.add(inicio);
//        rectangulos.add(crearRutaGrafica(puntoActual, inicio));
//        if (esPrimeraLLamada) {
//            graf.setRutaPuntos(ruta);
//        } else {
//            for (int i = 0; i < ruta.size(); i++) {
//                graf.getRutaPuntos().add(0, ruta.get(ruta.size() - i - 1));
//            }
//            int contadorEdificios = 0;
//            int contadorHuecos = 0;
//            List<Point> ruta2 = graf.getRutaPuntos();
//            //para que cuente bien hay que recorrer la lista de edificios y de huecos y usar el metodo contains
//            //CORREGIR
//            for (int i = 1; i < ruta2.size(); i++) {
//                if (matrizActual[ruta2.get(i).y][ruta2.get(i).x] == 1 && matrizActual[ruta2.get(i - 1).y][ruta2.get(i - 1).x] != 1) {
//                    contadorEdificios++;
//                } else if (matrizActual[ruta2.get(i).y][ruta2.get(i).x] == -1 && matrizActual[ruta2.get(i - 1).y][ruta2.get(i - 1).x] != -1 && ruta2.get(i).x != 0 && ruta2.get(i).x != 100 && ruta2.get(i).y != 0 && ruta2.get(i).y != 100) {
//                    contadorHuecos++;
//                }
//            }
//            JOptionPane.showMessageDialog(null, "Se atravesaron " + contadorEdificios + " edificios y " + contadorHuecos + " huecos.");
//        }
//        return rectangulos;
//    }
    private void generarMatriz() {
        for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 101; j++) {
                if (i == 0 || i == 100 || j == 0 || j == 100) {
                    matrizActual[j][i] = -1;
                } else {
                    matrizActual[i][j] = 0;
                }
            }
        }
        for (Elemento edificio : escenarioActual.getEdificios()) {
            Rectangle r = edificio.getRectangulo();
            for (int i = r.x + 1; i < r.x + r.width; i++) {
                for (int j = r.y + 1; j < r.y + r.height; j++) {
                    matrizActual[j][i] = 1;
                }
            }
        }
        for (Elemento hueco : escenarioActual.getHuecos()) {
            Rectangle r = hueco.getRectangulo();
            for (int i = r.x + 1; i < r.x + r.width; i++) {
                for (int j = r.y + 1; j < r.y + r.height; j++) {
                    matrizActual[j][i] = -1;
                }
            }
        }
    }
}
