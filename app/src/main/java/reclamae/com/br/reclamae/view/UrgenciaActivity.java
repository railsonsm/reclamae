package reclamae.com.br.reclamae.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.model.Urgencia;
import reclamae.com.br.reclamae.util.AdapterNumerosUrgencia;

public class UrgenciaActivity extends AppCompatActivity {

    TextView telefone;
    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgencia);

        btnVoltar = (Button)findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(voltarMenu);
        List<Urgencia> numeros = numerosDeUrgencia();
        final ListView numerosUrgencia = (ListView) findViewById(R.id.listaUrgencia);

        AdapterNumerosUrgencia adapter = new AdapterNumerosUrgencia(numeros, this);

        numerosUrgencia.setAdapter(adapter);

    }


    View.OnClickListener voltarMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UrgenciaActivity.this, MenusActivity.class);
            startActivity(intent);
            finish();
        }
    };

    public void clickEvent(View v) {
        telefone = (TextView) v.findViewById(R.id.urgencia_numero);
        String numero = telefone.getText().toString();
        Uri uri = Uri.parse("tel:"+numero);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (ActivityCompat.checkSelfPermission(UrgenciaActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UrgenciaActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        startActivity(intent);

    }

    private List<Urgencia> numerosDeUrgencia(){
        return new ArrayList<>(Arrays.asList
                (
                        new Urgencia("POLÍCIA MILITAR", "190"),
                        new Urgencia("DEFESA CIVIL", "199"),
                        new Urgencia("EMBASA", "0800555195"),
                        new Urgencia("CORPO DE BOMBEIROS", "193"),
                        new Urgencia("POLÍCIA CIVIL", "197"),
                        new Urgencia("PROCON", "33214228"),
                        new Urgencia("SAMU", "192"),
                        new Urgencia("TRANSALVADOR", "0800710880")
                ));
    }
}
