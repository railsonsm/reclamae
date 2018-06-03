package reclamae.com.br.reclamae.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.model.Reclamacao;
import reclamae.com.br.reclamae.model.Usuario;
import reclamae.com.br.reclamae.util.PermissionUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ReclamarActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    float bitmapDescriptorFactory =  BitmapDescriptorFactory.HUE_RED;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private String[] categorias = new String[]{"Saúde", "Sanamento", "Educação", "Limpeza", "Segurança"};
    private Spinner spnCategoria;
    Button btnVoltar;
    Button btnBuscarLocal;
    Button btnReclamar;
    private Address endereco;
    private EditText txtDescricao;
    private EditText txtRua;
    private EditText txtCidade;
    private EditText txtEstado;
    private Double longi= 0.0;
    private Double lati = 0.0;
    Usuario usuario;
    private  static final String COMPARILHADO = "Compartilhado";
    String nomeUsuario;
    Reclamacao reclamacao= new Reclamacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reclamacao= new Reclamacao();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamar);
        PreencheCategorias();
        btnBuscarLocal = (Button) findViewById(R.id.btnBuscarLoc);
        btnVoltar = (Button)findViewById(R.id.btnVoltar);
        btnReclamar = (Button)findViewById(R.id.btnReclamar);
        txtDescricao = (EditText) findViewById(R.id.txtDescricao);
        txtEstado = (EditText) findViewById(R.id.txtEstado);
        txtRua = (EditText) findViewById(R.id.txtRua);
        txtCidade = (EditText) findViewById(R.id.txtCidade);
        spnCategoria = (Spinner) findViewById(R.id.spnCategoria);
        btnVoltar.setOnClickListener(voltarMenu);
        btnReclamar.setOnClickListener(clickSalvar);
        btnBuscarLocal.setOnClickListener(buscarLocalizacao);
        PermissionUtils.validate(this, 0, permissoes);
        SharedPreferences nome = getSharedPreferences(COMPARILHADO, MODE_PRIVATE);
        nomeUsuario = nome.getString("nome", "oi");
    }

    private boolean validaCampos(){
        boolean res = false;

        String descricao = txtDescricao.getText().toString();
        String cidade = txtCidade.getText().toString();
        String estado = txtEstado.getText().toString();
        String rua = txtRua.getText().toString();
        String mensagem = "";

        if(res = isCampoVazio(descricao)){
            txtDescricao.requestFocus();
            res = false;
            mensagem("Campo descrição é obrigatório");
        }else if(res =isQtdDigitos(descricao)){
            res = false;
            mensagem("Campo descrição deve ter mais de 10 caracteres");
        }else if(res = isCampoVazio(rua)){
            txtRua.requestFocus();
            res = false;
            mensagem("Campo rua é obrigatório");
        }else if(res= isCampoVazio(cidade)){
            txtCidade.requestFocus();
            res = false;
            mensagem("Campo cidade é obrigatório");
        }else if(res = isCampoVazio(estado)){
            txtEstado.requestFocus();
            res = false;
            mensagem("Campo estado é obrigatório");
        }else if ((longi == 0.0) || (lati == 0.0)){
            res = false;
            mensagem("Clique no icone para pegar a localização");
        }else{
            res=true;
        }
        return res;
    }


    View.OnClickListener buscarLocalizacao = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callConnection();
            Toast.makeText(ReclamarActivity.this, "Aguarde...", Toast.LENGTH_SHORT).show();
            googleApiClient.connect();
        }
    };

    View.OnClickListener clickSalvar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(validaCampos()){
                cadastrarReclamcao();
            }
            return;
        }
    };
    private void cadastrarReclamcao(){
        try {
            reclamacao.setDescricao(txtDescricao.getText().toString());
            reclamacao.setRua(txtRua.getText().toString());
            reclamacao.setCidade(txtCidade.getText().toString());
            reclamacao.setEstado(txtEstado.getText().toString());
            reclamacao.setLongitude(longi);
            reclamacao.setLatitude(lati);
            reclamacao.setNome(nomeUsuario);
            ReclamacaoDao dao = new ReclamacaoDao(ReclamarActivity.this);
            dao.salvar(reclamacao);
            reclamacao= new Reclamacao();
            Toast.makeText(ReclamarActivity.this, "Reclamação registrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ReclamarActivity.this, MenusActivity.class);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        boolean resultado = (valor.length()< 11);
        return resultado;
    }


    View.OnClickListener voltarMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ReclamarActivity.this, MenusActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void PreencheCategorias(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, categorias);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCategoria = (Spinner)findViewById(R.id.spnCategoria);
        spnCategoria.setAdapter(adapter);
        spnCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reclamacao.setCategoria(spnCategoria.getItemAtPosition(i).toString());
                switch (i){
                    case 0:
                        reclamacao.setCor(bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_RED);
                        reclamacao.setIdCategoria(Integer.toString(0));
                        break;
                    case 1:
                        reclamacao.setCor(bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_ORANGE);
                        reclamacao.setIdCategoria(Integer.toString(1));
                        break;
                    case 2:
                        reclamacao.setCor( bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_AZURE);
                        reclamacao.setIdCategoria(Integer.toString(2));
                        break;
                    case 3:
                        reclamacao.setCor( bitmapDescriptorFactory =  BitmapDescriptorFactory.HUE_MAGENTA);
                        reclamacao.setIdCategoria(Integer.toString(3));
                        break;
                    case 4:
                        reclamacao.setCor(bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_VIOLET);
                        reclamacao.setIdCategoria(Integer.toString(4));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }












































    private synchronized void callConnection() {
        Log.i("LOG", "callConnection()");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }

    private void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else
            googleApiClient.connect();
    }

    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION , ACCESS_FINE_LOCATION})
    public void onResume() {
        super.onResume();

        if (googleApiClient != null && googleApiClient.isConnected())
            startLocationUpdate();

    }



    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    private void startLocationUpdate(){
        initLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this );
    }

    private void stopLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    @RequiresPermission(allOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "UpdateLocationActivity.onConnected(" + bundle + ")");

        @SuppressLint("MissingPermission") Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(googleApiClient); // PARA JÁ TER UMA COORDENADA PARA O UPDATE FEATURE UTILIZAR
        startLocationUpdate();

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "UpdateLocationActivity.onConnectionSuspended(" + i + ")");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "UpdateLocationActivity.onConnectionFailed(" + connectionResult + ")");

    }




    @Override
    public void onLocationChanged(Location location) {
        try {
        Log.i("LOG", "onLocationChanged(" +location.toString() + ")");
        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();
        String resultAddress = "";

        lati  = latPoint;
        longi = lngPoint;




            endereco = getEndereco(latPoint,lngPoint);
            Log.i("LOG","Atualizar "+endereco.getThoroughfare());

            for(int i = 0, tam = endereco.getMaxAddressLineIndex(); i < tam; i++){
                resultAddress += endereco.getAddressLine(i);
                resultAddress += i < tam - 1 ? ", " : "";
            }
            txtRua.setText(endereco.getThoroughfare().toString());

            txtCidade.setText(endereco.getLocality());
            txtEstado.setText(endereco.getAdminArea());

        } catch (IOException e) {
            googleApiClient.disconnect();
            Toast.makeText(this, "Sem conexão, mas a localizacao foi captudara", Toast.LENGTH_SHORT).show();
            return;
        }

    }


    public Address getEndereco(double latitude, double longitude) throws IOException {

        Geocoder geocoder;
        Address endereco= null;
        List<Address> enderecos;
        geocoder = new Geocoder(getApplicationContext());
        enderecos = geocoder.getFromLocation(latitude,longitude,1);
        if(enderecos.size()> 0)
            endereco = enderecos.get(0);
        googleApiClient.disconnect();
        return endereco;
    }
}


















