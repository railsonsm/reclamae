package reclamae.com.br.reclamae.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
        valores.put("categoria", reclamacao.getCategoria());
        valores.put("latitude", reclamacao.getLatitude().toString());
        valores.put("longitude", reclamacao.getLongitude().toString());
        valores.put("cor", reclamacao.getCor().toString());
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
        Reclamacao reclamacao = new Reclamacao();
        String sql = "select descricao, cidade, latitude, longitude, categoria, cor from reclamacao order by id desc";
        Cursor c = cria.getReadableDatabase().rawQuery(sql, null);
        List<Reclamacao> reclamacaoes = new ArrayList<>();
        while (c.moveToNext()) {
            reclamacao.setDescricao(c.getString(0));
            reclamacao.setCidade(c.getString(1));
            reclamacao.setLatitude(Double.valueOf(c.getString(2)));
            reclamacao.setLongitude(Double.valueOf(c.getString(3)));
            reclamacao.setCategoria(c.getString(4));
            reclamacao.setCor(Float.valueOf(c.getString(5)));
            reclamacaoes.add(reclamacao);
        }
        return reclamacaoes;

    }


















}
