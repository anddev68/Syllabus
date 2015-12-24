package jp.anddev68.searchunit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.record.Record;

/**
    データベースアクセス用クラス
*/
public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "data4.db";
    public static int DB_VERSION = 12;

    private Context context;
    private SQLiteDatabase database;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper","onCreate()");
        db.execSQL(context.getString(R.string.sql_create_table_my_class));
        db.execSQL(context.getString(R.string.sql_create_table_subject));
        db.execSQL(context.getString(R.string.sql_create_table_user));
        db.execSQL(context.getString(R.string.sql_unique_subject));
        db.execSQL(context.getString(R.string.sql_unique_my_class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper","onUpgrade()");
        db.execSQL(context.getString(R.string.sql_unique_my_class));
    }









}