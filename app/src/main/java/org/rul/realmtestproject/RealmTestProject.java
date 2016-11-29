package org.rul.realmtestproject;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by rgonzalez on 29/11/2016.
 */

public class RealmTestProject extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);
    }

}
