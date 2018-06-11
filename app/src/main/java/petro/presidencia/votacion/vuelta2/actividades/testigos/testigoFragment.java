package petro.presidencia.votacion.vuelta2.actividades.testigos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.widget.ListView;


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

            //startActivity(new Intent(getActivity(),anomaliasActivity.class));
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
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, Menu.NONE, "Salir");
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
}
