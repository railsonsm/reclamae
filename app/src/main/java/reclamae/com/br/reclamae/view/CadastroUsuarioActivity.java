package reclamae.com.br.reclamae.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.dao.UsuarioDao;
import reclamae.com.br.reclamae.model.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private Button btnVoltar;
    private Button btnCadastrar;
    private EditText txtNome;
    private EditText txtSobrenome;
    private EditText txtEmail;
    private EditText txtSenha;
    Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        txtNome = (EditText) findViewById(R.id.txtNome);
        txtSobrenome = (EditText) findViewById(R.id.txtSobrenome);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

        btnVoltar.setOnClickListener(voltarMenu);
        btnCadastrar.setOnClickListener(cadastrarUsuario);
     }

    View.OnClickListener voltarMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cadastrarUsuario = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                usuario.setNome(txtNome.getText().toString());
                usuario.setSobrenome(txtSobrenome.getText().toString());
                usuario.setEmail(txtEmail.getText().toString());
                usuario.setSenha(txtSenha.getText().toString());
                UsuarioDao dao = new UsuarioDao(CadastroUsuarioActivity.this);
                dao.salvar(usuario);
                usuario = new Usuario();
                Toast.makeText(CadastroUsuarioActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
