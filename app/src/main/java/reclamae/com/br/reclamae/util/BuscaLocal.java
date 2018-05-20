package reclamae.com.br.reclamae.util;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import reclamae.com.br.reclamae.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BuscaLocal extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };


    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAltitude;
    private TextView tvVelocidade;
    private TextView tvProvedor;
    private TextView tvPresicao;
    private TextView tvHora;
    private TextView tvRua;
    private TextView tvCidade;
    private TextView tvEstado;
    private TextView tvPais;
    private TextView tvCompleto;
    private Address endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localizacao);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvAltitude = (TextView) findViewById(R.id.tvAltitude);
        tvPresicao = (TextView) findViewById(R.id.tvPrecisao);
        tvVelocidade = (TextView) findViewById(R.id.tvVelocidade);
        tvProvedor = (TextView) findViewById(R.id.tvProvedor);
        tvHora = (TextView) findViewById(R.id.tvHora);
        tvCidade = (TextView) findViewById(R.id.tvCidade);
        tvEstado = (TextView) findViewById(R.id.tvEstado);
        tvPais = (TextView) findViewById(R.id.tvPais);
        tvRua = (TextView) findViewById(R.id.tvRua);
        tvCompleto = (TextView) findViewById(R.id.tvCompleto);

        callConnection();
        PermissionUtils.validate(this, 0, permissoes);

        googleApiClient.connect();
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

    @Override
    public void onPause() {
        super.onPause();

        if (googleApiClient != null) {
            stopLocationUpdate();
        }
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
        Log.i("LOG", "onLocationChanged(" +location.toString() + ")");
        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();
        String resultAddress = "";


        tvLatitude.setText("Latitude: "+latPoint.toString());
        tvLongitude.setText("Longitude: "+lngPoint.toString());
        tvAltitude.setText("Altitude: " + location.getAltitude());
        tvVelocidade.setText("Velocidade: " + location.getSpeed());
        tvProvedor.setText("Provider: " + location.getProvider());
        tvPresicao.setText("Accuracy: " + location.getAccuracy());
        tvHora.setText("Speed: " + DateFormat.getTimeInstance().format(new Date()));

        try {
            endereco = getEndereco(latPoint,lngPoint);
            Log.i("LOG","Atualizar "+endereco.getThoroughfare());

            for(int i = 0, tam = endereco.getMaxAddressLineIndex(); i < tam; i++){
                resultAddress += endereco.getAddressLine(i);
                resultAddress += i < tam - 1 ? ", " : "";
            }
            tvRua.setText("Rua: "+endereco.getThoroughfare());

            tvCidade.setText("Cidade: "+endereco.getLocality());
            tvEstado.setText("Estado: "+endereco.getAdminArea());
            tvPais.setText("Pais:"+endereco.getCountryName());
            tvCompleto.setText("Completo: "+resultAddress);

        } catch (IOException e) {
            e.printStackTrace();
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
        return endereco;
    }
}
