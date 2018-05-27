package reclamae.com.br.reclamae.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import reclamae.com.br.reclamae.model.Sugestao;
import reclamae.com.br.reclamae.util.SQLLiteUtil;

public class SugestaoDao {
    SQLLiteUtil cria;

    public SugestaoDao(Context context) {
        SQLLiteUtil ct = new SQLLiteUtil(context);
        cria = ct;
    }

    public void salvar(Sugestao sugestao) {
        ContentValues valores = new ContentValues();

       valores.put("descricao", sugestao.getDescricao());
        valores.put("estado", sugestao.getEstado());
        valores.put("cidade", sugestao.getCidade());
        valores.put("rua", sugestao.getRua());
        valores.put("latitude", sugestao.getLatitude().toString());
        valores.put("longitude", sugestao.getLongitude().toString());
        valores.put("nome", sugestao.getNome().toString());
        cria.getWritableDatabase()
                .insert("sugestao"
                        , null
                        , valores);

    }





    public List<Sugestao> listarSugestoes(){
        SQLiteDatabase db  = cria.getReadableDatabase();
        Sugestao sugestao = new Sugestao();
        Cursor c = null;
        c = db.rawQuery("select descricao, cidade, latitude, longitude, nome from sugestao order by id desc",null);
        //Cursor c = cria.getReadableDatabase().rawQuery(sql, null);
        List<Sugestao> sugestaoes = new ArrayList<>();
        while (c.moveToNext()) {
            sugestao.setDescricao(c.getString(0));
            sugestao.setCidade(c.getString(1));
            sugestao.setLatitude(Double.valueOf(c.getString(2)));
            sugestao.setLongitude(Double.valueOf(c.getString(3)));
            sugestao.setNome(c.getString(4));
            Log.i("descricao" , String.valueOf(sugestao.getDescricao()));
            sugestaoes.add(sugestao);
            sugestao = new Sugestao();
        }
        return sugestaoes;
    }
    public Long contaSugestoes(){
        SQLiteDatabase db  = cria.getReadableDatabase();
        Long result = null;
        Cursor c = null;
        c = db.rawQuery("select count(id) from sugestao order by id desc",null);
        //Cursor c = cria.getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            result= Long.valueOf(c.getString(0));
        }
        return result;
    }


















}
