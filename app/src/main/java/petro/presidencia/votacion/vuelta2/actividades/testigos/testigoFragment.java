package petro.presidencia.votacion.vuelta2.actividades.testigos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import petro.presidencia.votacion.utils.estaticos;
import petro.presidencia.votacion.vuelta2.fragments.minifragmentos.headtestigoMiniFragment;
import votacion.presidencia.petro.testigoscolombiahumana.R;


public class testigoFragment extends Fragment {
    private static final int MENU_ITEM_LOGOUT = 1;

    public testigoFragment() {

    }


    private View mProgressView;
    private View mLoginFormView;

    ListView listado_mesas_asignadas;

    JSONObject dataUser;
    JSONArray mesas;

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_testigo, container, false);

        mLoginFormView = V.findViewById(R.id.content);
        mProgressView = V.findViewById(R.id.progress);
        listado_mesas_asignadas = V.findViewById(R.id.listado_mesas_asignadas);

        showProgress(true);
        cargar_data();
        //startActivity(new Intent(getActivity(),votacionActivity.class));
        return V;
    }

    void cargar_data() {
        try {
            if (estaticos.prefs.contains("user")) {
                String USER = estaticos.prefs.getString("user", "");
                Log.i("testigoF", "directo: " + USER);
                dataUser= new JSONObject(USER);
                llenar_contenido();
            } else {
                listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        if (!key.equals("user")) {
                            String USER = estaticos.prefs.getString("user", "");
                            Log.i("testigoF", "Listener: " + USER);
                            cargar_data();
                            //llenar_contenido();
                        }
                    }
                };
                estaticos.prefs.registerOnSharedPreferenceChangeListener(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void llenar_contenido() {
        showProgress(false);
        try{
            //dataUser = dataUser.getJSONObject("user");
            String nombre = dataUser.getString("name");
            String puesto = dataUser.getJSONObject("post").getString("name");
            String celular_coor = dataUser.getBoolean("coordinator")?null:
                    dataUser.getJSONObject("post")
                    .getJSONObject("coordinator")
                    .getString("phone");


            Fragment f = headtestigoMiniFragment.headtestiBuilder(nombre,puesto,celular_coor);
            getFragmentManager().beginTransaction().replace(R.id.head_fragment, f).commit();

            mesas = dataUser.getJSONArray("tables");
            llenar_mesas_asignadas();
            //startActivity(new Intent(getActivity(),votacionActivity.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Testigos");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, Menu.NONE, "Cerrar sesión");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_LOGOUT:
                estaticos.editor.clear().apply();
                getActivity().finish();
                return true;

            default:
                return false;
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


    void llenar_mesas_asignadas(){
        BaseAdapter ba=new mesaAdapter(getActivity().getBaseContext());
        listado_mesas_asignadas.setAdapter(ba);
        //listado_mesas_asignadas.divider
    }

    class mesaAdapter extends BaseAdapter{

        Context ctt;

        public mesaAdapter(Context ct){
            this.ctt=ct;
        }


        @Override
        public int getCount() {
            return mesas.length();
        }

        @Override
        public Object getItem(int position) {
            try{
                return mesas.getJSONObject(position);
            }catch (Exception e){
                e.printStackTrace();
            }
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ctt).
                        inflate(R.layout.item_mesa, parent, false);
            }

            try{
                TextView mesaName = (TextView)convertView.findViewById(R.id.nombre_mesa);
                Button ranomalia = (Button)convertView.findViewById(R.id.btn_enviar_anomalia);
                Button rresultados = (Button)convertView.findViewById(R.id.btn_enviar_resultados);
                final int id =mesas.getJSONObject(position).getInt("id");
                final String nombre = mesas.getJSONObject(position).getString("name");
                mesaName.setText(nombre);

                rresultados.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i =new Intent(getActivity(),votacionActivity.class);
                        i.putExtra("id",id);
                        i.putExtra("name",nombre);
                        startActivity(i);
                    }
                });

                ranomalia.setOnClickListener(new View.OnClickListener() {
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
