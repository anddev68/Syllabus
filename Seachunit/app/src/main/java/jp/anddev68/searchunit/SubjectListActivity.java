package jp.anddev68.searchunit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.structure.Subject;


public class SubjectListActivity extends ActionBarActivity {

    private static final int MODE_POINT = 1;    //  点数表示モード
    private static final int MODE_REGISTER = 2;   //  点数登録モード
    private static final int MODE_SYLLABUS = 3;     //  シラバス閲覧モード
    private int _mode;

    SQLiteDatabase _db;
    int _schoolId;
    int _departId;
    int _gradeId;
    String _departName;
    ArrayList<Subject> _subjectList;

    TextView textView2;
    Button button,button2,button3,button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

    }

    @Override
    protected void onResume(){
        super.onResume();
        setupWidget();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,PrefActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ウィジェットの配置を行う
     */
    private void setupWidget(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        DatabaseHelper helper = DatabaseHelper.getInstance(getApplicationContext());
        _db = helper.getWritableDatabase();

        //  ウィジェットの取得
        textView2 = (TextView) findViewById(R.id.textView2);
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mode = MODE_POINT;
                setModeDisplay();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mode = MODE_REGISTER;
                setModeDisplay();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mode = MODE_SYLLABUS;
                setModeDisplay();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfigActivity();   //  設定画面を開く
            }
        });

        //  Prefから設定された学科IDと学年IDを取得
        _departId = pref.getInt("depart_id",-1);   //  学科ID
        _gradeId = pref.getInt("grade_id",-1); //  学年ID
        _schoolId = pref.getInt("school_id",-1);

        //  エラーの場合はここで処理を終了
        if( _schoolId ==-1 || _gradeId == -1 || _departId == -1){
            Toast.makeText(this,"設定画面を開いてください",Toast.LENGTH_SHORT).show();
            _db.close();
            return;
        }

        //  教科一覧と学科名等のセットアップ
        _departName = DatabaseHelper.getDepartName(_db,_departId,"N/A");
        _subjectList = DatabaseHelper.getSubjectList(_db,_departId,_gradeId);

        //  システムメッセージに現在の設定を表示
        TextView tv = (TextView) findViewById(R.id.system_mes);
        tv.setText("現在の設定："+_departName+" "+_gradeId+"年");

        //  初回のみ取得する
        if(_subjectList.isEmpty()){
            Log.i("SubjectListAct","subjectList is Emnpty.");

            //  現在の設定でフェッチを行う
            firstFetch();
            //  ダウンロードタスク終了後、自動でリストビューにセット
            return;
        }

        //  2回目以降はリストビューのセットだけを行う
        onEndTask();

        //  初期モードをセット
        _mode = MODE_POINT; //  点数表示モード
        setModeDisplay();
    }


    /**
     * データフェッチが終了したとき
     * アダプターにセットする
     */
    private void onEndTask(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int id_department = pref.getInt("depart_id",-1);   //  学科ID
        int id_grade = pref.getInt("grade_id",-1); //  学年ID
        _subjectList = DatabaseHelper.getSubjectList(_db,id_department,id_grade);
        ListView lv = (ListView) findViewById(R.id.subject_list);
        lv.setAdapter(new ArrayAdapter<Subject>(this,android.R.layout.simple_list_item_1,_subjectList));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubjectListActivity.this.onListView1ItemClick(parent,view,position,id);
            }
        });
    }

    /**
     * 教科リストがクリックされたとき
     * クリックされたTextViewを取得する
     *
     * 現在のモード別に起動するアクティビティーを変更する
     */
    private void onListView1ItemClick(AdapterView<?> parent,View view,int position,long id){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Subject subject = _subjectList.get((int)id);
        String url = DatabaseHelper.getSyllabusUrl(_db,subject.subjectId,"N/A");
        Log.i("SubjectListActivity","Pressed "+(String)subject.subjectName+" ID:"+subject.subjectId);

        switch(_mode){
            case MODE_SYLLABUS:
                openPdf(url);   //  シラバスを開く
                break;
            case MODE_POINT:
                openDetailActivity(subject);
                break;
            case MODE_REGISTER:
                openRegistActivity(subject,url);
                break;


        }



    }


    /**
     * 初回のみ教科リストを取得する
     * @use FetchSubjectListTask
     */
    private void firstFetch(){
        String top_url = DatabaseHelper.getDepartUrl(_db,_departId,null);
        AbstractParser parser = AbstractParser.create(_schoolId,top_url);
        if(parser==null){
            Toast.makeText(this,"不正な引数",Toast.LENGTH_SHORT).show();
            return;
        }
        FetchSubjectListTask task = new FetchSubjectListTask(this,parser,_db,_departId,_gradeId);
        task.setTaskEndListener(new FetchSubjectListTask.TaskEndListener() {
            @Override
            public void onEndTask() {
                SubjectListActivity.this.onEndTask();
            }
        });
        task.execute();
    }


    /**
     * モードに応じて表示する色を変える
     */
    private void setModeDisplay(){
        switch(_mode) {
            case MODE_POINT:
                textView2.setText("教科一覧[点数表示]");
                textView2.setBackgroundColor(Color.rgb(200,0,0));
                break;
            case MODE_REGISTER:
                textView2.setText("教科一覧[点数登録]");
                textView2.setBackgroundColor(Color.rgb(0,200,0));
                break;
            case MODE_SYLLABUS:
                textView2.setText("教科一覧[シラバス]");
                textView2.setBackgroundColor(Color.rgb(0,0,200));
                break;
        }
    }


    /**
     * 設定画面を開く
     */
    private void openConfigActivity(){
        Intent intent = new Intent(this,PrefActivity.class);
        this.startActivity(intent);
    }

    /**
     * PDFを開く
     */
    private void openPdf(String url){
        Uri uri = Uri.parse("http://docs.google.com/viewer?url="+url);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

    /**
     * 点数登録画面を開く
     * @param subject 科目
     * @param url URL
     */
    private void openRegistActivity(Subject subject,String url){
        Intent intent = new Intent(this,RegistActivity.class);
        intent.putExtra("subject_name",subject.subjectName);
        intent.putExtra("subject_id",subject.subjectId);
        startActivity(intent);
    }

    /**
     * 点数詳細画面を開く
     */
    private void openDetailActivity(Subject subject){
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("subject_id",subject.subjectId);
        intent.putExtra("subject_name",subject.subjectName);
        startActivity(intent);
    }

}
