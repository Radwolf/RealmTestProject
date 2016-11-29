package org.rul.realmtestproject.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by rgonzalez on 29/11/2016.
 */

public class Cuenta extends RealmObject {

    private long id;
    private String nombre;

    private Usuario usuario;

    private RealmList<Movimiento> movimientos;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public RealmList<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(RealmList<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
}
