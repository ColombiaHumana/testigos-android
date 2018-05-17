package petro.presidencia.votacion.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by mike on 5/17/18.
 */


public class Peticiones {

    public static RequestQueue rq;

    public static void hacerPeticion(Context ct, JsonObjectRequest peticion){
        try {
            if(rq==null){
                rq = Volley.newRequestQueue(ct);
            }

            Log.i("Peticion","URL: "+peticion.getUrl());



            rq.add(peticion);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}