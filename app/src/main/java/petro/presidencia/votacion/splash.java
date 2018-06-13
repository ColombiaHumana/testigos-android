package petro.presidencia.votacion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.security.Principal;

import petro.presidencia.votacion.vuelta2.principalActivity;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, principalActivity.class);
        startActivity(intent);
        finish();
    }
}
