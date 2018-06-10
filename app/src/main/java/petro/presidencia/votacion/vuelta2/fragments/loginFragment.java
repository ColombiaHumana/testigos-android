package petro.presidencia.votacion.vuelta2.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import petro.presidencia.votacion.vuelta2.actividades.forgorActivity;
import petro.presidencia.votacion.vuelta2.actividades.guiaActivity;
import votacion.presidencia.petro.testigoscolombiahumana.R;


public class loginFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {


    public loginFragment() {

    }

    private View mProgressView;
    private View mLoginFormView;

    private EditText cc_campo;
    private EditText passwordcampo;
    Button submit;


    Context CTT;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CTT=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Testigos");

        mLoginFormView = V.findViewById(R.id.login_form);
        mProgressView = V.findViewById(R.id.login_progress);

        cc_campo = V.findViewById(R.id.input_cedula);
        passwordcampo = V.findViewById(R.id.input_password);
        submit = V.findViewById(R.id.login);
        return V;
    }



    void abrir_pagina_registrate(){
        String url = "https://petro.com.co/ingreso-testigos/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.boton_guia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),guiaActivity.class));
            }
        });


        view.findViewById(R.id.texto_recuperar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),forgorActivity.class));
            }
        });

        view.findViewById(R.id.boton_registrate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrir_pagina_registrate();
            }
        });

        view.findViewById(R.id.texto_registrate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrir_pagina_registrate();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

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


            final JSONObject jsonBody = new JSONObject("{\"auth\": {\"document\": \""+emaila+"\",\"password\": \""+password+"\"}}");


            JsonObjectRequest JOA = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    jsonBody,
                    this, this
            );

            Peticiones.hacerPeticion(CTT, JOA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                estaticos.editor.putString("token",token);
                estaticos.editor.apply();
                Toasty.success(CTT,"Bienvenido",Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        showProgress(false);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "normal");
        estaticos.fanaly.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        showProgress(false);
        Toasty.error(CTT, "Verifica tu conexion a internet o tu cédula y contraseña", Toast.LENGTH_SHORT).show();
        passwordcampo.setText("");
    }




}
