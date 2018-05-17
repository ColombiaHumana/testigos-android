package petro.presidencia.votacion.subactividades;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import petro.presidencia.votacion.menuActivity;
import petro.presidencia.votacion.subactividades.fragmentos.mesaFragment;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class votacionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    JSONArray Jmesasvotadas;

    public final static String mesasvotadasString = "mesasvotadas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        setTitle("Registro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);
    }

    boolean esmesavotada(int ID) {
        try {
            for (int i = 0; i < Jmesasvotadas.length(); i++) {
                if (Jmesasvotadas.getInt(i) == ID) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    int NUMERO_MESA=0;

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());


        try {
            String user = menuActivity.prefs.getString("user", "");
            Log.i("user-pref", user);
            if (menuActivity.prefs.contains(votacionActivity.mesasvotadasString)) {
                Log.i("votacionString",menuActivity.prefs.getString(votacionActivity.mesasvotadasString,"---"));
                Jmesasvotadas = new JSONArray(menuActivity.prefs.getString(votacionActivity.mesasvotadasString,""));
            }

            JSONArray JARRAY = new JSONObject(user).getJSONObject("user").getJSONArray("tables");
            for (int i = 0; i < JARRAY.length(); i++) {


                JSONObject JO = JARRAY.getJSONObject(i);
                int ID = JO.getInt("id");
                String mesanombre = JO.getString("name");

                if (esmesavotada(ID)) {
                    continue;
                }


                mesaFragment AF = mesaFragment.getMesaFragment(ID);

                NUMERO_MESA+=1;
                adapter.addFragment(AF, mesanombre);
            }

            if(NUMERO_MESA>3){
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }else{
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            }

        } catch (Exception e) {
            Toasty.error(this, "Error: Deslogueate y vuelvete a loguear en la aplicacion", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.getItem(viewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }
}
