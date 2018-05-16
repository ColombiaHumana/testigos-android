package petro.presidencia.votacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import petro.presidencia.votacion.subactividades.anomaliasActivity;
import petro.presidencia.votacion.subactividades.guiaActivity;
import petro.presidencia.votacion.subactividades.votacionActivity;
import votacion.presidencia.petro.testigoscolombiahumana.R;

public class menuActivity extends AppCompatActivity {

    ImageView guias,anomalias,registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        guias=(ImageView)findViewById(R.id.guias);
        anomalias=(ImageView)findViewById(R.id.anomalias);
        registro=(ImageView)findViewById(R.id.registro);

        guias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),guiaActivity.class));
            }
        });

        anomalias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),anomaliasActivity.class));
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),votacionActivity.class));
            }
        });
    }

}
