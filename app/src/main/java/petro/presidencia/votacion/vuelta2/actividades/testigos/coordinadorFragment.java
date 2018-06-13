package petro.presidencia.votacion.vuelta2.actividades.testigos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import petro.presidencia.votacion.R;
import votacion.presidencia.petro.testigoscolombiahumana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class coordinadorFragment extends Fragment {


    public coordinadorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coordinador, container, false);
    }

}
