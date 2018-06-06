package petro.presidencia.votacion.subactividades.noti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import org.json.JSONObject;

import votacion.presidencia.petro.testigoscolombiahumana.R;

public class notiActivity extends AppCompatActivity {


    WebView WB;
    JSONObject JO;
    String titulo,contenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);
        try{
            JO  = new JSONObject(getIntent().getStringExtra("noticia"));
            titulo=JO.getString("title");
            contenido = JO.getString("content");
        }catch (Exception e){
            e.printStackTrace();
        }

        setTitle(titulo);
        WB= (WebView)findViewById(R.id.web);
        WB.getSettings().setJavaScriptEnabled(true);
        WB.loadDataWithBaseURL("", contenido, "text/html", "UTF-8", "");

    }


}
