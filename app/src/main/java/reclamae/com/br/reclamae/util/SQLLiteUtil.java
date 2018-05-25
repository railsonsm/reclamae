package reclamae.com.br.reclamae.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLLiteUtil extends SQLiteOpenHelper {
    private static final int VERSAO=1;
    private static final String BANCO="reclamae.db";



    public SQLLiteUtil(Context context) {
        super(context, BANCO, null, VERSAO);
    }
    private static final String CREATE_TABLE_USUARIO =
            "create table if not exists usuario (" +
            "id Integer primary key autoincrement " +
            ", nome varchar(80) not null " +
            ", sobrenome varchar(80) not null " +
            ", email varchar(80) not null unique" +
            ", senha varchar (20) not null)";
    private static final String CREATE_TABLE_RECLAMACAO =
            "create table if not exists reclamacao (" +
            "id Integer primary key autoincrement " +
            ", descricao varchar(80) not null " +
            ", estado varchar(80) not null " +
            ", cidade varchar(80) not null " +
            ", nome varchar(80) not null " +
            ", cor varchar(80) not null " +
            ", categoria varchar(80) not null " +
             ", idcategoria varchar(2) not null " +
            ", rua varchar(80) not null " +
            ", longitude varchar (20) " +
            ", latitude varchar (20)  )";

    private static final String CREATE_TABLE_SUGESTAO =
            "create table if not exists sugestao (" +
                    "id Integer primary key autoincrement " +
                    ", descricao varchar(80) not null " +
                    ", estado varchar(80) not null " +
                    ", cidade varchar(80) not null " +
                    ", nome varchar(80) not null " +
                    ", rua varchar(80) not null " +
                    ", longitude varchar (20) " +
                    ", latitude varchar (20)  )";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SUGESTAO);
        db.execSQL(CREATE_TABLE_RECLAMACAO);
        db.execSQL(CREATE_TABLE_USUARIO);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
