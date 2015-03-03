package jp.anddev68.searchunit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**

	エラーが出るため
	毎回インスタンスを確保する方式に変更
	
	fixme:よりよい方法があれば修正をお願いします
	
	2015/3/3 anddev68

*/
public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "data3.db";
    public static int DB_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onOpen(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	//	upgradeされたときの処理
	//	テーブルを削除し、
	//	再作成する
	//	onCreate(db);
    }









}