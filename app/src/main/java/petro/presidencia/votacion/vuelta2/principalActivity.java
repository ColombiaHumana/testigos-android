package petro.presidencia.votacion.vuelta2;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.utils.Peticiones;
import petro.presidencia.votacion.utils.estaticos;
import petro.presidencia.votacion.vuelta2.actividades.testigos.coordinadorFragment;
import petro.presidencia.votacion.vuelta2.actividades.testigos.testigoFragment;
import petro.presidencia.votacion.vuelta2.fragments.loginFragment;
import petro.presidencia.votacion.vuelta2.fragments.noticiasFragment;
import votacion.presidencia.petro.testigoscolombiahumana.R;


import static petro.presidencia.votacion.utils.estaticos.MY_PREFS_NAME;
import static petro.presidencia.votacion.utils.estaticos.editor;
import static petro.presidencia.votacion.utils.estaticos.prefs;

public class principalActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    DatabaseReference mDatabase;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    denuncuasAdapter adapter;

    PackageInfo pInfo;
    private Fragment ftestigos;

    ProgressBar progressBar;
    boolean LOGUEADO=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        progressBar = (ProgressBar)findViewById(R.id.tab_progress);
        progressBar.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        estaticos.fanaly = FirebaseAnalytics.getInstance(this);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        FirebaseMessaging.getInstance().subscribeToTopic("todos");
        estaticos.prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        estaticos.editor = estaticos.prefs.edit();

        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(mFirebaseRemoteConfig!=null){
            mFirebaseRemoteConfig.fetch()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFirebaseRemoteConfig.activateFetched();
                                long ultimaV = Long.parseLong(mFirebaseRemoteConfig.getString("androidAPK"));
                                Log.i("principal","Versionnube:"+ultimaV + " - Version actual:"+pInfo.versionCode);
                                if(pInfo.versionCode<ultimaV){
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=petro.presidencia.votacion")));
                                    Toasty.info(getApplicationContext(),"Hay una versión mas reciente de la aplicación. ACTUALIZA LA APLICACIÓN",Toast.LENGTH_LONG).show();
                                    estaticos.editor.clear().apply();
                                    finish();
                                }
                            }
                        }
                    });
        }


        if(estaticos.prefs.contains("token")){

            estaticos.TOKEN = estaticos.prefs.getString("token","");
            try{
                onResponse(new JSONObject(estaticos.prefs.getString("response","{}")));
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            this.ftestigos = new loginFragment();
            setupViewPager();
        }


    }

    void pedir_informacion(){
        LOGUEADO=true;
        String URL = getResources().getString(R.string.SERVER) + "/api/user";

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

        Peticiones.hacerPeticion(this, JOA);
    }



    @Override
    public void onResponse(JSONObject response) {
        try{
            estaticos.USER = response.getJSONObject("user").toString();
            estaticos.cedula = String.valueOf(response.getJSONObject("user").getInt("cedula"));

            editor.putString("response",response.toString()).apply();
            editor.putString("user",estaticos.USER).apply();


            String departamento = response.getJSONObject("user").getJSONObject("department").getString("name");
            String municipio = response.getJSONObject("user").getJSONObject("municipality").getString("name");
            String puesto = response.getJSONObject("user").getJSONObject("post").getString("name");
            String escoordinador = response.getJSONObject("user").getBoolean("coordinator") ? "coordinador" : "testigo";

            estaticos.puesto=puesto;
            estaticos.departamento=departamento;
            estaticos.municipio=municipio;


            HashMap<String, Object> result = new HashMap<>();
            result.put("departamento",estaticos.departamento);
            result.put("municipio",estaticos.municipio);
            result.put("puesto",estaticos.puesto);
            mDatabase.child("/votacion").child("/"+ estaticos.cedula).child("/lugar").setValue(result);


            String sdepartamento = departamento.replace(" ", "-");
            String smunicipio = municipio.replace(" ", "-");
            String spuesto = puesto.replace(" ", "-");

            FirebaseMessaging.getInstance().subscribeToTopic(sdepartamento);
            FirebaseMessaging.getInstance().subscribeToTopic(smunicipio);
            FirebaseMessaging.getInstance().subscribeToTopic(spuesto);
            FirebaseMessaging.getInstance().subscribeToTopic(escoordinador);

            if(response.getJSONObject("user").getBoolean("coordinator")){
                //Toast.makeText(this,"no esta definido cuando es coordinador",Toast.LENGTH_SHORT).show();
                ftestigos=new coordinadorFragment();
            }else{
                ftestigos = new testigoFragment();
            }
            setupViewPager();
        }catch (Exception e){
            e.printStackTrace();
            editor.clear().apply();
            finish();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        if (error instanceof AuthFailureError) {
            editor.clear().apply();
            finish();
            Toasty.info(this,"Vuelve a abrir la aplicación",Toast.LENGTH_LONG).show();
        }else{
            Toasty.info(this,"Verifica tu conexión a internet",Toast.LENGTH_LONG).show();
        }
    }





    private void setupViewPager() {
        progressBar.setVisibility(View.GONE);
        adapter = new denuncuasAdapter(getSupportFragmentManager());

        noticiasFragment nf = new noticiasFragment();

        adapter.addFragment(nf, "Información");
        if(ftestigos instanceof coordinadorFragment){
            adapter.addFragment(this.ftestigos, "Coordinación");
        }else{
            adapter.addFragment(this.ftestigos, "Testigos");
        }


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setLOGUEADO(){
        pedir_informacion();
        adapter.mFragmentManager.beginTransaction().remove(ftestigos).commit();
        ftestigos=new testigoFragment();
        adapter.mFragmentList.set(1,ftestigos);
        adapter.notifyDataSetChanged();
    }



    class denuncuasAdapter extends FragmentPagerAdapter {
        public final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public FragmentManager mFragmentManager;

        public denuncuasAdapter(FragmentManager manager) { super(manager);
            mFragmentManager=manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof loginFragment && ftestigos instanceof testigoFragment || object instanceof loginFragment && ftestigos instanceof coordinadorFragment)
                return POSITION_NONE;
            return POSITION_UNCHANGED;
        }

        @Override
        public int getCount() { return mFragmentList.size();}

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
