package petro.presidencia.votacion.subactividades.fragmentos;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import petro.presidencia.votacion.subactividades.anomaliasActivity;
import votacion.presidencia.petro.testigoscolombiahumana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class anomaliaFragment extends Fragment {


    public anomaliaFragment() {
    }



    public static anomaliaFragment getAnomaliaFragment(int id){
        anomaliaFragment f = new anomaliaFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    public int ID;

    anomaliasActivity DA;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DA=(anomaliasActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V= inflater.inflate(R.layout.fragment_anomalias, container, false);
        Bundle args = getArguments();
        this.ID= args.getInt("id", 0);
        return V;
    }



}
