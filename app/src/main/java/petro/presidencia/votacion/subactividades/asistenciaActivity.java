package petro.presidencia.votacion.subactividades;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.menuActivity;
import petro.presidencia.votacion.subactividades.fragmentos.anomaliaFragment;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

import static petro.presidencia.votacion.menuActivity.editor;
import static petro.presidencia.votacion.menuActivity.prefs;

public class asistenciaActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, Response.ErrorListener, Response.Listener<JSONObject> {


    JSONArray users;
    ListView lista;
    JSONArray asistidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        setTitle("Asistencia");


        try {
            users = new JSONObject(prefs.getString("user", "")).getJSONObject("user").getJSONArray("users");
            Log.i("users", users.toString());
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        lista = (ListView) findViewById(R.id.listado);
        usuariosAdapter adaptador = new usuariosAdapter(this);
        lista.setAdapter(adaptador);

        try{

            if(prefs.contains("asistentes")){
                asistidos = new JSONArray(
                        prefs.getString("asistentes","")
                );
                Log.i("asistidos",prefs.getString("asistentes",""));
            }else{
                asistidos = new JSONArray();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    boolean esta_marcado(int id){
        try{
            for(int i=0;i<asistidos.length();i++){
                if(asistidos.getInt(i)==id){
                    return true;
               }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public Dialog createDialogConfirmacion() {
        String nombre="";
        try{
            nombre += users.getJSONObject(posicionARRAY).getString("name");
        }catch (Exception e){
            e.printStackTrace();
        }

        String mensaje = "¿Está seguro que "+nombre+" asistió al puesto de votación?";
        Dialog D = new AlertDialog.Builder(this)
                .setTitle("Enviar Assitencia").setMessage(mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enviar_asistencia();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkboxbutton.setChecked(false);
                    }
                }).setCancelable(false)
                .create();
        return D;
    }

    public void enviar_asistencia(){

        JSONObject JO = new JSONObject();
        try {
            String query = "{\n" +
                    "    \"user\": {\n" +
                    "        \"id\": "+idARRAY+"\n" +
                    "    }\n" +
                    "}";

            JO = new JSONObject(query);
            Log.i("asistencia-query",JO.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String URL = getResources().getString(R.string.SERVER) + "/api/user";
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

                    asistidos.put(idARRAY);
                    String nombre = users.getJSONObject(posicionARRAY).getString("name");

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("La asistencia del testigo "+nombre+" ha sido envíada satisfactoriamente.")
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
        Toasty.error(this,"Verifica tu conexion a internet", Toast.LENGTH_SHORT).show();
        checkboxbutton.setChecked(false);
        error.printStackTrace();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putString("asistentes",asistidos.toString());
        editor.apply();
    }

    Dialog DD;
    int posicionARRAY;
    int idARRAY;
    CompoundButton checkboxbutton;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!isChecked){
            return;
        }
        try {
            checkboxbutton = buttonView;
            posicionARRAY = (int)buttonView.getTag();
            idARRAY = users.getJSONObject(posicionARRAY).getInt("id");
            DD = createDialogConfirmacion();
            DD.show();
            //asistidos.put(ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    class usuariosAdapter extends BaseAdapter {

        asistenciaActivity ctt;

        public usuariosAdapter(asistenciaActivity CTT) {
            ctt = CTT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View V = LayoutInflater.from(ctt).inflate(R.layout.item_asistente, parent, false);

            LinearLayout layout = (LinearLayout) V.findViewById(R.id.item_usuario);
            CheckBox radio = (CheckBox) V.findViewById(R.id.check_box);
            TextView nombre = (TextView) V.findViewById(R.id.iarriba);
            TextView abajo = (TextView) V.findViewById(R.id.iabajo);
            String scedula = "", smesas = "";

            try {
                if (position % 2 + 1 == 0) {
                    int color = Color.parseColor("#C3C3C3");
                    layout.setBackgroundColor(color);
                }

                int ID = users.getJSONObject(position).getInt("id");

                if(esta_marcado(ID)){
                    radio.setChecked(true);
                    radio.setEnabled(false);
                }else{
                    radio.setTag(position);
                    radio.setOnCheckedChangeListener(ctt);
                }

                //Log.i("tadradio", "" + (int) radio.getTag());

                nombre.setText(users.getJSONObject(position).getString("name"));


                JSONArray mesas = users.getJSONObject(position).getJSONArray("tables");
                for (int i = 0; i < mesas.length(); i++) {
                    String smesa = mesas.getJSONObject(i).getString("name").replace("Mesa ", "");
                    smesas += smesa + (i == mesas.length() - 1 ? "" : ", ");
                }

                if (users.getJSONObject(position).has("document") && users.getJSONObject(position).getString("document") != null) {
                    scedula = users.getJSONObject(position).getString("document");
                }
                if (scedula.equals("null")) {
                    scedula = " --- ";
                }

                String textmensaje = "<span><b>C.C</b> " + scedula + " <b>Mesas: </b>" + smesas + "</span>";


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    abajo.setText(Html.fromHtml(textmensaje, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    abajo.setText(Html.fromHtml(textmensaje));
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            return V;
        }


        @Override
        public int getCount() {
            return users.length();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            try {
                JSONObject JO = users.getJSONObject(position);
                return JO.getInt("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }


    }


}
