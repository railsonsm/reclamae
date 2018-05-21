package reclamae.com.br.reclamae.view;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.dao.UsuarioDao;
import reclamae.com.br.reclamae.model.Reclamacao;
import reclamae.com.br.reclamae.model.Usuario;

public class MainActivity extends AppCompatActivity {
    Button btnCadastrar;
    Button btnEntrar;
    EditText email;
    EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            email = (EditText)findViewById(R.id.txtEmail);
            senha = (EditText)findViewById(R.id.txtSenha);
            btnEntrar = (Button)findViewById(R.id.btnEntrar);
            btnCadastrar = (Button)findViewById(R.id.btnCadastrar);


            btnCadastrar.setOnClickListener(cadastrarUsuario);

            btnEntrar.setOnClickListener(entrar);
        }catch (NullPointerException e){
            throw e;
        }

    }
    View.OnClickListener entrar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Usuario usuario = logar(email.getText().toString());
            if(email.getText().toString().equals(usuario.getEmail()) &&
                    senha.getText().toString().equals(usuario.getSenha())){

                Intent intent = new Intent(MainActivity.this, MenusActivity.class);
                intent.putExtra("nome", usuario.getNome().toString());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Email e/ou Senha inválido(s)", Toast.LENGTH_SHORT).show();
            }
        }
    };

    View.OnClickListener cadastrarUsuario = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, CadastroUsuarioActivity.class);
            startActivity(intent);
        }
    };

    public Usuario logar(String email){
        UsuarioDao dao = new UsuarioDao(MainActivity.this);
        Usuario usuario = dao.buscaUsuario(email);
        if (usuario.getEmail() == null){
            Toast.makeText(this, "E-mail não cadastrado", Toast.LENGTH_SHORT).show();
        }
        return usuario;
    }

}
