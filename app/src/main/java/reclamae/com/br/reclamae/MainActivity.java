package reclamae.com.br.reclamae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnEntrar;
    EditText email;
    EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.txtEmail);
        senha = (EditText)findViewById(R.id.txtSenha);
        btnEntrar = (Button)findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(email.getText().toString().equals("admin@admin") &&
                        senha.getText().toString().equals("123")){

                    Intent intent = new Intent(MainActivity.this, MenusActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "Email e/ou Senha inválido(s)", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
