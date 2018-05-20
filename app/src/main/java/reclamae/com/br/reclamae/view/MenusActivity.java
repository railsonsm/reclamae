package reclamae.com.br.reclamae.view;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Usuario usuario = new Usuario();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        btnMapa  = (Button) findViewById(R.id.verReclame);
        btnMapa.setOnClickListener(verReclamacoes);
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
