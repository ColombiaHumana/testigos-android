package petro.presidencia.votacion.subactividades.fragmentos;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import org.json.JSONObject;

import petro.presidencia.votacion.vuelta2.actividades.testigos.anomaliasActivity;
import votacion.presidencia.petro.testigoscolombiahumana.R;

import static petro.presidencia.votacion.menuActivity.editor;
import static petro.presidencia.votacion.menuActivity.prefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class anomaliaFragment extends Fragment {


    public anomaliaFragment() {
    }

    RadioButton jurados_votacion, limitaciones_al_derecho, censo_extracontemporaneo;
    RadioButton no_destruccion, errores_en_el_conteo, errores_e14;

    public static anomaliaFragment getAnomaliaFragment(int id) {
        anomaliaFragment f = new anomaliaFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);
        return f;
    }

    public int ID;
    anomaliasActivity DA;
    JSONObject JOcampos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DA = (anomaliasActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        guardar_campos_enjson();
    }

    void guardar_campos_enjson(){
        try{
            JOcampos.put("jurados_votacion",jurados_votacion.isChecked());
            JOcampos.put("limitaciones_al_derecho",limitaciones_al_derecho.isChecked());
            JOcampos.put("censo_extracontemporaneo",censo_extracontemporaneo.isChecked());
            JOcampos.put("no_destruccion",no_destruccion.isChecked());
            JOcampos.put("errores_en_el_conteo",errores_en_el_conteo.isChecked());
            JOcampos.put("errores_e14",errores_e14.isChecked());
            editor.putString("anomalia-"+ID,JOcampos.toString());
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void cargar_campos_dejson(){
        try{
        JOcampos = new JSONObject(prefs.getString("anomalia-"+ID,""));

            jurados_votacion.setChecked(JOcampos.getBoolean("jurados_votacion"));
            limitaciones_al_derecho.setChecked(JOcampos.getBoolean("limitaciones_al_derecho"));
            censo_extracontemporaneo.setChecked(JOcampos.getBoolean("censo_extracontemporaneo"));
            no_destruccion.setChecked(JOcampos.getBoolean("no_destruccion"));
            errores_en_el_conteo.setChecked(JOcampos.getBoolean("errores_en_el_conteo"));
            errores_e14.setChecked(JOcampos.getBoolean("errores_e14"));



            jurados_votacion.setEnabled(!JOcampos.getBoolean("jurados_votacion"));
                    limitaciones_al_derecho.setEnabled(!JOcampos.getBoolean("limitaciones_al_derecho"));
            censo_extracontemporaneo.setEnabled(!JOcampos.getBoolean("censo_extracontemporaneo"));
                    no_destruccion.setEnabled(!JOcampos.getBoolean("no_destruccion"));
            errores_en_el_conteo.setEnabled(!JOcampos.getBoolean("errores_en_el_conteo"));
                    errores_e14.setEnabled(!JOcampos.getBoolean("errores_e14"));







        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_anomalias, container, false);
        Bundle args = getArguments();
        this.ID = args.getInt("id", 0);

        jurados_votacion = (RadioButton) V.findViewById(R.id.jurados_votacion);
        limitaciones_al_derecho = (RadioButton) V.findViewById(R.id.limitaciones_al_derecho);
        censo_extracontemporaneo = (RadioButton) V.findViewById(R.id.censo_extracontemporaneo);
        no_destruccion = (RadioButton) V.findViewById(R.id.no_destruccion);
        errores_en_el_conteo = (RadioButton) V.findViewById(R.id.errores_en_el_conteo);
        errores_e14 = (RadioButton) V.findViewById(R.id.errores_e14);

        if(prefs.contains("anomalia-"+ID)){
        cargar_campos_dejson();
        }else{
            JOcampos = new JSONObject();
        }

        return V;
    }


}
