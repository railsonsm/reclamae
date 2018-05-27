package reclamae.com.br.reclamae.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.UsuarioDao;
import reclamae.com.br.reclamae.model.Usuario;

public class MenusActivity extends AppCompatActivity {

    Button btnReclamar;
    Button btnMapa;
    String nomeUsuario;
    Button btnSugerir;
    Button btnGrafico, btnSair;
    private  static final String COMPARILHADO = "Compartilhado";


    Usuario usuario = new Usuario();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        usuario = new Usuario();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);

        Intent intent = getIntent();

        SharedPreferences nome = getSharedPreferences(COMPARILHADO, MODE_PRIVATE);
        nomeUsuario = nome.getString("nome", "oi");
        Log.i("menuoncreate", nomeUsuario );



        btnMapa  = (Button) findViewById(R.id.verReclame);
        btnSugerir = (Button)  findViewById(R.id.sugira);
        btnReclamar  = (Button) findViewById(R.id.btnReclame);
        btnGrafico  = (Button) findViewById(R.id.verGraficos);
        btnSair = (Button) findViewById(R.id.sair);

        btnMapa.setOnClickListener(verReclamacoes);
        btnReclamar.setOnClickListener(reclamar);
        btnSugerir.setOnClickListener(sugerir);
        btnGrafico.setOnClickListener(graficos);
        btnSair.setOnClickListener(sair);
    }


    View.OnClickListener verReclamacoes = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenusActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener reclamar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenusActivity.this, ReclamarActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener sugerir = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenusActivity.this, SugestaoActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener graficos = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MenusActivity.this, GraficoActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener sair = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            finish();
        }
    };

}
