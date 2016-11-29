package org.rul.realmtestproject.model;

import io.realm.RealmObject;

/**
 * Created by rgonzalez on 29/11/2016.
 */

public class Movimiento extends RealmObject{

    public String concepto;
    public double importe;

    public String tipo;
}
