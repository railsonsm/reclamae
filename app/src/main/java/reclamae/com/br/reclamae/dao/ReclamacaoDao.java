package reclamae.com.br.reclamae.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import reclamae.com.br.reclamae.model.Reclamacao;
import reclamae.com.br.reclamae.util.SQLLiteUtil;

public class ReclamacaoDao {
    SQLLiteUtil cria;

    public ReclamacaoDao(Context context) {
        SQLLiteUtil ct = new SQLLiteUtil(context);
        cria = ct;
    }

    public void salvar(Reclamacao reclamacao) {
        ContentValues valores = new ContentValues();

       valores.put("descricao", reclamacao.getDescricao());
        valores.put("estado", reclamacao.getEstado());
        valores.put("cidade", reclamacao.getCidade());
        valores.put("rua", reclamacao.getRua());
        valores.put("idCategoria", reclamacao.getIdCategoria());
        valores.put("categoria", reclamacao.getCategoria());
        valores.put("latitude", reclamacao.getLatitude().toString());
        valores.put("longitude", reclamacao.getLongitude().toString());
        valores.put("cor", reclamacao.getCor().toString());
        valores.put("nome", reclamacao.getNome().toString());
        cria.getWritableDatabase()
                .insert("reclamacao"
                        , null
                        , valores);

    }

    public void apagar(Integer id){
        cria.getWritableDatabase()
                .delete("reclamacao",
                        "id =" + id,
                        null);
    }

    public void atualizar(Integer id, String nome, String telefone) {
        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        valores.put("telefone", telefone);

        cria.getWritableDatabase()
                .update("reclamacao"
                        , valores,
                        "_id =" + id,
                        null);

    }





    public List<Reclamacao> listarReclamacoes(){
        SQLiteDatabase db  = cria.getReadableDatabase();
        Reclamacao reclamacao = new Reclamacao();
        Cursor c = null;
        c = db.rawQuery("select descricao, cidade, latitude, longitude, categoria, cor, nome, idCategoria, rua from reclamacao order by id desc",null);
        //Cursor c = cria.getReadableDatabase().rawQuery(sql, null);
        List<Reclamacao> reclamacaoes = new ArrayList<>();
        while (c.moveToNext()) {
            reclamacao.setDescricao(c.getString(0));
            reclamacao.setCidade(c.getString(1));
            reclamacao.setLatitude(Double.valueOf(c.getString(2)));
            reclamacao.setLongitude(Double.valueOf(c.getString(3)));
            reclamacao.setCategoria(c.getString(4));
            reclamacao.setCor(Float.valueOf(c.getString(5)));
            reclamacao.setNome(c.getString(6));
            reclamacao.setIdCategoria(c.getString(7));
            reclamacao.setRua(c.getString(8));
            reclamacaoes.add(reclamacao);
            reclamacao = new Reclamacao();
        }
        return reclamacaoes;
    }

    public Long contaReclamacoes(){
        SQLiteDatabase db  = cria.getReadableDatabase();
        Long result = null;
        Cursor c = null;
        c = db.rawQuery("select count(id) from reclamacao order by id desc",null);
        //Cursor c = cria.getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            result= Long.valueOf(c.getString(0));
        }
        return result;
    }

    public List<Reclamacao> contaReclamacoesPorCategoria(){
        SQLiteDatabase db  = cria.getReadableDatabase();
        List<Reclamacao> contagem = new ArrayList<>();
        Reclamacao reclamacao = new Reclamacao();
        Cursor c = null;
        c = db.rawQuery("select count(id), categoria from reclamacao group by categoria order by id desc",null);
        //Cursor c = cria.getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            reclamacao.setId(Long.valueOf(c.getString(0)));
            reclamacao.setCategoria(c.getString(1 ));
            contagem.add(reclamacao);
            reclamacao = new Reclamacao();
        }
        return contagem;

    }



}
