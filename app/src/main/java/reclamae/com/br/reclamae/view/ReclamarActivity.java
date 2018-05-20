package reclamae.com.br.reclamae.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.util.Log;
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
    Reclamacao reclamacao= new Reclamacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            try {
                reclamacao.setDescricao(txtDescricao.getText().toString());
                reclamacao.setRua(txtRua.getText().toString());
                reclamacao.setCidade(txtCidade.getText().toString());
                reclamacao.setEstado(txtEstado.getText().toString());
                reclamacao.setLongitude(longi);
                reclamacao.setLatitude(lati);
                ReclamacaoDao dao = new ReclamacaoDao(ReclamarActivity.this);
                dao.salvar(reclamacao);
                Toast.makeText(ReclamarActivity.this, "Reclamação registrada", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener voltarMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ReclamarActivity.this, MenusActivity.class);
            startActivity(intent);
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
                        break;
                    case 1:
                        reclamacao.setCor(bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_ORANGE);
                        break;
                    case 2:
                        reclamacao.setCor( bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_AZURE);
                        break;
                    case 3:
                        reclamacao.setCor( bitmapDescriptorFactory =  BitmapDescriptorFactory.HUE_BLUE);
                        break;
                    case 4:
                        reclamacao.setCor(bitmapDescriptorFactory = BitmapDescriptorFactory.HUE_GREEN);
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
            txtRua.setText(endereco.getThoroughfare());

            txtCidade.setText(endereco.getLocality());
            txtEstado.setText(endereco.getAdminArea());

        } catch (IOException e) {
            googleApiClient.disconnect();
            Toast.makeText(this, "Sem conexão, tente novamente", Toast.LENGTH_SHORT).show();
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


















