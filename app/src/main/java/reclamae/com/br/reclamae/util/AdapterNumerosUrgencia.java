package reclamae.com.br.reclamae.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import reclamae.com.br.reclamae.R;
import reclamae.com.br.reclamae.model.Urgencia;

/**
 * Created by guser on 26/05/2018.
 */

public class AdapterNumerosUrgencia extends BaseAdapter{

    private final List<Urgencia> urgencias;
    private final Activity act;

    public AdapterNumerosUrgencia(List<Urgencia> numeros, Activity act){
        this.urgencias = numeros;
        this.act = act;
    }

    @Override
    public int getCount() {
        return urgencias.size();
    }

    @Override
    public Object getItem(int i) {
        return urgencias.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = act.getLayoutInflater().inflate(R.layout.listviewpersonalizada, viewGroup, false);

        final Urgencia urgencia = urgencias.get(i);

        //pegando as referências das Views
        TextView orgao = (TextView)
                view.findViewById(R.id.urgencia_orgao);
        TextView numero = (TextView)
                view.findViewById(R.id.urgencia_numero);
        ImageView imagem = (ImageView)
                view.findViewById(R.id.urgencia_imagem);

        //populando as Views
        orgao.setText(urgencia.getNome());
        numero.setText(urgencia.getNumero());
        imagem.setImageResource(R.drawable.ligar);


        return view;
    }

    /*
    public void clickEvent(View v) {
        TextView telefone;
        telefone = (TextView) v.findViewById(R.id.urgencia_numero);
        String numero = telefone.getText().toString();
        Uri uri = Uri.parse("tel:" + numero);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        act.startActivity(intent);
    }
    */
}
