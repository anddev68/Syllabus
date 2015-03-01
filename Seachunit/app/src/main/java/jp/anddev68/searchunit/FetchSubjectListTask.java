package jp.anddev68.searchunit;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.parser.OnParsedLineListener;
import jp.anddev68.searchunit.structure.Subject;

/**
 * 任意のパーサーで指定したURLをパースし、
 * データベースに教科を追加する
 *
 *
 *
 * Created by hideki on 2014/12/11.
 */
public class FetchSubjectListTask extends AsyncTask<String,Integer,Integer>{

    public interface TaskEndListener{
        public void onEndTask();
    }





    ProgressDialog _dialog;
    Context _context;
    AbstractParser _parser;

    //  ソースURLと学科
    LinkedList<String> _urls;
    LinkedList<String> _departs;

    //  データベースに追加するデータのバッファ
    ArrayList<Subject> _subjects;

    TaskEndListener _listener;
    public void setTaskEndListener(TaskEndListener l){ _listener = l; }


    /**
     * 非同期でネットからダウンロードし、データベースに格納する
     * @param context
     * @param parser
     * @param grade 未使用
     */
    public FetchSubjectListTask(Context context,AbstractParser parser,String grade){
        _context = context;
        _parser = parser;
        _departs = new LinkedList<>();
        _urls = new LinkedList<>();
        _subjects = new ArrayList<>();

        _parser.setOnParsedLineListener(new OnParsedLineListener() {
            @Override
            public boolean onParsedLine(String subjectName,String url,String code,int grade) {
                return FetchSubjectListTask.this.onParsedLine(subjectName,url,code,""+grade);
            }
        });




    }


    /**
     * ダウンロードするソースを追加する
     */
    public void addDownloadSrc(String url,String depart){
        _urls.addLast(url);
        _departs.addLast(depart);
    }


    /**
     * ダウンロードするソースの数を取得
     * @return
     */
    public int getSrcCount(){
        return _urls.size();
    }


    @Override
    protected Integer doInBackground(String... params) {
        while(!_urls.isEmpty()){
            //  追加されたソースを順番に解析していく
            _parser.start(_urls.getFirst());
            _urls.removeFirst();
            _departs.removeFirst();
        }

        //  データベースに追加する
        //insertDB();

        _dialog.dismiss();

        return 0;
    }

    @Override
    protected void onPreExecute() {
        _dialog = new ProgressDialog(_context);
        _dialog.setTitle("Creating List...");
        _dialog.show();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        _listener.onEndTask();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


    /**
     * 1行解析した結果がここに入ります
     * @param subjectName 教科名
     * @param url シラバスへのURL
     * @param code シラバスコード
     * @param grade 学年
     * @return
     */
    private boolean onParsedLine(String subjectName,String url,String code,String grade){

        //if(gradeId!=_gradeId) return true;

        SQLiteDatabase db = DatabaseHelper.getInstance(_context).getWritableDatabase();
        db.acquireReference();


        //  教科テーブルに追加
        DatabaseHelper.insertSubject(db,subjectName,_departs.getFirst(),grade);

        //  シラバステーブルにURLを追加
        int subjectId = DatabaseHelper.getSubjectId(db,subjectName,grade,_departs.getFirst(),-1);
        DatabaseHelper.insertSyllabus(db,code,subjectId);

        Log.d("", "+++onParsedLine() "+subjectName);

        DatabaseHelper.getInstance(_context).close();

        return true;
    }

    /**
     * プリコンパイルステートメントを利用してデータベースに書き込む
     */
    private void insertDB(){
        SQLiteDatabase db = DatabaseHelper.getInstance(_context).getWritableDatabase();
        db.acquireReference();

        //  データ
        SQLiteStatement stat1 = db.compileStatement( "insert into profile_table(name, age) values(?, ?)" );


        //  シラバスコードを追加


        db.close();
    }





}
