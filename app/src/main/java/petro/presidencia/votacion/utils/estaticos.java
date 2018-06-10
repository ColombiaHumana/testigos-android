package petro.presidencia.votacion.utils;

import android.content.SharedPreferences;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by mike on 5/26/18.
 */

public class estaticos {
    public static String cedula;
    public static String departamento;
    public static String municipio;
    public static String puesto;


    public static String TOKEN;
    public static String USER;

    public static SharedPreferences.Editor editor;
    public static SharedPreferences prefs;
    public static String MY_PREFS_NAME = "login";

    public static FirebaseAnalytics fanaly;
}
