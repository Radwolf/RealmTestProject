package org.rul.realmtestproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rul.realmtestproject.model.Cuenta;
import org.rul.realmtestproject.model.Movimiento;
import org.rul.realmtestproject.model.Usuario;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private LinearLayout rootLayout = null;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = (LinearLayout) findViewById(R.id.container);
        rootLayout.removeAllViews();

        realm = Realm.getDefaultInstance();

        basicCRUD(realm);
        basicQuery(realm);
        basicLinkQuery(realm);

        // More complex operations can be executed on another thread.
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String info;
                info = complexReadWrite();
                info += complexQuery();
                return info;
            }

            @Override
            protected void onPostExecute(String result) {
                showStatus(result);
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    private void showStatus(String txt) {
        Log.i(TAG, txt);
        TextView tv = new TextView(this);
        tv.setText(txt);
        rootLayout.addView(tv);
    }

    private void basicCRUD(Realm realm) {
        showStatus("Perform basic Create/Read/Update/Delete (CRUD) operations...");

        // All writes must be wrapped in a transaction to facilitate safe multi threading
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a cuenta
                Cuenta cuenta = realm.createObject(Cuenta.class);
                cuenta.setId(1);
                cuenta.setNombre("Casa");
            }
        });

        // Find the first cuenta (no query conditions) and read a field
        final Cuenta cuenta = realm.where(Cuenta.class).findFirst();
        showStatus(String.format("Cuenta: %s", cuenta.getNombre()));

        // Update cuenta in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                cuenta.setNombre("Casa Ahorro");
                showStatus(String.format("Cuenta modificada: %s", cuenta.getNombre()));
            }
        });

        // Delete all persons
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Cuenta.class);
            }
        });

        showStatus("Número de cuentas al finalizar el CRUD: " + realm.where(Cuenta.class).count());
    }

    private void basicQuery(Realm realm) {
        showStatus("\nPreparando una query básica...");
        showStatus("Número de cuentas: " + realm.where(Cuenta.class).count());

        RealmResults<Cuenta> results = realm.where(Cuenta.class).equalTo("nombre", "Casa Ahorro").findAll();

        showStatus(String.format("Cuentas con nombre 'Casa Ahorro': %d", results.size()));
    }

    private void basicLinkQuery(Realm realm) {
        showStatus("\nPreparando una query con relaciones...");
        showStatus("Número de cuentas: " + realm.where(Cuenta.class).count());

        RealmResults<Cuenta> results = realm.where(Cuenta.class).equalTo("movimientos.tipo", "GASTO").findAll();

        showStatus(String.format("Cuentas con movimientos de tipo 'GASTO': %d", results.size()));
    }

    private String complexReadWrite() {
        String status = "\nPerforming complex Read/Write operation...";

        // Open the default realm. All threads must use its own reference to the realm.
        // Those can not be transferred across threads.
        Realm realm = Realm.getDefaultInstance();

        // Add ten persons in one transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Usuario usuario = realm.createObject(Usuario.class);
                usuario.nombre = "Rul";
                for (int i = 0; i < 3; i++) {
                    Cuenta cuenta = realm.createObject(Cuenta.class);
                    cuenta.setId(i);
                    cuenta.setNombre("Cuenta no. " + i);
                    cuenta.setUsuario(usuario);

                    // The field tempReference is annotated with @Ignore.
                    // This means setTempReference sets the Person tempReference
                    // field directly. The tempReference is NOT saved as part of
                    // the RealmObject:

                    // person.setTempReference(42);

                    for (int j = 0; j < i; j++) {
                        Movimiento movimiento = realm.createObject(Movimiento.class);
                        movimiento.concepto = "Movimiento_" + j;
                        movimiento.importe = 3.45 * (j+i);
                        movimiento.tipo = "GASTO";
                        cuenta.getMovimientos().add(movimiento);
                    }
                }
            }
        });

        // Implicit read transactions allow you to access your objects
        status += "\nNúmero de cuentas: " + realm.where(Cuenta.class).count();

        // Iterate over all objects
        for (Cuenta account : realm.where(Cuenta.class).findAll()) {
            String nombreUsuario;
            if (account.getUsuario() == null) {
                nombreUsuario = "None";
            } else {
                nombreUsuario = account.getUsuario().nombre;
            }
            status += "\n" + account.getNombre() + ": " + nombreUsuario + " : " + account.getMovimientos().size();
        }

        // Sorting
        RealmResults<Cuenta> sortedCuentas = realm.where(Cuenta.class).findAllSorted("nombre", Sort.ASCENDING);
        status += "\nSorting " + sortedCuentas.last().getNombre() + " == " + realm.where(Cuenta.class).findFirst()
                .getNombre();

        realm.close();
        return status;
    }

    private String complexQuery() {
        String status = "\n\nPerforming complex Query operation...";

        Realm realm = Realm.getDefaultInstance();
        status += "\nNúmero de cuentas: " + realm.where(Cuenta.class).count();

        // Find all persons where age between 7 and 9 and name begins with "Cuenta".
        RealmResults<Cuenta> results = realm.where(Cuenta.class)
                //.between("age", 7, 9)       // Notice implicit "and" operation
                .beginsWith("nombre", "Cuen").findAll();
        status += "\nNúmero de cuentas: " + results.size();
        for(Cuenta account: results){
            double impGasto = 0;
            for(Movimiento movimiento: account.getMovimientos()){
                impGasto += movimiento.importe;
            }
            status += String.format("\nCuenta (%s) tiene un total de gastos: %s", account.getNombre(), impGasto);
        }

        realm.close();
        return status;
    }
}
