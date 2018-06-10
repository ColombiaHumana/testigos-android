package petro.presidencia.votacion.vuelta2.actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import petro.presidencia.votacion.menuActivity;
import petro.presidencia.votacion.utils.Peticiones;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class noticiasActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {


    JSONArray noticias;
    ListView listv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);
        listv = findViewById(R.id.listado);

        setTitle("Noticias");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
                params.put("Authorization", menuActivity.token);
                return params;
            }
        };


        Peticiones.hacerPeticion(this, JOA);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);

    }

    void cargar_datos(){
        noticiasAdapter adap = new noticiasAdapter(this);
        listv.setAdapter(adap);

        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Intent i = new Intent(getApplicationContext(),notiActivity.class);
                    i.putExtra("noticia",noticias.getJSONObject(position).toString());

                    startActivity(i);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

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


    class noticiasAdapter extends BaseAdapter{

        noticiasActivity na;

        public noticiasAdapter(noticiasActivity noacty){
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
                ((TextView)V.findViewById(android.R.id.text2)).setText(noticias.getJSONObject(position).getString("updated_at"));

            }catch (Exception e){
                e.printStackTrace();
            }

            return V;
        }
    }

}
