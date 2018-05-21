package reclamae.com.br.reclamae.view;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.model.Reclamacao;
import reclamae.com.br.reclamae.util.PermissionUtils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private List<Reclamacao> reclamacaoes;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ReclamacaoDao dao = new ReclamacaoDao(MapsActivity.this);
        List<Reclamacao> reclamacaoes = dao.listarReclamacoes();
        // mMap.animateCamera(CameraUpdateFactory.zoomIn());
        for (int i = 0; i < reclamacaoes.size(); i++) {
            LatLng ponto = new LatLng(reclamacaoes.get(i).getLatitude(), reclamacaoes.get(i).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ponto, 15));
            mMap.addMarker(new MarkerOptions().position(ponto)
                    .title("Usuário : " + reclamacaoes.get(i).getNome() + ", Categoria: " + reclamacaoes.get(i).getCategoria())
                    .icon(BitmapDescriptorFactory.defaultMarker(reclamacaoes.get(i).getCor()))
                    .snippet("Descrição: " + reclamacaoes.get(i).getDescricao()));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(ponto));
        }

    /*
        mMap.animateCamera(CameraUpdateFactory.zoomIn());        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-12.9812459, -38.4585933);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
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
}
