package petro.presidencia.votacion;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class forgorActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    EditText email;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgor);
        setTitle("Recuperar contraseña");
        email = (EditText)findViewById(R.id.email);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
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

            Peticiones.hacerPeticion(this, JOA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if(response.has("ok")){
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
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }
}
