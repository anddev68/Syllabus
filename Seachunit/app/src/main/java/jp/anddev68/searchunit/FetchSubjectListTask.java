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

import jp.anddev68.searchunit.database.DatabaseAccessor;
import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.parser.OnParsedLineListener;
import jp.anddev68.searchunit.structure.Subject;

/**
 * ?C???p?[?T?[??w????URL???p?[?X???A
 * ?f?[?^?x?[?X????????????
 *
 *
 *
 * Created by hideki on 2014/12/11.
 */
public class FetchSubjectListTask extends AsyncTask<String,Integer,Integer> {

    public interface TaskEndListener {
        public void onEndTask();
    }

    ProgressDialog _dialog;
    Context _context;
    AbstractParser _parser;


    //  ?\?[?XURL??w??
    LinkedList<String> _urls;
    LinkedList<String> _departs;

    //  ?f?[?^?x?[?X????????f?[?^??o?b?t?@
    ArrayList<Subject> _subjects;

    TaskEndListener _listener;

    public void setTaskEndListener(TaskEndListener l) {
        _listener = l;
    }


    /**
     * ? ???l?b?g????_?E?????[?h???A?f?[?^?x?[?X??i?[????
     *
     * @param context
     * @param parser
     * @param grade   ???g?p
     */
    public FetchSubjectListTask(Context context, AbstractParser parser, String grade) {
        _context = context;
        _parser = parser;
        _departs = new LinkedList<>();
        _urls = new LinkedList<>();
        _subjects = new ArrayList<>();

        _parser.setOnParsedLineListener(new OnParsedLineListener() {
            @Override
            public boolean onParsedLine(String subjectName, String url, String code, int grade) {
                return FetchSubjectListTask.this.onParsedLine(subjectName, url, code, "" + grade);
            }
        });


    }


    /**
     * ?_?E?????[?h????\?[?X????????
     */
    public void addDownloadSrc(String url, String depart) {
        _urls.addLast(url);
        _departs.addLast(depart);
    }


    /**
     * ?_?E?????[?h????\?[?X??????擾
     *
     * @return
     */
    public int getSrcCount() {
        return _urls.size();
    }


    @Override
    protected Integer doInBackground(String... params) {
        //	??????_??f?[?^?x?[?X??C???X?^???X????;
        DatabaseHelper helper = new DatabaseHelper(_context);
        SQLiteDatabase db = helper.getWritableDatabase();

        while (!_urls.isEmpty()) {
            //  ????????\?[?X???????????????
            _parser.start(_urls.getFirst());
            _urls.removeFirst();
            _departs.removeFirst();
        }

        //  ?f?[?^?x?[?X????????
        //insertDB();

        //  ?f?[?^?x?[?X??????
        db.close();

        _dialog.dismiss();

        return 0;
    }

    @Override
    protected void onPreExecute() {
        //	?_?C?A???O??
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
     * 1?s??????????????????????
     *
     * @param subjectName ?????
     * @param url         ?V???o?X???URL
     * @param code        ?V???o?X?R?[?h
     * @param grade       ?w?N
     * @return
     */
    private boolean onParsedLine(String subjectName, String url, String code, String grade) {

        //if(gradeId!=_gradeId) return true;

        DatabaseHelper helper = new DatabaseHelper(_context);
        SQLiteDatabase db = helper.getWritableDatabase();

        //  ????e?[?u??????
        DatabaseAccessor.insertSubject(db, subjectName, _departs.getFirst(), grade);

        //  ?V???o?X?e?[?u????URL????
        int subjectId = DatabaseAccessor.getSubjectId(db, subjectName, grade, _departs.getFirst(), -1);
        DatabaseAccessor.insertSyllabus(db, code, subjectId);

        Log.d("", "+++onParsedLine() " + subjectName);

        db.close();

        return true;
    }


}





