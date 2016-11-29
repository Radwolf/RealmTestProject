package org.rul.realmtestproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rul.realmtestproject.R;
import org.rul.realmtestproject.model.Categoria;

import java.util.List;
import java.util.Locale;

/**
 * Created by rgonzalez on 29/11/2016.
 */

public class CategoriaAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Categoria> categorias = null;

    public CategoriaAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Categoria> details) {
        this.categorias = details;
    }

    @Override
    public int getCount() {
        if(this.categorias == null) {
            return 0;
        }
        return this.categorias.size();
    }

    @Override
    public Object getItem(int i) {
        if(this.categorias == null || i >= this.getCount()) {
            return null;
        }
        return this.categorias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.categoria_listitem, parent, false);
        }
        Categoria categoria = this.categorias.get(position);

        if (categoria != null) {
            ((TextView) currentView.findViewById(R.id.nombre)).setText(categoria.getNombre());
            ((TextView) currentView.findViewById(R.id.categoriaPadre)).setText(String.format(Locale.US, "%d",categoria.getCategoriaPadre()));
        }
        return currentView;
    }
}
