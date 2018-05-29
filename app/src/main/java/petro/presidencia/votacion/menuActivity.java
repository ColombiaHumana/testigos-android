package petro.presidencia.votacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.subactividades.anomaliasActivity;
import petro.presidencia.votacion.subactividades.asistenciaActivity;
import petro.presidencia.votacion.subactividades.guiaActivity;
import petro.presidencia.votacion.subactividades.votacionActivity;
import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class menuActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    ImageView guias, anomalias, registro, asistencia;


    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    public static String MY_PREFS_NAME = "login";

    public static String token;

    TextView puesto;

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db;

    AlertDialog dialogoCorreo;

    DatabaseReference mDatabase;


    boolean es_data_guardada=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        db = FirebaseFirestore.getInstance();
        mFirebaseAnalytics.setCurrentScreen(this, "Menu", null /* class override */);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("PETRO");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        guias = (ImageView) findViewById(R.id.guias);
        anomalias = (ImageView) findViewById(R.id.anomalias);
        registro = (ImageView) findViewById(R.id.registro);
        asistencia = (ImageView) findViewById(R.id.asistencia);
        puesto = (TextView) findViewById(R.id.puestotexto);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        token = "Bearer " + prefs.getString("token", "");


        if (prefs.contains("user")) {
            try {
                es_data_guardada=true;
                onResponse(new JSONObject(prefs.getString("user", "")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        String URL = getResources().getString(R.string.SERVER) + "/api/user";
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
                params.put("Authorization", token);
                return params;
            }
        };


        Peticiones.hacerPeticion(this, JOA);

    }

    private void activar_botones() {
        guias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), guiaActivity.class));
            }
        });

        anomalias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), anomaliasActivity.class));
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), votacionActivity.class));
            }
        });
        asistencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), asistenciaActivity.class));
            }
        });
    }


    @Override
    public void onResponse(JSONObject response) {
        Log.i("RTA", response.toString());
        try {
            if (response.has("user")) {
                editor.putString("user", response.toString());
                editor.apply();

                activar_botones();

                String nombre = response.getJSONObject("user").getString("name");
                //((TextView)HEADER.findViewById(R.id.head_text)).setText("Bienvenido: "+nombre);
                //((TextView)HEADER.findViewById(R.id.head_text)).setTextSize(18);

                puesto.setText(
                        "PUESTO: " + response.getJSONObject("user").getJSONObject("post").getString("name")
                );

                // SI ES COORDINADOR
                if (response.getJSONObject("user").has("coordinator") && response.getJSONObject("user").getBoolean("coordinator")) {
                    asistencia.setVisibility(View.VISIBLE);
                    puesto.setVisibility(View.VISIBLE);
                }


                if (response.getJSONObject("user").has("email") && !response.getJSONObject("user").getBoolean("email")) {

                    if (!prefs.getBoolean("correoenviado", false)) {
                        mostrarDialogoPedirCorreo();
                    }

                }


                String ccedula = String.valueOf(response.getJSONObject("user").getInt("cedula"));
                estaticos.cedula = ccedula;

                String departamento = response.getJSONObject("user").getJSONObject("department").getString("name");
                String municipio = response.getJSONObject("user").getJSONObject("municipality").getString("name");
                String puesto = response.getJSONObject("user").getJSONObject("post").getString("name");
                String escoordinador = response.getJSONObject("user").getBoolean("coordinator") ? "coordinador" : "testigo";


                HashMap<String, Object> result = new HashMap<>();
                result.put("departamento",estaticos.departamento);
                result.put("municipio",estaticos.municipio);
                result.put("puesto",estaticos.puesto);
                mDatabase.child("/votacion").child("/"+ estaticos.cedula).child("/lugar").setValue(result);


                String sdepartamento = departamento.replace(" ", "-");
                String smunicipio = municipio.replace(" ", "-");
                String spuesto = puesto.replace(" ", "-");

                FirebaseMessaging.getInstance().subscribeToTopic(sdepartamento);
                FirebaseMessaging.getInstance().subscribeToTopic(smunicipio);
                FirebaseMessaging.getInstance().subscribeToTopic(spuesto);
                FirebaseMessaging.getInstance().subscribeToTopic(escoordinador);

                if (!es_data_guardada) {
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    if (refreshedToken != null) {

                        Map<String, Object> user = new HashMap<>();
                        user.put("token", refreshedToken);

                        db.collection("token-usuarios").document(estaticos.cedula)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("token", "agregado a firestore");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    mFirebaseAnalytics.setUserId(estaticos.cedula);
                    mFirebaseAnalytics.setUserProperty("Departamento",departamento);
                    mFirebaseAnalytics.setUserProperty("Municipio",municipio);
                    mFirebaseAnalytics.setUserProperty("Puesto",puesto);


                }



                estaticos.departamento=departamento;
                estaticos.municipio = municipio;
                estaticos.puesto = puesto;

            }

        } catch (Exception e) {
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
            mFirebaseAnalytics.resetAnalyticsData();

            db.collection("token-usuarios").document("/"+estaticos.cedula).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            editor.clear().apply();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void enviar_correo(String email) throws Exception {

        Response.Listener<JSONObject> rta = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (!response.has("user")) {
                    return;
                }
                editor.putBoolean("correoenviado", true);
                editor.apply();
                Toasty.success(getApplicationContext(), "Tu Email ha sido registrado", Toast.LENGTH_LONG).show();
            }

            ;
        };

        String ask = "{\n" +
                "    \"user\": {\n" +
                "        \"email\": \"" + email + "\"\n" +
                "    }\n" +
                "}";
        JSONObject user = new JSONObject(ask);


        String URL = getResources().getString(R.string.SERVER) + "/api/user/email";
        JsonObjectRequest JOA = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                user,
                rta, this
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };


        Peticiones.hacerPeticion(this, JOA);


        dialogoCorreo.dismiss();
    }


    void mostrarDialogoPedirCorreo() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pedircorreo, null);
        dialogoCorreo = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Por favor escribe tu E-mail")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .create();


        dialogoCorreo.show();
        final EditText campo_correo = (EditText) dialogoCorreo.findViewById(R.id.input_correo);
        final TextInputLayout texinput = (TextInputLayout) dialogoCorreo.findViewById(R.id.input_layout_email);

        Button b = dialogoCorreo.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = campo_correo.getText().toString();
                Log.i("validacion", "Correo: " + campo_correo.getText().toString());
                if (isEmailValid(email)) {
                    try {

                        enviar_correo(email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    texinput.setError("E-mail inválido");
                }
            }
        });


    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
