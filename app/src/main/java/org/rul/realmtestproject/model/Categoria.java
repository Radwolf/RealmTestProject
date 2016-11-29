package org.rul.realmtestproject.model;

import io.realm.RealmObject;

/**
 * Created by rgonzalez on 29/11/2016.
 */

public class Categoria extends RealmObject {

    private String nombre;
    private Categoria categoriaPadre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoriaPadre() {
        return categoriaPadre;
    }

    public void setCategoriaPadre(Categoria categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
    }
}
