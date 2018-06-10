package petro.presidencia.votacion.vuelta2.actividades;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class forgorActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    EditText email;
    Button submit;
    TextView mesaje;

    private FirebaseAnalytics mFirebaseAnalytics;
    boolean back=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(this, "RecuperarContrasena", null /* class override */);
        setContentView(R.layout.activity_forgor);
        setTitle("Recuperar contraseña");
        email = (EditText)findViewById(R.id.email);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        mesaje = (TextView)findViewById(R.id.noemail);
    }

    @Override
    public void onClick(View v) {
        try {
            String URL = getResources().getString(R.string.SERVER) + "/api/password_reset/";


            final JSONObject jsonBody = new JSONObject(
                    "{ \"password_reset\":{ \"email\":\""+email.getText().toString()+"\" } }"
            );

            JsonObjectRequest JOA = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    jsonBody,
                    this, this
            );
            back=false;
            Peticiones.hacerPeticion(this, JOA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            if(back){
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONObject response) {

        if(response.has("ok")){
            Bundle params = new Bundle();
            params.putInt("correos", 1);
            mFirebaseAnalytics.logEvent("correos_enviados", params);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Se ha enviado un correo de recuperación a tu cuenta.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            back=true;

            Bundle b  = new Bundle();
            estaticos.fanaly.logEvent("recuperar",b);
        }

    }

    @Override
    public void finish() {
        if(back){
            super.finish();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mesaje.setVisibility(View.VISIBLE);
        back=true;
        error.printStackTrace();
    }
}
