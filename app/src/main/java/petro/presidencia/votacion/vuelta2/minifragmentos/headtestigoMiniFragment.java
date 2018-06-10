package petro.presidencia.votacion.vuelta2.minifragmentos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import votacion.presidencia.petro.testigoscolombiahumana.R;

public class headtestigoMiniFragment extends Fragment {

    TextView nombre, puesto;
    Button reporar_anomalia,llamar_coordinador;

    public headtestigoMiniFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_headtestigo_mini, container, false);
        nombre = (TextView)V.findViewById(R.id.h_nombre);
        puesto = (TextView)V.findViewById(R.id.h_puesto);
        reporar_anomalia = (Button)V.findViewById(R.id.reportar_anomalia);
        return V;
    }




}
