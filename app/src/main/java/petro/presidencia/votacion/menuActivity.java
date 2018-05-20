package petro.presidencia.votacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import petro.presidencia.votacion.subactividades.anomaliasActivity;
import petro.presidencia.votacion.subactividades.asistenciaActivity;
import petro.presidencia.votacion.subactividades.guiaActivity;
import petro.presidencia.votacion.subactividades.votacionActivity;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class menuActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    ImageView guias,anomalias,registro,asistencia;



    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    public static String MY_PREFS_NAME = "login";

    public static String token;

    TextView puesto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("PETRO");

        guias=(ImageView)findViewById(R.id.guias);
        anomalias=(ImageView)findViewById(R.id.anomalias);
        registro=(ImageView)findViewById(R.id.registro);
        asistencia=(ImageView)findViewById(R.id.asistencia);
        puesto = (TextView)findViewById(R.id.puestotexto);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        token = "Bearer "+prefs.getString("token","");



        if(prefs.contains("user")){
            try {
                onResponse(new JSONObject(prefs.getString("user","")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        String URL = getResources().getString(R.string.SERVER)+"/api/user";
        JsonObjectRequest JOA = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                this, this
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/json");
                params.put("Authorization",token);
                return params;
            }
        };


        Peticiones.hacerPeticion(this,JOA);

    }

    private void activar_botones(){
        guias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),guiaActivity.class));
            }
        });

        anomalias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),anomaliasActivity.class));
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),votacionActivity.class));
            }
        });
        asistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),asistenciaActivity.class));
            }
        });
    }


    @Override
    public void onResponse(JSONObject response) {
        Log.i("RTA",response.toString());
        try{
            if(response.has("user")){
                editor.putString("user",response.toString());
                editor.apply();

                activar_botones();

                String nombre = response.getJSONObject("user").getString("name");
                //((TextView)HEADER.findViewById(R.id.head_text)).setText("Bienvenido: "+nombre);
                //((TextView)HEADER.findViewById(R.id.head_text)).setTextSize(18);

                puesto.setText(
                        "PUESTO: "+response.getJSONObject("user").getJSONObject("post").getString("name")
                );

                // SI ES COORDINADOR
                if(response.getJSONObject("user").has("coordinator") && response.getJSONObject("user").getBoolean("coordinator")){
                    asistencia.setVisibility(View.VISIBLE);
                    puesto.setVisibility(View.VISIBLE);
                }



                if (menuActivity.prefs.contains(votacionActivity.mesasvotadasString)) {
                    JSONArray mesasvotadasJ = new JSONArray(menuActivity.prefs.getString(votacionActivity.mesasvotadasString,""));
                    //int cant_mesas = mesasvotadasJ.length();
                    JSONArray arraymesas = new JSONObject(response.toString()).getJSONObject("user").getJSONArray("tables");

                    if(mesasvotadasJ.length() == arraymesas.length()){

                        // AGREGAR BOTON GRIS
                    }

                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            editor.clear().apply();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
