package petro.presidencia.votacion.vuelta2.actividades.testigos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class votacionActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    private static final int selectPhoto = 100;
    EditText ttotalvot;
    EditText tpetro, tivanduque;
    EditText tblanco, tnulos, tnomarcados;
    EditText te11,tvurna,tvincinerados;
    TextView votos_validos, total_votos;
    View formulario;

    int total_candidatos = 0, total_otros = 0;

    Dialog DD;

    LinearLayout linear;
    TextView textphoto;
    ImageView imagePhoto;
    Button submit;
    Uri selectedImage;

    String IMAGEN_URL;

    DatabaseReference fdatabase;
    StorageReference storageReference;
    FirebaseAnalytics mFirebaseAnalytics;
    FirebaseStorage storage;

    int ID;
    String NOMBRE_MESA;

    TextView tmesanme, tpuestotexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frament_tarjeton);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(this, "Votacion", null /* class override */);

        setTitle("Registro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fdatabase = FirebaseDatabase.getInstance().getReference();

        tmesanme = (TextView) findViewById(R.id.nummesa);

        textphoto = (TextView) findViewById(R.id.text_image);
        imagePhoto = (ImageView) findViewById(R.id.image);
        linear = (LinearLayout) findViewById(R.id.linear_image);
        submit = (Button) findViewById(R.id.t_btndatos);

        te11  = (EditText) findViewById(R.id.t_e11);
        tvurna  = (EditText) findViewById(R.id.t_vurna);
        tvincinerados = (EditText) findViewById(R.id.t_vincinerados);

        submit.setOnClickListener(this);
        linear.setOnClickListener(this);

        ttotalvot = (EditText) findViewById(R.id.t_totalvot);
        tpetro = (EditText) findViewById(R.id.t_petro);
        tivanduque = (EditText) findViewById(R.id.t_ivan_duque);


        tblanco = (EditText) findViewById(R.id.t_votos_blanco);
        tnulos = (EditText) findViewById(R.id.t_votos_nulos);
        tnomarcados = (EditText) findViewById(R.id.t_votos_no_marcados);

        votos_validos = (TextView) findViewById(R.id.total_votos_validos);
        total_votos = (TextView) findViewById(R.id.totalv);
        formulario = (LinearLayout) findViewById(R.id.layout_formulario);


        TextWatcher candidatos = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.equals("")) {
                    return;
                }
                hacerConteo();
            }
        };

        tpetro.addTextChangedListener(candidatos);
        tivanduque.addTextChangedListener(candidatos);
        tblanco.addTextChangedListener(candidatos);
        tnulos.addTextChangedListener(candidatos);
        tnomarcados.addTextChangedListener(candidatos);


        tpuestotexto = (TextView) findViewById(R.id.puestotexto);


        ID = getIntent().getIntExtra("id", 0);
        NOMBRE_MESA = getIntent().getStringExtra("name");

        tmesanme.setText(NOMBRE_MESA);
        tpuestotexto.setText(estaticos.puesto);
        //desactivar_vista();
    }

    void desactivar_vista() {
        setViewAndChildrenEnabled(formulario, false);
    }

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    void hacerConteo() {
        total_candidatos = 0;

        if (!TextUtils.isEmpty(tpetro.getText().toString())) {
            total_candidatos += Integer.parseInt(tpetro.getText().toString());
        }

        if (!TextUtils.isEmpty(tivanduque.getText().toString())) {
            total_candidatos += Integer.parseInt(tivanduque.getText().toString());
        }


        if (!TextUtils.isEmpty(tblanco.getText().toString())) {
            total_candidatos += Integer.parseInt(tblanco.getText().toString());
        }

        votos_validos.setText("Total votos validos: " + total_candidatos);


        total_otros = total_candidatos;


        if (!TextUtils.isEmpty(tnulos.getText().toString())) {
            total_otros += Integer.parseInt(tnulos.getText().toString());
        }

        if (!TextUtils.isEmpty(tnomarcados.getText().toString())) {
            total_otros += Integer.parseInt(tnomarcados.getText().toString());
        }

        total_votos.setText("Total votos: " + (total_otros));
    }


    public void subirImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, selectPhoto);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


        if (id == R.id.t_btndatos) {


            DD = new Dialog(this);
            DD = confirmar_envio_mesa();
            DD.show();


        } else if (id == R.id.linear_image) {
            subirImagen();
        }
    }

    ProgressDialog progressDialog;

    private void uploadImage() {

        if (selectedImage != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo datos...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            IMAGEN_URL = taskSnapshot.getDownloadUrl().toString();
                            Log.i("firebase-image", IMAGEN_URL);
                            //Toast.makeText(getActivity(), "Imagen Subida", Toast.LENGTH_SHORT).show();
                            //subir_datos();
                            subir_datos_a_servidores();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (80.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Subiendo " + (int) progress + "%");
                        }
                    });
        }
    }

    public Dialog confirmar_envio_mesa() {

        return new AlertDialog.Builder(this)
                .setTitle("Enviar datos mesa").setMessage("¿Está seguro de enviar los datos de la mesa?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enviard_data_confirmado();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DD.dismiss();
                    }
                })
                .create();
    }

    void enviard_data_confirmado() {
        submit.setEnabled(false);
        Log.d("Conteo", "Total_candidatos: " + total_candidatos + " | Total_otros:" + total_otros + " | Total votos digitados: " + ttotalvot.getText().toString());
        if (ttotalvot.getText().toString().equals("") || total_otros != Integer.parseInt(ttotalvot.getText().toString())) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle("Error en conteo de votos");
            dialog.setMessage("Hay un error en el conteo de los votos, puede ser error tuyo o del jurado, ¡ solicita un reconteo de votos!");
            dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            final AlertDialog alert = dialog.create();
            alert.show();
            submit.setEnabled(true);
            return;
        }

        if (selectedImage == null) {
            Toasty.info(this, "Debes subir una imagen", Toast.LENGTH_SHORT).show();
            submit.setEnabled(true);

        } else {
            uploadImage();
        }
    }


    void subir_datos_a_servidores() {
        validar_campos();


        int petro = Integer.parseInt(tpetro.getText().toString());
        int ivanduque = Integer.parseInt(tivanduque.getText().toString());
        int blanco = Integer.parseInt(tblanco.getText().toString());
        int nulos = Integer.parseInt(tnulos.getText().toString());
        int nomarcados = Integer.parseInt(tnomarcados.getText().toString());

        int e11 = Integer.parseInt(te11.getText().toString());
        int urna = Integer.parseInt(tvurna.getText().toString());
        int incinerados = Integer.parseInt(tvincinerados.getText().toString());

        HashMap<String, Object> resultados = new HashMap<>();
        resultados.put("petro", petro );
        resultados.put("ivanduque", ivanduque );
        resultados.put("blanco", blanco );
        resultados.put("nulos", nulos );
        resultados.put("nomarcados", nomarcados );
        resultados.put("e11", e11 );
        resultados.put("urna", urna);
        resultados.put("incinerados", incinerados);
        resultados.put("imagen", IMAGEN_URL);
        resultados.put("total", total_otros);

        //resultados.put("mesa",)
        resultados.put("municipio", estaticos.municipio);
        resultados.put("departamento", estaticos.departamento);
        resultados.put("puesto", estaticos.puesto);
        resultados.put("cedula", estaticos.cedula);


        fdatabase.child("/post_votos/" + UUID.randomUUID().toString()).setValue(resultados);


        try {
            String URL = getResources().getString(R.string.SERVER) + "/api/results";

            final JSONObject jsonBody = new JSONObject("{\n" +
                    "  \"round\": {\n" +
                    "    \"table_id\": " + ID + ",\n" +
                    "    \"votes\": {\n" +
                    "      \"petro\": "+petro+",\n" +
                    "      \"duque\": "+ivanduque+",\n" +
                    "      \"blanco\": "+blanco+",\n" +
                    "      \"nulos\": "+nulos+",\n" +
                    "      \"no_marcados\": "+nomarcados+",\n" +
                    "      \"total_e11\": "+e11+",\n" +
                    "      \"total_urna\": "+urna+",\n" +
                    "      \"total_incinerados\": "+incinerados+",\n" +
                    "      \"total_validos\": "+total_otros+" \n" +
                    "    },\n" +
                    "    \"image\": \"https://pbs.twimg.com/media/DYLbFd4WkAAzB-z.jpg\"\n" +
                    "  }\n" +
                    "}");
            Log.i("registro-mesa",jsonBody.toString());

            JsonObjectRequest JOA = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    jsonBody,
                    this, this
            ){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("content-type", "application/json");
                    params.put("Authorization", estaticos.TOKEN);
                    return params;
                }
            };;

            Peticiones.hacerPeticion(this, JOA);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void validar_campos() {

        if ("".equals(tpetro.getText().toString())) {
            tpetro.setText("0");
        }

        if ("".equals(tblanco.getText().toString())) {
            tblanco.setText("0");
        }
        if ("".equals(tnulos.getText().toString())) {
            tnulos.setText("0");
        }
        if ("".equals(tnomarcados.getText().toString())) {
            tnomarcados.setText("0");
        }

        if ("".equals(votos_validos.getText().toString())) {
            votos_validos.setText("0");
        }

        if ("".equals(total_votos.getText().toString())) {
            total_votos.setText("0");
        }

        if ("".equals(te11.getText().toString())) {
            te11.setText("0");
        }

        if ("".equals(tvurna.getText().toString())) {
            tvurna.setText("0");
        }

        if ("".equals(tvincinerados.getText().toString())) {
            tvincinerados.setText("0");
        }

        if ("".equals(tivanduque.getText().toString())) {
            tivanduque.setText("0");
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case selectPhoto:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    Picasso.with(this).load(selectedImage).into(imagePhoto);
                    imagePhoto.setAdjustViewBounds(true);
                    textphoto.setVisibility(View.GONE);
                }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResponse(JSONObject response) {
        DD.dismiss();
        DD = datos_enviados_dialog();
        DD.show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    public Dialog datos_enviados_dialog() {
        return new AlertDialog.Builder(this)
                .setTitle("¡Gracias!").setMessage("Los datos se han enviado satisfactoriamente.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
    }
}
