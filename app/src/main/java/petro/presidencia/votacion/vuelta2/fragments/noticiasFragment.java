package petro.presidencia.votacion.vuelta2.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import petro.presidencia.votacion.vuelta2.actividades.notiActivity;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;


public class noticiasFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {


    public noticiasFragment() {
    }

    JSONArray noticias;
    ListView listv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.activity_noticias, container, false);

        listv = V.findViewById(R.id.listado);

        getActivity().setTitle("Noticias");

        String URL = getResources().getString(R.string.SERVER) + "/api/news/";
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
                return params;
            }
        };

        Peticiones.hacerPeticion(getActivity().getBaseContext(), JOA);

        return V;
    }




    void cargar_datos(){
        noticiasAdapter adap = new noticiasAdapter(getActivity().getBaseContext());
        listv.setAdapter(adap);

        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Intent i = new Intent(getActivity().getBaseContext(),notiActivity.class);
                    i.putExtra("noticia",noticias.getJSONObject(position).toString());

                    startActivity(i);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onResponse(JSONObject response) {
        try {

            noticias= response.getJSONArray("news");
            cargar_datos();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    class noticiasAdapter extends BaseAdapter {

        Context na;

        public noticiasAdapter(Context noacty){
            this.na=noacty;

        }

        @Override
        public int getCount() {
            return noticias.length();
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
            View V = LayoutInflater.from(na).inflate(android.R.layout.simple_list_item_2, parent, false);
            try{
                ((TextView)V.findViewById(android.R.id.text1)).setText(noticias.getJSONObject(position).getString("title"));
                ((TextView)V.findViewById(android.R.id.text1)).setTypeface(null, Typeface.BOLD);
                ((TextView)V.findViewById(android.R.id.text2)).setText(noticias.getJSONObject(position).getString("updated_at"));

            }catch (Exception e){
                e.printStackTrace();
            }

            return V;
        }
    }
}
