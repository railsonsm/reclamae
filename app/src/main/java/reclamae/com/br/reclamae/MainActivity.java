package reclamae.com.br.reclamae;

<<<<<<< HEAD
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

=======
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

>>>>>>> 89fc15a20346e0c6792d35cd33b09392962ed4d3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD

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
=======
>>>>>>> 89fc15a20346e0c6792d35cd33b09392962ed4d3
    }
}
