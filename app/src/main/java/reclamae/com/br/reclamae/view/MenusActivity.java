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
        btnReclamar  = (Button) findViewById(R.id.btnReclame);

        btnMapa.setOnClickListener(verReclamacoes);
        btnReclamar.setOnClickListener(reclamar);
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

}
