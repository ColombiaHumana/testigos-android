package petro.presidencia.votacion.vuelta2.actividades.testigos;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


import petro.presidencia.votacion.utils.NonScrollListView;
import votacion.presidencia.petro.testigoscolombiahumana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class listadotestigosFragment extends Fragment {



    public static listadotestigosFragment listadotestigoBuilder(String tes) {
        listadotestigosFragment f = new listadotestigosFragment();
        Bundle b = new Bundle();
        b.putString("testigos", tes);
        f.setArguments(b);
        return f;
    }

    public listadotestigosFragment() {

    }

    NonScrollListView listview;
    JSONArray testigos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_listado, container, false);

        try {
            testigos = new JSONArray(getArguments().getString("testigos", ""));
            Log.i("testigos",testigos.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }



        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //((TextView)view.findViewById(R.id.pruebaaa)).setText("hpta funcioneeeeeeeeeeeeeeeeee");

        this.listview = (NonScrollListView)view.findViewById(R.id.listadop);
        testigosAdapter aad = new testigosAdapter(getActivity());
        this.listview.setAdapter(aad);
    }

    class testigosAdapter extends BaseAdapter {

        Context ctt;

        public testigosAdapter(Context ct) {
            this.ctt = ct;
        }

        @Override
        public int getCount() {
            return testigos.length();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ctt).
                        inflate(R.layout.item_testigo, parent, false);
            }
            //position=0;
            TextView nombre,mesa,cedula;
            Button llamar;
            nombre = (TextView)convertView.findViewById(R.id.testi_nombre);
            mesa = (TextView)convertView.findViewById(R.id.testi_nombre_mesa);
            cedula = (TextView)convertView.findViewById(R.id.testi_cedula);
            llamar = (Button)convertView.findViewById(R.id.testi_btn_call);

            try {
                JSONArray mesas = testigos.getJSONObject(position).getJSONArray("tables");
                String smesas="";
                for(int i=0;i<mesas.length();i++){
                        smesas+=mesas.getJSONObject(position).getString("name")+",";
                }
                smesas=smesas.substring(0,smesas.length()-1);
                cedula.setText(testigos.getJSONObject(position).getString("document"));
                nombre.setText(testigos.getJSONObject(position).getString("name"));
                mesa.setText(smesas);

                llamar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                            callIntent.setData(
                                    Uri.parse("tel:"+testigos.getJSONObject(position).getString("phone"))
                            );
                            startActivity(callIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
