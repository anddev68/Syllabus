package jp.anddev68.searchunit;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;

import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.parser.OnParsedLineListener;
import jp.anddev68.searchunit.structure.Subject;

/**
 * 任意のパーサーで指定したURLをパースし、
 * データベースに教科を追加する
 * Created by hideki on 2014/12/11.
 */
public class FetchSubjectListTask extends AsyncTask<String,Integer,Integer>{

    public interface TaskEndListener{
        public void onEndTask();
    }

    ProgressDialog _dialog;
    Context _context;
    AbstractParser _parser;
    SQLiteDatabase _db;
    int _departId;
    int _gradeId;

    TaskEndListener _listener;
    public void setTaskEndListener(TaskEndListener l){ _listener = l; }

    public FetchSubjectListTask(Context context,AbstractParser parser,SQLiteDatabase db,int departId,int gradeId){
        _context = context;
        _parser = parser;
        _db = db;
        _departId = departId;
        _gradeId = gradeId;
        _parser.setOnParsedLineListener(new OnParsedLineListener() {
            @Override
            public boolean onParsedLine(String subjectName,String url,int gradeId) {
                return FetchSubjectListTask.this.onParsedLine(subjectName,url,gradeId);
            }
        });

        _dialog = new ProgressDialog(context);
        _dialog.setTitle("Creating List...");
        _dialog.show();


    }


    @Override
    protected Integer doInBackground(String... params) {
        _parser.start();


        _dialog.dismiss();
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        _listener.onEndTask();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


    private boolean onParsedLine(String subjectName,String url,int gradeId){

        if(gradeId!=_gradeId) return true;

        //  教科テーブルに追加
        DatabaseHelper.insertSubject(_db,subjectName,_departId,_gradeId);
        //  シラバステーブルにURLを追加
        int subjectId = DatabaseHelper.getSubjectId(_db,_departId,_gradeId,-1);
        DatabaseHelper.insertSyllabus(_db,subjectId,url);


        return true;
    }
}
