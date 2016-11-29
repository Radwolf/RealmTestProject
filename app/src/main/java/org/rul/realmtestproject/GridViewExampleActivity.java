package org.rul.realmtestproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.rul.realmtestproject.adapter.CategoriaAdapter;
import org.rul.realmtestproject.model.Categoria;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Rul on 29/11/2016.
 */

public class GridViewExampleActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = GridViewExampleActivity.class.getName();
    private GridView mGridView;
    private CategoriaAdapter mAdapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_example);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        // Clear the realm from last time
        Realm.deleteRealm(realmConfiguration);

        // Create a new empty instance of Realm
        realm = Realm.getInstance(realmConfiguration);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load from file "cities.json" first time
        if(mAdapter == null) {
            List<Categoria> categoriaList = loadCities();

            //This is the GridView adapter
            mAdapter = new CategoriaAdapter(this);
            mAdapter.setData(categoriaList);

            //This is the GridView which will display the list of cities
            mGridView = (GridView) findViewById(R.id.categorias_list);
            mGridView.setAdapter(mAdapter);
            mGridView.setOnItemClickListener(GridViewExampleActivity.this);
            mAdapter.notifyDataSetChanged();
            mGridView.invalidate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    private List<Categoria> loadCities() {
        // In this case we're loading from local assets.
        // NOTE: could alternatively easily load from network
        InputStream stream;
        try {
            stream = getAssets().open("categorias.json");
        } catch (IOException e) {
            return null;
        }

        Gson gson = new GsonBuilder().create();

        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
        Type categoriaType = new TypeToken<List<Categoria>>(){}.getType();
        List<Categoria> categoriaList = gson.fromJson(json, categoriaType);

        // Open a transaction to store items into the realm
        // Use copyToRealm() to convert the objects into proper RealmObjects managed by Realm.
        realm.beginTransaction();
        Collection<Categoria> realmCategoria = realm.copyToRealm(categoriaList);
        realm.commitTransaction();

        return new ArrayList<Categoria>(realmCategoria);
    }

    public void updateCities() {
        // Pull all the cities from the realm
        RealmResults<Categoria> categorias = realm.where(Categoria.class).findAll();

        // Put these items in the Adapter
        mAdapter.setData(categorias);
        mAdapter.notifyDataSetChanged();
        mGridView.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Categoria modifiedCategoria = (Categoria) mAdapter.getItem(position);


        // Acquire the RealmObject matching the name of the clicked City.
        final Categoria categoria = realm.where(Categoria.class).equalTo("nombre", modifiedCategoria.getNombre()).findFirst();

        // Create a transaction to increment the vote count for the selected City in the realm
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, categoria.getCategoriaPadre().getNombre());
            }
        });

        updateCities();
    }

}
