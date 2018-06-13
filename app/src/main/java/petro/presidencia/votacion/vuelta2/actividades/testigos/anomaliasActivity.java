package petro.presidencia.votacion.vuelta2.actividades.testigos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class anomaliasActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    Dialog DD;

    RadioButton jurados_votacion, limitaciones_al_derecho, censo_extracontemporaneo;
    RadioButton no_destruccion, errores_en_el_conteo, errores_e14;

    String  mensajes = "";
    String tipoanomalia="";
    int id_anomalia = 0;


    String NOMBRE_MESA;
    int ID_MESA;

    TextView tmesanme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_anomalias);
        setTitle("Anomalías");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jurados_votacion = (RadioButton)findViewById(R.id.jurados_votacion);
        limitaciones_al_derecho = (RadioButton)findViewById(R.id.limitaciones_al_derecho);
        censo_extracontemporaneo = (RadioButton)findViewById(R.id.censo_extracontemporaneo);
        no_destruccion = (RadioButton)findViewById(R.id.no_destruccion);
        errores_en_el_conteo = (RadioButton)findViewById(R.id.errores_en_el_conteo);
        errores_e14 = (RadioButton)findViewById(R.id.errores_e14);

        tmesanme = (TextView)findViewById(R.id.nummesa);


        ID_MESA =getIntent().getIntExtra("id",0);
        NOMBRE_MESA =getIntent().getStringExtra("name");
        tmesanme.setText(NOMBRE_MESA);
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


        JSONObject JO = new JSONObject();
        try {
            String query = "{\"report\": {\"issue_id\": " + id_anomalia + " ,\"table_id\": " + ID_MESA + "}}";

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
                params.put("Authorization", estaticos.TOKEN);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
