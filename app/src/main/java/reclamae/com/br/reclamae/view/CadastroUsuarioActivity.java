package reclamae.com.br.reclamae.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

     private boolean validaCampos(){
        boolean res = false;

        String nome = txtNome.getText().toString();
        String email = txtEmail.getText().toString();
        String sobrenome = txtSobrenome.getText().toString();
        String senha = txtSenha.getText().toString();
        String mensagem = "";

        if(res = isCampoVazio(nome)){
            txtNome.requestFocus();
            res = false;
            mensagem("Campo nome é obrigatório");
        }else if(res =isQtdDigitos(nome)){
            res = false;
            mensagem("Campo nome deve ter mais de 3 caracteres");
         }else if(res = isCampoVazio(sobrenome)){
            txtSobrenome.requestFocus();
            res = false;
            mensagem("Campo sobrenome é obrigatório");
         }else if(res =isQtdDigitos(sobrenome)){
            res = false;
            mensagem("Campo sobrenome deve ter mais de 3 caracteres");
        }else if(res= !isEmailValido(email)){
            txtEmail.requestFocus();
            res = false;
            mensagem("Email inválido");
        }else if(res = isCampoVazio(senha)){
            txtSenha.requestFocus();
            res = false;
            mensagem("Campo senha é obrigatório");
        }else if(res =isQtdDigitos(senha)){
            res = false;
            mensagem("Campo senha deve ter mais de 3 caracteres");
        }else{
            res=true;
        }
        return res;
     }

     private void mensagem(String mensagem){
         AlertDialog.Builder dlg = new AlertDialog.Builder(this);
         dlg.setTitle("Aviso!");
         dlg.setMessage(mensagem);
         dlg.setNeutralButton("OK", null);
         dlg.show();
     }

     private boolean isCampoVazio(String valor){
       boolean resultado = (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
       return resultado;
     }

     private boolean isQtdDigitos(String valor){
         boolean resultado = (valor.length()< 4);
         return resultado;
     }

     private boolean isEmailValido(String email){
         boolean resultado = (!TextUtils.isEmpty(email)  && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        return  resultado;
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
            if(validaCampos()){
              cadastrarUsuario();
            };
           // validaCampos();
            return;
        }
    };

    private void cadastrarUsuario(){
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
}
