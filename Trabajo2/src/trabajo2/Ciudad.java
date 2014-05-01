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
    public List<Rectangle> getRutaMasCortaBFS(Point inicio, Point fin) {
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
                if (puedoMoverme(tamanoRecorrido, nodoVecino) || clienteEnEdif(inicio, nodoVecino)) {
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

    private List<Rectangle> construirRuta(Point[][] recorrido, Point inicio, Point fin) {
        List<Rectangle> rectangulos = new ArrayList<>();
        Point puntoActual = new Point(fin);
        Point puntoAnterior = recorrido[fin.y][fin.x];
        int auxEd=0;
        int auxHu=0;
        while (!puntoAnterior.equals(inicio)) {
            rectangulos.add(crearLinea(puntoActual, puntoAnterior));
            if (matrizActual[puntoAnterior.y][puntoAnterior.x]==1 && matrizActual[puntoActual.y][puntoActual.x]!=1) {
                ++auxEd;
            }
            if (matrizActual[puntoAnterior.y][puntoAnterior.x]==-1 && matrizActual[puntoActual.y][puntoActual.x]!=-1) {
                ++auxHu;
            }
            puntoActual = puntoAnterior;
            puntoAnterior = recorrido[puntoAnterior.y][puntoAnterior.x];
        }
        JOptionPane.showMessageDialog(null, "La ruta atraviesa "+auxEd+" edificios y "+auxHu+" huecos.");
        rectangulos.add(crearLinea(puntoActual, inicio));
        return rectangulos;
    }

    private Rectangle crearLinea(Point puntoActual, Point puntoAnterior) {
        int posX = Math.min(puntoActual.x, puntoAnterior.x) * Elemento.TAMANOPIXEL - GROSOR_LINEA/2;
        int posY = Math.min(puntoActual.y, puntoAnterior.y) * Elemento.TAMANOPIXEL - GROSOR_LINEA/2;
        int ancho = (puntoActual.x == puntoAnterior.x) ? GROSOR_LINEA : Math.abs(puntoActual.x - puntoAnterior.x) * Elemento.TAMANOPIXEL + GROSOR_LINEA;
        int largo = (puntoActual.y == puntoAnterior.y) ? GROSOR_LINEA : Math.abs(puntoActual.y - puntoAnterior.y) * Elemento.TAMANOPIXEL + GROSOR_LINEA;
        return (new Rectangle(posX, posY, ancho, largo));
    }

    private boolean puedoMoverme(int[][] tamanoRecorrido, Point nodoAux) {
        return nodoAux.x >= 0 && nodoAux.x < 101 && nodoAux.y >= 0 && nodoAux.y < 101 && tamanoRecorrido[nodoAux.y][nodoAux.x] == 0 && Math.abs(matrizActual[nodoAux.y][nodoAux.x]) != 1;
    }
    
    !!private boolean clienteEnEdif(Point cliente, Point nodo){
        List<Elemento> edificios=escenarioActual.getEdificios();
        for (Elemento edif : edificios) {
            if (edif.getRectangulo().contains(nodo) && edif.getRectangulo().contains(cliente)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Rectangle> getRutaMasCortaEdificio(Point inicio, Point fin) {
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
                if (puedoMovermeEdificio(tamanoRecorrido, nodoVecino)) {
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
    
    private boolean puedoMovermeEdificio(int[][] tamanoRecorrido, Point nodoAux) {
        return nodoAux.x > 0 && nodoAux.x < 100 && nodoAux.y > 0 && nodoAux.y < 100 && tamanoRecorrido[nodoAux.y][nodoAux.x] == 0;
    }

    private void generarMatriz() {
        for (int i = 0; i < 101; i++) {
            for (int j = 0; j < 101; j++) {
                if (i == 0 || i == 100 || j == 0 || j == 100) {
                    matrizActual[j][i] = -1;
                }
                else {
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
