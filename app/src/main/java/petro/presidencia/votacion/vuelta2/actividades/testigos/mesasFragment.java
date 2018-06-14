package petro.presidencia.votacion.vuelta2.actividades.testigos;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import votacion.presidencia.petro.testigoscolombiahumana.R;

import static petro.presidencia.votacion.utils.estaticos.editor;

/**
 * A simple {@link Fragment} subclass.
 */
public class mesasFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {



    public mesasFragment() {

    }
    ListView vlist;
    JSONArray mesas_disponibles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.layout_listado, container, false);
        vlist = (ListView)V.findViewById(R.id.listadop);
        peticion_mesas_disponibles();
        return V;
    }

    void peticion_mesas_disponibles(){
        String URL = getResources().getString(R.string.SERVER) + "/api/tables/";

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
                params.put("Authorization", estaticos.TOKEN);
                return params;
            }
        };

        Peticiones.hacerPeticion(getActivity().getBaseContext(), JOA);
    }

    @Override
    public void onResponse(JSONObject response) {
        try{
            mesas_disponibles = response.getJSONArray("mesas");

            mesadisponibleAdapter mda=new mesadisponibleAdapter(getActivity().getBaseContext());
            vlist.setAdapter(mda);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        if (error instanceof AuthFailureError) {
            editor.clear().apply();
            getActivity().finish();
            Toasty.info(getActivity().getBaseContext(),"Vuelve a abrir la aplicación", Toast.LENGTH_LONG).show();
        }else{
            Toasty.info(getActivity().getBaseContext(),"Verifica tu conexión a internet",Toast.LENGTH_LONG).show();
        }
    }

    class mesadisponibleAdapter extends BaseAdapter{

        Context ctt;
        public mesadisponibleAdapter(Context ct){
            this.ctt=ct;
        }


        @Override
        public int getCount() {
            return mesas_disponibles.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ctt).
                        inflate(R.layout.item_mesa_disponible, parent, false);
            }
            TextView tx =(TextView)convertView.findViewById(R.id.nombre_mesa);
            Button registrar_voto=(Button) convertView.findViewById(R.id.btn_enviar_resultados);
            Button anomalias= (Button)convertView.findViewById(R.id.btn_enviar_anomalia);
            try{
                final String nombre = mesas_disponibles.getJSONObject(position).getString("nombre");
                final int id = mesas_disponibles.getJSONObject(position).getInt("id");

                tx.setText(nombre);

                registrar_voto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i =new Intent(getActivity(),votacionActivity.class);
                        i.putExtra("id",id);
                        i.putExtra("name",nombre);
                        startActivity(i);
                    }
                });

                anomalias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i =new Intent(getActivity(),anomaliasActivity.class);
                        i.putExtra("id",id);
                        i.putExtra("name",nombre);
                        startActivity(i);
                    }
                });




            }catch (Exception e){
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
