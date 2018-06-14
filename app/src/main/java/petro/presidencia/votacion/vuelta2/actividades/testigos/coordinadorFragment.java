package petro.presidencia.votacion.vuelta2.actividades.testigos;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import petro.presidencia.votacion.utils.estaticos;
import petro.presidencia.votacion.vuelta2.fragments.loginFragment;
import petro.presidencia.votacion.vuelta2.fragments.minifragmentos.headtestigoMiniFragment;
import petro.presidencia.votacion.vuelta2.fragments.noticiasFragment;
import petro.presidencia.votacion.vuelta2.principalActivity;
import votacion.presidencia.petro.testigoscolombiahumana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class coordinadorFragment extends Fragment {


    public coordinadorFragment() {
        // Required empty public constructor
    }

    JSONObject dataUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    coordinadorFragmentAdapter adaptador;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coordinador, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager2);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs2);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            String USER = estaticos.prefs.getString("user", "");
            Log.i("testigoF", "directo: " + USER);
            dataUser= new JSONObject(USER);
            String nombre = dataUser.getString("name");
            String puesto = dataUser.getJSONObject("post").getString("name");
            String celular_coor = dataUser.getBoolean("coordinator")?null:
                    dataUser.getJSONObject("post")
                            .getJSONObject("coordinator")
                            .getString("phone");


            Fragment f = headtestigoMiniFragment.headtestiBuilder(nombre,puesto,celular_coor);
            getFragmentManager().beginTransaction().replace(R.id.head_fragment, f).commit();

            setupViewPager();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, 1, Menu.NONE, "Cerrar sesión");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                estaticos.editor.clear().apply();
                getActivity().finish();
                return true;

            default:
                return false;
        }
    }

    private void setupViewPager() {
        try{
            JSONArray testigos = new JSONObject(estaticos.USER).getJSONArray("users");

            adaptador = new coordinadorFragmentAdapter(getChildFragmentManager());
            adaptador.addFragment(listadotestigosFragment.listadotestigoBuilder(testigos.toString()), "Testigos");
            adaptador.addFragment(new noticiasFragment(), "Mesa");

            viewPager.setAdapter(adaptador);

            tabLayout.setupWithViewPager(viewPager);

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    class coordinadorFragmentAdapter extends FragmentPagerAdapter {
        public final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public FragmentManager mFragmentManager;

        public coordinadorFragmentAdapter(FragmentManager manager) { super(manager);
            mFragmentManager=manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
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
