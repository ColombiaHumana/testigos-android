package petro.presidencia.votacion.subactividades;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Scroller;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import votacion.presidencia.petro.testigoscolombiahumana.R;

public class guiaActivity extends AppCompatActivity {


    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(this, "Guias", null /* class override */);
        setTitle("Guías");
    }


    public void dialogo(View view) {
        int id = view.getId();
        String mensaje="No enviado";
        String titulo="No enviado";
        switch (id){
            case R.id.recomendaciones:
                titulo="10  recomendaciones para ser un buen testigo electoral";
                mensaje="1. Llega a tiempo con tu credencial electoral, formatos y esfero\n\n" +
                        "2. Verifica que los jurados de las mesas correspondan a los asignados por la Registraduría \n\n" +
                        "3. Verifica que esté completo el Kit electoral\n\n" +
                        "4. Verifica las urnas con los jurados antes de la apertura del proceso de votación\n\n" +
                        "5. Verifica que se destruye el material electoral sobrante después del cierre de votaciones\n\n" +
                        "6. Apunta en la App el número de sufragantes de tu mesa\n\n" +
                        "7. Vigila la apertura de la urna y el conteo de tarjetas electorales\n\n" +
                        "8. Se muy cuidadoso en verificar la nivelación de mesa\n\n" +
                        "9. Debes estar presente en el conteo de votos y digitar los resultados en la APP\n\n" +
                        "10. Haz las Reclamaciones por escrito en los formatos suministrados por la campaña";
                break;
            case R.id.reportarresultados:
                titulo="Cómo reportar los resultados de las votaciones:";
                mensaje="1. Abre la App y busca la sección: Resultados de las votaciones\n\n" +
                        "2. En la casilla “Total votos en la mesa” digita el número de votos en la mesa después de la nivelación\n\n" +
                        "3. Digita el número de votos que obtuvo cada candidato en la casilla frente a cada nombre\n\n" +
                        "4. Digita el número de votos en blanco, nulos y no marcados en las casillas correspondientes\n\n" +
                        "5. Si las cifras no corresponden la App emite una alerta de error. \n\n" +
                        "6. En ese caso verifica con los jurados las cifras o solicita un reconteo de los votos\n\n" +
                        "7. Envía el reporte\n\n" +
                        "8. Abre la cámara a través de la App y toma una foto del formulario E14 utilizando las guías en pantalla\n\n" +
                        "9. Trata de tomar en la foto los tres formularios E14 (Claveros, transmisión y testigos)\n\n" +
                        "10. Envía la fotografía";
                break;
            case R.id.reportaranomalia:
                titulo="Cómo reportar anomalías ";
                mensaje="1. Abre la App y busca la sección: “Reporte de anomalías durante las votaciones”\n" +
                        "\n" +
                        "2. Identifica la Mesa en la que está ocurriendo la anomalía electoral pulsando sobre el número de alguna de las mesas que te fueron asignadas\n" +
                        "\n" +
                        "3. Pulsa sobre la anomalía electoral detectada\n" +
                        "\n" +
                        "4. La aplicación te describe la anomalía y te pregunta si estás seguro que la deseas reportar\n" +
                        "\n" +
                        "5.  Si estás seguro presiona enviar\n" +
                        "\n" +
                        "Recuerda que una anomalía es una situación irregular que puede afectar el normal desarrollo de las votaciones o sus resultados\n" +
                        "\n" +
                        "Las anomalías detectadas también se deben reportar por escrito al delegado de la Registraduría y a los observadores electorales";
                break;
        }



        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        //textView.setMaxLines();
        textView.setScroller(new Scroller(this));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
}
