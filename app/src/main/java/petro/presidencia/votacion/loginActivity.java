package petro.presidencia.votacion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class loginActivity extends AppCompatActivity implements com.android.volley.Response.Listener<JSONObject>, com.android.volley.Response.ErrorListener {


    private EditText cc_campo;
    private EditText passwordcampo;
    private View mProgressView;
    private View mLoginFormView;


    String TAG = "login";

    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    static String MY_PREFS_NAME = "login";
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("");


        cc_campo = (EditText)findViewById(R.id.cccampo);

        passwordcampo = (EditText) findViewById(R.id.password);
        passwordcampo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();





        submit =(Button) findViewById(R.id.sesion);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (prefs.getString("token", null) != null) {
            startActivity(new Intent(this, menuActivity.class));
            finish();
        }


    }

    private void attemptLogin() {
        cc_campo.setError(null);
        passwordcampo.setError(null);

        final String emaila = cc_campo.getText().toString();
        final String password = passwordcampo.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if ((password.length() < 4) || !isPasswordValid(password)) {
            passwordcampo.setError(getString(R.string.error_invalid_password));
            focusView = passwordcampo;
            cancel = true;
        }

        if (TextUtils.isEmpty(emaila)) {
            cc_campo.setError(getString(R.string.error_field_required));
            focusView = cc_campo;
            cancel = true;
        } else if (!isEmailValid(emaila)) {
            cc_campo.setError(getString(R.string.error_invalid_email));
            focusView = cc_campo;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
            return;
        }
        showProgress(true);


        try {
            String URL = getResources().getString(R.string.SERVER) + "/api/user_token";


            final JSONObject jsonBody = new JSONObject("{\"auth\": {\"email\": \""+emaila+"\",\"password\": \""+password+"\"}}");


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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isEmailValid(String cc) {
        return cc.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }



    @Override
    public void onResponse(JSONObject response) {
        Log.i("RTA",response.toString());
        try{
            if(response.has("jwt")){
                String token = response.getString("jwt");
                editor.putString("token",token);
                editor.apply();
                startActivity(new Intent(this, menuActivity.class));
                finish();

            }else{
                Toasty.error(this,"Contraseña o Email inválido", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toasty.error(this,"Error en el ingreso",Toast.LENGTH_SHORT).show();
            passwordcampo.setText("");
        }
        showProgress(false);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showProgress(false);
        Toasty.error(this, "Verifica tu conexion a internet o tu cédula y contraseña", Toast.LENGTH_SHORT).show();
        error.printStackTrace();
    }

    public void lostpassword(View view) {

    }

}
