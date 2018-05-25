package petro.presidencia.votacion.subactividades;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.menuActivity;
import petro.presidencia.votacion.subactividades.fragmentos.anomaliaFragment;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class anomaliasActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {


    Dialog DD;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    denuncuasAdapter adapter;


    JSONArray MESAS;
    String  mensajes = "";
    String tipoanomalia="";
    int id_anomalia = 0;

    FirebaseAnalytics mFirebaseAnalytics;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        setTitle("Anomalías");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(this, "Anomalias", null /* class override */);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        menuActivity.prefs = getSharedPreferences(menuActivity.MY_PREFS_NAME, MODE_PRIVATE);

    }
    int numTABS=0;


    private void setupViewPager(ViewPager viewPager) {

        adapter = new denuncuasAdapter(getSupportFragmentManager());

        //adapter.addFragment(new anomaliaFragment(), "Mesa 1");
        //adapter.addFragment(new anomaliaFragment(), "Mesa 2");
        //adapter.addFragment(new anomaliaFragment(), "Mesa 3");

        try {
            String user = menuActivity.prefs.getString("user", "");
            Log.i("user-pref", user);
            JSONArray JARRAY = new JSONObject(user).getJSONObject("user").getJSONArray("tables");

            for (int i = 0; i < JARRAY.length(); i++) {
                JSONObject JO = JARRAY.getJSONObject(i);
                int ID = JO.getInt("id");
                String mesanombre = JO.getString("name");
                anomaliaFragment AF = anomaliaFragment.getAnomaliaFragment(ID);

                numTABS++;
                adapter.addFragment(AF, mesanombre);
            }

            tabLayout.setTabMode(numTABS>3?TabLayout.MODE_SCROLLABLE:TabLayout.MODE_FIXED);


        } catch (Exception e) {
            Toasty.error(this, "Error: Deslogueate y vuelvete a loguear en la aplicacion", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        viewPager.setAdapter(adapter);
    }


    class denuncuasAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public denuncuasAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.getItem(viewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }

    View anomaliaView;

    public void denuncia(View view) {
        anomaliaView=view;
        int id = view.getId();


        switch (id) {
            case R.id.jurados_votacion:

                mensajes = "Está a punto de reportar la suplantación de jurados de votación o la presencia de jurados no registrados en la mesa elegida";
                id_anomalia = 1;
                break;
            case R.id.limitaciones_al_derecho:
                mensajes = "Está a punto de reportar una o varias circunstancias que impiden el libre derecho al voto o el voto secreto ";
                id_anomalia = 2;
                break;
            case R.id.censo_extracontemporaneo:
                mensajes = "Está a punto de reportar que en la mesa elegida se depositaron votos en la urna después del cierre de las votaciones a las 4PM";
                id_anomalia = 3;
                break;
            case R.id.no_destruccion:
                mensajes = "Está a punto de reportar que en la mesa elegida no se destruyó todo o parte del material electoral sobrante";
                id_anomalia = 4;
                break;
            case R.id.errores_en_el_conteo:
                mensajes = "Está a punto de reportar que en la mesa elegida no se hizo nivelación de mesa, se destruyeron votos válidos u otras irregularidades en el conteo de los votos";
                id_anomalia = 5;
                break;
            case R.id.errores_e14:
                mensajes = "Está a punto de reportar que en los formatos E14 de la mesa elegida contienen errores que pueden modificar los resultados electorales";
                id_anomalia = 6;
                break;
        }

        mensajes+="\n\n Asegúrate de hacer la reclamación correspondiente.";
        tipoanomalia=((TextView)view).getText().toString();

        DD = new Dialog(this);
        DD = createDialog(mensajes);
        DD.show();

    }


    public Dialog createDialog(String mensaje) {
        return new AlertDialog.Builder(this)
                .setTitle("Enviar incidencia").setMessage(mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enviar_anomalia();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((RadioButton)anomaliaView).setChecked(false);
                    }
                }).setCancelable(false)
                .create();
    }

    public void enviar_anomalia(){


        anomaliaFragment F = (anomaliaFragment) adapter.getItem(viewPager.getCurrentItem());

        JSONObject JO = new JSONObject();
        try {
            String query = "{\"report\": {\"issue_id\": " + id_anomalia + " ,\"table_id\": " + F.ID + "}}";

            JO = new JSONObject(query);
            Log.i("anomalia-query",JO.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String URL = getResources().getString(R.string.SERVER) + "/api/issue";
        JsonObjectRequest JOA = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                JO,
                this, this
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/json");
                params.put("Authorization", menuActivity.token);
                return params;
            }
        };

        Peticiones.hacerPeticion(this, JOA);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has("ok")) {
                if (response.getBoolean("ok") == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Anomalía: "+tipoanomalia+". Ha sido envíada exitosamente.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toasty.error(this,"Verifica tu conexion a internet",Toast.LENGTH_SHORT).show();
        ((RadioButton)anomaliaView).setChecked(false);
        error.printStackTrace();
    }


}
