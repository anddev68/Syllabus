package jp.anddev68.searchunit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**

	エラーが出るため
	毎回インスタンスを確保する方式に変更
	
	fixme:よりよい方法があれば修正をお願いします
	
	2015/3/3 anddev68

*/
public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "data3.db";
    public static int DB_VERSION = 7;

    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

        //  初回のみassetからコピー
        if(!existsDatabase()){
            try {
                copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onOpen(db);

        //  データベースをコピーする作業


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //  初回のみassetからコピー
            try {
                copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }



    public void copyDatabase() throws IOException {
        String path = this.context.getFilesDir().getParent() + "/databases/";
        File pathFile = new File(path);
        if(!pathFile.exists()) pathFile.mkdirs();

        // ファイルのインプット、アウトプットのセット
        InputStream in = this.context.getAssets().open(DB_NAME);
        OutputStream out = new FileOutputStream(path + DB_NAME);

        // ファイルのコピー
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        // インプット、アウトプットのクローズ
        in.close();
        out.close();


    }


    public boolean existsDatabase(){
        File file = new File(this.context.getFilesDir().getParent() + "/databases/" + DB_NAME);
        return file.exists();
    }






}