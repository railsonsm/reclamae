package reclamae.com.br.reclamae.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import reclamae.com.br.reclamae.model.Usuario;
import reclamae.com.br.reclamae.util.SQLLiteUtil;

public class UsuarioDao  {
    SQLLiteUtil cria;

    public UsuarioDao(Context context) {
        SQLLiteUtil ct = new SQLLiteUtil(context);
        cria = ct;
    }

    public void salvar(Usuario usuario) {
        ContentValues valores = new ContentValues();

        valores.put("nome", usuario.getNome());
        valores.put("sobrenome", usuario.getSobrenome());
        valores.put("email", usuario.getEmail());
        valores.put("senha", usuario.getSenha());
        cria.getWritableDatabase()
                .insert("usuario"
                        , null
                        , valores);

    }

    public Usuario buscaUsuario(String email){
        Usuario usuario = new Usuario();
        String[] colunas= {"email", "senha"};
        SQLiteDatabase db  = cria.getReadableDatabase();
        Cursor c = null;
        String empName = "";
        try {
            c = db.rawQuery("SELECT email, senha, nome FROM usuario WHERE email=?", new String[] {email});
            while (c.moveToNext()) {
                usuario.setEmail(c.getString(0));
                usuario.setSenha(c.getString(1));
                usuario.setNome(c.getString(2));
            }
            return usuario;
        }finally {
            c.close();
        }
    }
}
