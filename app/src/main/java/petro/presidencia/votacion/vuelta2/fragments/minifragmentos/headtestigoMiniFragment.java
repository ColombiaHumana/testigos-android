package petro.presidencia.votacion.vuelta2.fragments.minifragmentos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import votacion.presidencia.petro.testigoscolombiahumana.R;

public class headtestigoMiniFragment extends Fragment {

    TextView nombre, puesto;
    Button llamar_coordinador;

    public headtestigoMiniFragment() {
    }

    public static headtestigoMiniFragment headtestiBuilder(String nombre, String puesto,@Nullable String telcoordinador){
        headtestigoMiniFragment fragment = new headtestigoMiniFragment();
        Bundle bundle = new Bundle();
        bundle.putString("nombre", nombre);
        bundle.putString("puesto", puesto);
        bundle.putString("telcoor", telcoordinador);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V=inflater.inflate(R.layout.fragment_headtestigo_mini, container, false);

        nombre = (TextView)V.findViewById(R.id.h_nombre);
        puesto = (TextView)V.findViewById(R.id.h_puesto);
        llamar_coordinador = (Button)V.findViewById(R.id.boton_head_llamar_coordinador);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            nombre.setText(bundle.getString("nombre"));
            puesto.setText(bundle.getString("puesto"));
            if(bundle.getString("telcoor")==null){
                llamar_coordinador.setVisibility(View.GONE);
            }else{
                llamar_coordinador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+bundle.getString("telcoor")));
                        startActivity(intent);
                    }
                });
            }
        }
        return V;
    }




}
