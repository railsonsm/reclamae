package reclamae.com.br.reclamae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CadastrarActivity extends AppCompatActivity {

    private String[] categorias = new String[]{"Problema 1", "Problema 2"};
    private Spinner sp;
    Button btnVoltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        PreencheCategorias();

        btnVoltar = (Button)findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent(CadastrarActivity.this, MenusActivity.class);
                startActivity(intent);

            }
        });

    }

    private void PreencheCategorias(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp = (Spinner)findViewById(R.id.spnCategoria);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}


















