/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajo2;

import java.util.List;

/**
 *
 * @author usuario
 */
public class Escenario {

    private int id;
    private List<Elemento> edificios;
    private List<Elemento> huecos;

    public Escenario(int id, List<Elemento> edificios, List<Elemento> huecos) {
        this.id = id;
        this.edificios = edificios;
        this.huecos = huecos;
    }

    public List<Elemento> getEdificios() {
        return edificios;
    }

    public void setEdificios(List<Elemento> edificios) {
        this.edificios = edificios;
    }

    public List<Elemento> getHuecos() {
        return huecos;
    }

    public void setHuecos(List<Elemento> huecos) {
        this.huecos = huecos;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

}
