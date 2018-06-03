package reclamae.com.br.reclamae.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.dao.SugestaoDao;
import reclamae.com.br.reclamae.model.Reclamacao;
import reclamae.com.br.reclamae.model.Sugestao;
import reclamae.com.br.reclamae.util.PermissionUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private SupportMapFragment mapFrag;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double longi= 0.0;
    private Double lati = 0.0;
    List<Sugestao> sugestoesSelecioandas = new ArrayList<>();
    private GoogleMap mMap;
    Button btnMenu;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private String[] categorias = new String[]{"Selecione", "Saúde", "Sanamento", "Educação", "Limpeza", "Segurança", "Apenas sugestões", "Tudo"};
    Integer tipo;
    Spinner spinner;
    String categoria;
    SugestaoDao sugestaoDao = new SugestaoDao(MapsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnMenu = (Button) findViewById(R.id.btnMenu) ;
        btnMenu.setOnClickListener(menu);
        selecionaFiltro();
    }

    View.OnClickListener menu = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MapsActivity.this, MenusActivity.class);
            startActivity(intent);
            finish();
            //onDestroy();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        callConnection();
        googleApiClient.connect();
        try {
            Intent intent = getIntent();
            tipo = Integer.valueOf(intent.getStringExtra("tipo"));
            if (tipo == 100) {
                tipo = null;
            }
        } catch (NumberFormatException e) {
            tipo = null;
        }

        mMap = googleMap;
        List<Reclamacao> reclamacaoes = findReclamacoes(tipo);
        List<Sugestao> sugestaoes = findSugestoes(tipo);

        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        for (int i = 0; i < reclamacaoes.size(); i++) {
            LatLng teste = new LatLng(lati, longi);
            LatLng reclame = new LatLng(reclamacaoes.get(i).getLatitude(), reclamacaoes.get(i).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(teste, 100));
            mMap.addMarker(new MarkerOptions().position(reclame)
                    .title("Usuário :" + reclamacaoes.get(i).getNome() + "<br />" + "Categoria: " + reclamacaoes.get(i).getCategoria()
                            + "<br />" + "Rua: " + reclamacaoes.get(i).getRua())
                    .icon(BitmapDescriptorFactory.defaultMarker(reclamacaoes.get(i).getCor()))
                    .alpha(0.5f)
                    .snippet("Descrição: " + reclamacaoes.get(i).getDescricao()));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(reclame));
            teste(mMap);
        }



        for (int s = 0; s < sugestaoes.size(); s++) {
            LatLng teste = new LatLng(lati, longi);
            Log.i("tamanho", String.valueOf(sugestaoes.get(s).getDescricao()));
            LatLng sugestao = new LatLng(sugestaoes.get(s).getLatitude(), sugestaoes.get(s).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(teste, 100));
            mMap.addMarker(new MarkerOptions().position(sugestao)
                    .title("Usuário :" + sugestaoes.get(s).getNome())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .alpha(1)
                    .snippet("Descrição: " + sugestaoes.get(s).getDescricao()
                            + "<br />" + "Rua: " + sugestaoes.get(s).getRua()));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(sugestao));
            teste(mMap);
        }

    }


    public void teste(GoogleMap map) {
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoContents(Marker marker) {
                TextView tv = new TextView(MapsActivity.this);
                tv.setText(Html.fromHtml("<b><font color=\"#D2691E\">" + marker.getTitle() + ":</font></b> " + marker.getSnippet() ));

                return tv;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                LinearLayout ll = new LinearLayout(MapsActivity.this);
                ll.setPadding(20, 20, 20, 20);
                ll.setBackgroundColor(Color.WHITE);
                TextView tv = new TextView(MapsActivity.this);
                tv.setText(Html.fromHtml("<font color=\"#D2691E\">" +
                        marker.getTitle() + "<br />" + marker.getSnippet() + "</font>"));
                ll.addView(tv);
                return ll;
            }

        });
    }

    public void selecionaFiltro() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_dropdown_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.locateSpinner);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                switch (categorias[i]) {
                    case "Saúde":
                        intent.putExtra("tipo", tipo.toString(0));
                        startActivity(intent);
                        break;
                    case "Sanamento":
                        intent.putExtra("tipo", tipo.toString(1));
                        startActivity(intent);
                        break;
                    case "Educação":
                        intent.putExtra("tipo", tipo.toString(2));
                        startActivity(intent);
                        break;
                    case "Limpeza":
                        intent.putExtra("tipo", tipo.toString(3));
                        startActivity(intent);
                        break;
                    case "Segurança":
                        intent.putExtra("tipo", tipo.toString(4));
                        startActivity(intent);
                        break;
                    case "Apenas sugestões":
                        intent.putExtra("tipo", tipo.toString(5));
                        startActivity(intent);
                        break;
                    case "Tudo":
                        intent.putExtra("tipo", tipo.toString(100));
                        startActivity(intent);
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //tipo = 100;
            }
        });
    }

    private List<Reclamacao> findReclamacoes(Integer idCat) {
        ReclamacaoDao dao = new ReclamacaoDao(MapsActivity.this);
        List<Reclamacao> reclamacaosSelecioandas = dao.listarReclamacoes();
        List<Reclamacao> results = new ArrayList<>();
        try {
            for (int i = 0; i < reclamacaosSelecioandas.size(); i++) {
                if (reclamacaosSelecioandas.get(i).getIdCategoria().equalsIgnoreCase(Integer.valueOf(idCat).toString())) {
                    results.add(reclamacaosSelecioandas.get(i));
                }
            }
            if ((results.size() == 0) && (tipo !=5)) {
                Toast.makeText(this, "Não existe registro para esta categoria ", Toast.LENGTH_SHORT).show();
            }

        } catch (NullPointerException e) {
            return reclamacaosSelecioandas;
        }
        return results;
    }

    private List<Sugestao> findSugestoes(Integer tipo){
        try{
            if((tipo==null) || (tipo == 5)){
                SugestaoDao dao = new SugestaoDao(MapsActivity.this);
                sugestoesSelecioandas = dao.listarSugestoes();
                return sugestoesSelecioandas;
            }else {
                return new ArrayList<>();
            }
        }catch (NullPointerException e){
            return sugestoesSelecioandas;
        }
    }


    private void enableMyLocation() {
        //Testa se o usuário ja tem permissão
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Falta a permissão para acessar a localização
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Acesso a localizaçao permitida
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Ativa o MyLocation se a permissão tiver sido concedida
            enableMyLocation();
        } else {
            // Permissão não concedida
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
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
            Log.i("LOG", "onLocationChanged(" +location.toString() + ")");
            Double latPoint = location.getLatitude();
            Double lngPoint = location.getLongitude();
            lati  = latPoint;
            longi = lngPoint;
            googleApiClient.disconnect();
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