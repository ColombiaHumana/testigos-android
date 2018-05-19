package petro.presidencia.votacion.subactividades;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import votacion.presidencia.petro.testigoscolombiahumana.R;

import static petro.presidencia.votacion.menuActivity.prefs;

public class asistenciaActivity extends AppCompatActivity {


    JSONArray users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        setTitle("Asistencia");

        try{

            users = new JSONObject(prefs.getString("user","")).getJSONObject("user").getJSONArray("users");
            Log.i("users",users.toString());
        }catch (Exception e){
            e.printStackTrace();
            finish();
        }

    }




    class usuariosAdapter extends BaseAdapter{

        Context ctt;

        public usuariosAdapter(Context CTT){
            ctt=CTT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View V=LayoutInflater.from(ctt).inflate(R.layout.item_asistente, parent, false);

            TextView nombre = (TextView)V.findViewById(R.id.iarriba);
            TextView abajo = (TextView)V.findViewById(R.id.iabajo);

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
            try{
                JSONObject JO =users.getJSONObject(position);
                return JO.getInt("id");
            }catch (Exception e){
                e.printStackTrace();
            }
            return 0;
        }


    }






}
