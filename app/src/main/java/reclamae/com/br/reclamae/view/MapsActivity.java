package reclamae.com.br.reclamae.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.dao.ReclamacaoDao;
import reclamae.com.br.reclamae.dao.SugestaoDao;
import reclamae.com.br.reclamae.model.Reclamacao;
import reclamae.com.br.reclamae.model.Sugestao;
import reclamae.com.br.reclamae.util.PermissionUtils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private String[] categorias = new String[]{"Selecione", "Saúde", "Sanamento", "Educação", "Limpeza", "Segurança", "Apenas sugestões", "Tudo"};
    Integer tipo;
    Spinner spinner;
    String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        selecionaFiltro();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            Intent intent = getIntent();
            tipo = Integer.valueOf(intent.getStringExtra("tipo"));
            if (tipo == 100){
                tipo = null;
            }
        }catch (NumberFormatException e){
            tipo = null;
        }

        mMap = googleMap;
        List<Reclamacao> reclamacaoes = findUsuariosTipo(tipo);

        // mMap.animateCamera(CameraUpdateFactory.zoomIn());
        for (int i = 0; i < reclamacaoes.size(); i++) {
            LatLng reclame = new LatLng(reclamacaoes.get(i).getLatitude(), reclamacaoes.get(i).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reclame, 15));
            mMap.addMarker(new MarkerOptions().position(reclame)
                    .title("Usuário : " + reclamacaoes.get(i).getNome() + ", Categoria: " +
                            reclamacaoes.get(i).getCategoria())
                    .icon(BitmapDescriptorFactory.defaultMarker(reclamacaoes.get(i).getCor()))
                    .alpha(0.5f)
                    .snippet("Descrição: " + reclamacaoes.get(i).getDescricao()));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(reclame));
        }

        SugestaoDao sugestaoDao = new SugestaoDao(MapsActivity.this);
        List<Sugestao> sugestaoes = sugestaoDao.listarSugestoes();

        for (int s = 0; s < sugestaoes.size(); s++) {
            Log.i("tamanho", String.valueOf(sugestaoes.get(s).getDescricao()));
            LatLng sugestao = new LatLng(sugestaoes.get(s).getLatitude(), sugestaoes.get(s).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sugestao, 15));
            mMap.addMarker(new MarkerOptions().position(sugestao)
                    .title("Usuário : " + sugestaoes.get(s).getNome())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .alpha(1)
                    .snippet("Descrição: " + sugestaoes.get(s).getDescricao()));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(sugestao));
        }
    }

    public void selecionaFiltro(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_dropdown_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.locateSpinner);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                switch (categorias[i]){
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
                       // intent.putExtra("tipo", tipo.toString(4));
                        //startActivity(intent);
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

    private List<Reclamacao> findUsuariosTipo(Integer idCat){
        ReclamacaoDao dao = new ReclamacaoDao(MapsActivity.this);
        List<Reclamacao> reclamacaosSelecioandas = dao.listarReclamacoes();
        List<Reclamacao> results = new ArrayList<>();
        try{
            for(int i = 0; i < reclamacaosSelecioandas.size(); i++){
                if(reclamacaosSelecioandas.get(i).getIdCategoria().equalsIgnoreCase(Integer.valueOf(idCat).toString())){
                    results.add(reclamacaosSelecioandas.get(i));
                }
            }
            if(results.size()==0){
                Toast.makeText(this, "Não existe registro para esta categoria ", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            return reclamacaosSelecioandas;
        }
        return  results;
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
