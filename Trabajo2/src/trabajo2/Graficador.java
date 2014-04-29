/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajo2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Se tomaron varios fragmentos de codigo para el funcionamiento efectos de
 * Zoom, y dragging de imagenes. Se hace referencia al autor: Bradley T. Vander
 * Zanden Más información: http://goo.gl/DOKxmX
 */
public class Graficador {

    private Lienzo lienzo;
    private ManejadorEventos manejadorEventos;
    private AffineTransform at;
    private Point2D XFormedPoint;
    private JSlider barraZoom;
    private JPanel panelGraficador;
    private Ciudad ciudad;
    private List<Taxi> taxis;

    public Graficador() {
        this.taxis = new ArrayList<>();
        lienzo = new Lienzo();
        manejadorEventos = new ManejadorEventos();
        lienzo.addMouseListener(manejadorEventos);
        lienzo.addMouseMotionListener(manejadorEventos);
        lienzo.addMouseWheelListener(manejadorEventos);
        lienzo.setBorder(BorderFactory.createLineBorder(Color.black));
        barraZoom = new JSlider(JSlider.VERTICAL, 20, 400, 100);
        barraZoom.setMajorTickSpacing(20);
        barraZoom.setMinorTickSpacing(10);
        //barraZoom.setPaintTicks(true);
        barraZoom.setPaintLabels(true);
        barraZoom.addChangeListener(new ManejadorZoom());
        barraZoom.setBackground(Color.WHITE);
    }

    public void graficarCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
        this.taxis = new ArrayList<>();
        lienzo.translateX = 0;
        lienzo.translateY = 0;
        barraZoom.setValue(100);
        lienzo.scale = 1;
        panelGraficador.add(barraZoom, BorderLayout.WEST);
        panelGraficador.add(lienzo, BorderLayout.CENTER);

        panelGraficador.revalidate();
        lienzo.repaint();
        lienzo.requestFocus();
    }

    public void graficarTaxis(List<Taxi> taxis) {
        this.taxis = taxis;
        panelGraficador.add(barraZoom, BorderLayout.WEST);
        panelGraficador.add(lienzo, BorderLayout.CENTER);
        panelGraficador.revalidate();
        lienzo.repaint();
    }

    public JPanel getPanelGraficador() {
        return panelGraficador;
    }

    public void setPanelGraficador(JPanel panelGraficador) {
        this.panelGraficador = panelGraficador;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    class Lienzo extends JComponent {

        private static final long serialVersionUID = 1L;
        double translateX;
        double translateY;
        double scale;

        Lienzo() {
            translateX = 0;
            translateY = 0;
            scale = 1;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D graficos = (Graphics2D) g;
            AffineTransform saveTransform = graficos.getTransform();
            at = new AffineTransform(saveTransform);
            at.translate(getWidth() / 2, getHeight() / 2);
            at.scale(scale, scale);
            at.translate(-getWidth() / 2, -getHeight() / 2);
            at.translate(translateX, translateY);
            graficos.setTransform(at);
            graficos.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Ciudad.dibujarSuelo(graficos);
            getCiudad().dibujar(graficos);
            for (Taxi taxi : taxis) {
                taxi.dibujarTaxi(graficos);
            }
            graficos.setTransform(saveTransform);
        }

    }

    class ManejadorEventos implements MouseListener, MouseMotionListener, MouseWheelListener {

        private final int CANTIDAD_MOVER = 10;
        double referenceX;
        double referenceY;
        AffineTransform initialTransform;

        @Override
        public void mousePressed(MouseEvent e) {
            try {
                XFormedPoint = at.inverseTransform(e.getPoint(), null);
            } catch (NoninvertibleTransformException te) {
                System.out.println(te);
            }

            referenceX = XFormedPoint.getX();
            referenceY = XFormedPoint.getY();
            initialTransform = at;
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            try {
                XFormedPoint = initialTransform.inverseTransform(e.getPoint(),
                        null);
            } catch (NoninvertibleTransformException te) {
                System.out.println(te);
            }
            double deltaX = XFormedPoint.getX() - referenceX;
            double deltaY = XFormedPoint.getY() - referenceY;

            referenceX = (int) XFormedPoint.getX();
            referenceY = (int) XFormedPoint.getY();

            lienzo.translateX += deltaX;
            lienzo.translateY += deltaY;
            lienzo.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                XFormedPoint = initialTransform.inverseTransform(e.getPoint(), null);
            } catch (NoninvertibleTransformException ex) {

            }
            for (Elemento edificio : ciudad.getEscenarioActual().getEdificios()) {
                if (edificio.isclicked(XFormedPoint)) {
                    StringBuilder informacion = new StringBuilder();
                    informacion.append("Nombre: ").append(edificio.getNombre()).append('\n');
                    informacion.append("Tipo: ").append(edificio.getTipo()).append('\n');
                    informacion.append("Rectangulo\n\tPunto Inicial: ").append('(').append(edificio.getRectangulo().x).append(',').append(edificio.getRectangulo().y);
                    informacion.append(")\n\tAncho: ").append(edificio.getRectangulo().width).append('\n');
                    informacion.append("\tLargo: ").append(edificio.getRectangulo().height).append('\n');
                    JOptionPane.showMessageDialog(null, informacion.toString(), "Información", JOptionPane.INFORMATION_MESSAGE);
                    lienzo.requestFocus();
                }
            }

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int movimiento = e.getWheelRotation();
            barraZoom.setValue(barraZoom.getValue() - CANTIDAD_MOVER * movimiento);

        }

    }

    class ManejadorZoom implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();
            int zoomPercent = slider.getValue();
            lienzo.scale = Math.max(0.00001, zoomPercent / 100.0);
            lienzo.repaint();
        }
    }

}
