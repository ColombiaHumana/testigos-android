package petro.presidencia.votacion.subactividades.fragmentos;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;


import petro.presidencia.votacion.menuActivity;
import petro.presidencia.votacion.subactividades.votacionActivity;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

import static android.app.Activity.RESULT_OK;
import static petro.presidencia.votacion.menuActivity.editor;
import static petro.presidencia.votacion.menuActivity.prefs;

public class mesaFragment extends Fragment implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {



    public static mesaFragment getMesaFragment(int ID){
        mesaFragment F = new mesaFragment();
        Bundle args = new Bundle();
        args.putInt("id", ID);
        F.setArguments(args);
        return F;
    }


    public mesaFragment() {

    }

    private static final int selectPhoto = 100;


    JSONObject JO_datoscammpos;

    JSONArray mesasvotadas;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri selectedImage;

    LinearLayout linear;
    TextView textphoto;
    ImageView imagePhoto;

    Button submit;

    int ID;
    String IMAGEN_URL;

    EditText ttotalvot;
    EditText tpetro, tvotblanco,tivanduque,thumberto,ttrujillo,tfajardo,tvmorales,tvargas;
    EditText tblanco,tnulos,tnomarcados;

    TextView votos_validos, total_votos;
    View formulario;

    int total_candidatos=0, total_otros=0;

    Dialog DD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V= inflater.inflate(R.layout.frament_tarjeton, container, false);

        textphoto =(TextView)V.findViewById(R.id.text_image);
        imagePhoto =(ImageView)V.findViewById(R.id.image);
        linear = (LinearLayout)V.findViewById(R.id.linear_image);
        submit=(Button)V.findViewById(R.id.t_btndatos);
        submit.setOnClickListener(this);
        linear.setOnClickListener(this);

        Bundle args = getArguments();
        this.ID= args.getInt("id", 0);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ttotalvot= (EditText)V.findViewById(R.id.t_totalvot);
        tpetro= (EditText)V.findViewById(R.id.t_petro);
        tvotblanco= (EditText)V.findViewById(R.id.t_pro_blanco);
        tivanduque= (EditText)V.findViewById(R.id.t_ivan_duque);
        thumberto= (EditText)V.findViewById(R.id.t_humberto_calles);
        ttrujillo= (EditText)V.findViewById(R.id.t_jtrujillo);
        tfajardo= (EditText)V.findViewById(R.id.t_s_fajardo);
        tvmorales= (EditText)V.findViewById(R.id.t_v_morales);
        tvargas= (EditText)V.findViewById(R.id.t_vargas_lleras);

        tblanco=(EditText)V.findViewById(R.id.t_votos_blanco);
        tnulos=(EditText)V.findViewById(R.id.t_votos_nulos);
        tnomarcados=(EditText)V.findViewById(R.id.t_votos_no_marcados);

        votos_validos=(TextView)V.findViewById(R.id.total_votos_validos);
        total_votos =(TextView)V.findViewById(R.id.totalv);
        formulario = (LinearLayout)V.findViewById(R.id.layout_formulario);



        if(prefs.contains("mesa-"+String.valueOf(ID))){
            try{
                JO_datoscammpos = new JSONObject(prefs.getString("mesa-"+String.valueOf(ID),""));
                ttotalvot.setText(JO_datoscammpos.getString("ttotalvot"));
                tpetro.setText(JO_datoscammpos.getString("tpetro"));
                tvotblanco.setText(JO_datoscammpos.getString("tvotblanco"));
                tivanduque.setText(JO_datoscammpos.getString("tivanduque"));
                thumberto.setText(JO_datoscammpos.getString("thumberto"));
                ttrujillo.setText(JO_datoscammpos.getString("ttrujillo"));
                tfajardo.setText(JO_datoscammpos.getString("tfajardo"));
                tvmorales.setText(JO_datoscammpos.getString("tvmorales"));
                tvargas.setText(JO_datoscammpos.getString("tvargas"));
                tblanco.setText(JO_datoscammpos.getString("tblanco"));
                tnulos.setText(JO_datoscammpos.getString("tnulos"));
                tnomarcados.setText(JO_datoscammpos.getString("tnomarcados"));

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            JO_datoscammpos =new JSONObject();
        }


        if(prefs.contains(votacionActivity.mesasvotadasString)){
            try {
                mesasvotadas = new JSONArray(
                        prefs.getString(votacionActivity.mesasvotadasString,"")
                );

                for(int i=0;i<mesasvotadas.length();i++){
                    if(mesasvotadas.getInt(i)==ID){
                        desactivar_vista();
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            mesasvotadas = new JSONArray();
        }


        TextWatcher candidatos = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.equals("")){
                    return;
                }

                hacerConteo();

            }
        };



        TextWatcher blanconulomarcado = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.equals("")){
                    return;
                }
               hacerConteo();
            }
        };


        tpetro.addTextChangedListener(candidatos);
        tvotblanco.addTextChangedListener(candidatos);
        tivanduque.addTextChangedListener(candidatos);
        thumberto.addTextChangedListener(candidatos);
        ttrujillo.addTextChangedListener(candidatos);
        tfajardo.addTextChangedListener(candidatos);
        tvmorales.addTextChangedListener(candidatos);
        tvargas.addTextChangedListener(candidatos);

        tblanco.addTextChangedListener(blanconulomarcado);
        tnulos.addTextChangedListener(blanconulomarcado);
        tnomarcados.addTextChangedListener(blanconulomarcado);

        return V;
    }

    @Override
    public void onStart() {
        super.onStart();
        hacerConteo();
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

    void desactivar_vista(){
        setViewAndChildrenEnabled(formulario,false);
    }


    void hacerConteo(){
        total_candidatos=0;

        if(!TextUtils.isEmpty(tpetro.getText().toString())){
            total_candidatos+=Integer.parseInt(tpetro.getText().toString());
        }
        if(!TextUtils.isEmpty(tvotblanco.getText().toString())){
            total_candidatos+=Integer.parseInt(tvotblanco.getText().toString());
        }
        if(!TextUtils.isEmpty(tivanduque.getText().toString())){
            total_candidatos+=Integer.parseInt(tivanduque.getText().toString());
        }
        if(!TextUtils.isEmpty(thumberto.getText().toString())){
            total_candidatos+=Integer.parseInt(thumberto.getText().toString());
        }
        if(!TextUtils.isEmpty(ttrujillo.getText().toString())){
            total_candidatos+=Integer.parseInt(ttrujillo.getText().toString());
        }
        if(!TextUtils.isEmpty(tfajardo.getText().toString())){
            total_candidatos+=Integer.parseInt(tfajardo.getText().toString());
        }
        if(!TextUtils.isEmpty(tvmorales.getText().toString())){
            total_candidatos+=Integer.parseInt(tvmorales.getText().toString());
        }
        if(!TextUtils.isEmpty(tvargas.getText().toString())){
            total_candidatos+=Integer.parseInt(tvargas.getText().toString());
        }

        if(!TextUtils.isEmpty(tblanco.getText().toString())){
            total_candidatos+=Integer.parseInt(tblanco.getText().toString());
        }

        votos_validos.setText("Total votos validos: "+total_candidatos);


        total_otros = total_candidatos;


        if(!TextUtils.isEmpty(tnulos.getText().toString())){
            total_otros+=Integer.parseInt(tnulos.getText().toString());
        }

        if(!TextUtils.isEmpty(tnomarcados.getText().toString())){
            total_otros+=Integer.parseInt(tnomarcados.getText().toString());
        }

        total_votos.setText("Total votos: "+(total_otros));
    }



    @Override
    public void onDetach() {
        super.onDetach();
        try {git ad
            JO_datoscammpos.put("ttotalvot",ttotalvot.getText().toString());
            JO_datoscammpos.put("tpetro",tpetro.getText().toString());
            JO_datoscammpos.put("tvotblanco",tvotblanco.getText().toString());
            JO_datoscammpos.put("tivanduque",tivanduque.getText().toString());
            JO_datoscammpos.put("thumberto",thumberto.getText().toString());
            JO_datoscammpos.put("ttrujillo",ttrujillo.getText().toString());
            JO_datoscammpos.put("tfajardo",tfajardo.getText().toString());
            JO_datoscammpos.put("tvmorales",tvmorales.getText().toString());
            JO_datoscammpos.put("tvargas",tvargas.getText().toString());


            JO_datoscammpos.put("tblanco",tblanco.getText().toString());
            JO_datoscammpos.put("tnulos",tnulos.getText().toString());
            JO_datoscammpos.put("tnomarcados",tnomarcados.getText().toString());

            editor.putString(String.valueOf("mesa-"+ID), JO_datoscammpos.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }


    public Dialog confirmar_envio_mesa() {

        return new AlertDialog.Builder(getContext())
                .setTitle("Enviar datos mesa").setMessage("¿Está seguro de enviar los datos de la mesa?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enviard_data();
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

    void enviard_data(){
        submit.setEnabled(false);
        Log.d("Conteo","Total_candidatos: "+total_candidatos+" | Total_otros:"+total_otros+" | Total votos digitados: "+ttotalvot.getText().toString());
        if(ttotalvot.getText().toString().equals("") || total_otros != Integer.parseInt(ttotalvot.getText().toString())){


            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(false);
            dialog.setTitle("Error en conteo de votos");
            dialog.setMessage("Hay un error en el conteo de los votos, puede ser error tuyo o del jurado, ¡ solicita un reconteo de votos!" );
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

        if(selectedImage==null){

            Toasty.info(getActivity(),"Debes subir una imagen",Toast.LENGTH_SHORT).show();
            submit.setEnabled(true);

        }else{
            uploadImage();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();



        if(id==R.id.t_btndatos){
            DD = new Dialog(getContext());
            DD= confirmar_envio_mesa();
            DD.show();


        } else if( id == R.id.linear_image) {
            subirImagen();
        }
    }

    void validar_campos(){

        if("".equals(tpetro.getText().toString())){
            tpetro.setText("0");
        }
        if("".equals(tvotblanco.getText().toString())){
            tvotblanco.setText("0");
        }
        if("".equals(tivanduque.getText().toString())){
            tivanduque.setText("0");
        }
        if("".equals(thumberto.getText().toString())){
            thumberto.setText("0");
        }
        if("".equals(ttrujillo.getText().toString())){
            ttrujillo.setText("0");
        }
        if("".equals(tfajardo.getText().toString())){
            tfajardo.setText("0");
        }
        if("".equals(tvmorales.getText().toString())){
            tvmorales.setText("0");
        }
        if("".equals(tvargas.getText().toString())){
            tvargas.setText("0");
        }

        if("".equals(tblanco.getText().toString())){
            tblanco.setText("0");
        }
        if("".equals(tnulos.getText().toString())){
            tnulos.setText("0");
        }
        if("".equals(tnomarcados.getText().toString())){
            tnomarcados.setText("0");
        }


    }



    public void subir_datos(){
        progressDialog.setMessage("Subiendo 81%");

        validar_campos();

        String ttotalvotos =ttotalvot.getText().toString();
        String stpetro=tpetro.getText().toString();
        String stvotblanco=tvotblanco.getText().toString();
        String stivanduque=tivanduque.getText().toString();
        String sthumberto=thumberto.getText().toString();
        String sttrujillo=ttrujillo.getText().toString();
        String stfajardo=tfajardo.getText().toString();
        String stvmorales=tvmorales.getText().toString();
        String stvargas=tvargas.getText().toString();

        String stblanco=tblanco.getText().toString();
        String stnulos=tnulos.getText().toString();
        String stnomarcados=tnomarcados.getText().toString();







        JSONObject JO = new JSONObject();
        try {




            String query = "{" +
                    "  \"result\": {" +
                    "    \"table_id\": "+ID+"," +
                    "    \"votes\": {" +
                    "      \"total_mesa\": "+ttotalvotos+"," +
                    "      \"petro\": "+stpetro+"," +
                    "      \"promotores\": "+stvotblanco+"," +
                    "      \"duque\": "+stivanduque+"," +
                    "      \"la_calle\": "+sthumberto+"," +
                    "      \"trujillo\": "+sttrujillo+"," +
                    "      \"fajardo\": "+stfajardo+"," +
                    "      \"morales\": "+stvmorales+"," +
                    "      \"vargas\": "+stvargas+"," +
                    "      \"votos_validos\": "+total_candidatos+"," +
                    "      \"votos_blancos\": "+stblanco+"," +
                    "      \"votos_nulos\": "+stnulos+"," +
                    "      \"votos_no_marcados\": "+stnomarcados+"," +
                    "      \"total\": "+ttotalvotos+"" +
                    "    }," +
                    "    \"image\": \""+IMAGEN_URL+"\"" +
                    "  }" +
                    "}";

            JO = new JSONObject(query);
            Log.i("anomalia-query",JO.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String URL = getResources().getString(R.string.SERVER) + "/api/results";
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

        Peticiones.hacerPeticion(getActivity(), JOA);

    }

    public void subirImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, selectPhoto);

    }

    ProgressDialog progressDialog;

    private void uploadImage() {

        if(selectedImage != null)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Subiendo datos...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            IMAGEN_URL =taskSnapshot.getDownloadUrl().toString();
                            Log.i("firebase-image",IMAGEN_URL);
                            //Toast.makeText(getActivity(), "Imagen Subida", Toast.LENGTH_SHORT).show();
                            subir_datos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (80.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Subiendo "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case selectPhoto:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    Picasso.with(getContext()).load(selectedImage).into(imagePhoto);
                    imagePhoto.setAdjustViewBounds(true);
                    textphoto.setVisibility(View.GONE);
                }
        }
    }


    boolean estamesa(){
        try {
            for(int i = 0;i<mesasvotadas.length();i++){
                if(ID==mesasvotadas.getInt(i)){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    void agregar_mesa(){
        if(!estamesa()){
            mesasvotadas.put(ID);
            editor.putString(votacionActivity.mesasvotadasString,mesasvotadas.toString());
            editor.apply();
        }
        desactivar_vista();
    }



    @Override
    public void onResponse(JSONObject response) {
        try{
            if(response.has("ok") && response.getBoolean("ok")==true){
                agregar_mesa();
                Toasty.success(getActivity(),"Datos subidos correctamente",Toast.LENGTH_SHORT).show();
                progressDialog.setMessage("Datos subidos correctamente");
                progressDialog.dismiss();
                Dialog mensaje = datos_enviados_correctamente();
                mensaje.show();
                agregar_mesa();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Dialog datos_enviados_correctamente() {

        return new AlertDialog.Builder(getContext())
                .setTitle("DATOS ENVIADOS").setMessage("Los datos se han enviado correctamente")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .create();
    }





    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        progressDialog.dismiss();
        submit.setEnabled(true);
        Toasty.error(getActivity(),"Error subiendo los datos, por favor inténtalo mas tarde.",Toast.LENGTH_SHORT).show();
    }
}
