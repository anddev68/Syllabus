package jp.anddev68.searchunit;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.parser.GnctParser;
import jp.anddev68.searchunit.structure.Subject;


public class SubjectListActivity extends Activity {

    private static final int MODE_POINT = 1;    //  点数表示モード
    private static final int MODE_REGISTER = 2;   //  点数登録モード
    private static final int MODE_SYLLABUS = 3;     //  シラバス閲覧モード
    private static final int NON_SELECT_COLOR = Color.rgb(0x99,0x99,0x99);
    private static final int SELECT_COLOR = Color.rgb(0x55,0x55,0x55);
    private int _mode;




    private SQLiteDatabase _db;
    private String _grade;
    private String _depart;
    private SharedPreferences _pref;


    TextView textView2;
    //  Button button,button2,button3,button4;
    ListView listView;
    LinearLayout[] buttonLayouts;   //  擬似ボタン


    //  教科表
    ArrayList<String> allSubjectName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

    }

    @Override
    protected void onResume(){
        super.onResume();

        //  設定画面を開くかどうかのチェックを行う
        if(configCheck()){
            openConfigActivity();
            return;
        }

        //  設定値を取得
        _pref = PreferenceManager.getDefaultSharedPreferences(this);
        DatabaseHelper helper = DatabaseHelper.getInstance(getApplicationContext());
        _db = helper.getWritableDatabase();
        _grade = _pref.getString("grade","");
        _depart = _pref.getString("depart","");

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
     * 設定のチェック
     * 次回表示しない設定（未実装）もしくは学科と学年データが設定されていないかどうかチェック
     *
     * @return 条件を満たす
     **/
    private boolean configCheck(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String grade = pref.getString("grade",null);
        String depart = pref.getString("depart",null);
        if(grade==null || depart==null){
            return true;
        }
        return false;
    }



    /**
     * ウィジェットの配置を行う
     */
    private void setupWidget(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        DatabaseHelper helper = DatabaseHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        String grade = pref.getString("grade","");
        String depart = pref.getString("depart","");

        //  ウィジェットの取得
        textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.subject_list);

        buttonLayouts = new LinearLayout[4];
        buttonLayouts[0] = (LinearLayout) findViewById(R.id.lbutton1);
        buttonLayouts[1] = (LinearLayout) findViewById(R.id.lbutton2);
        buttonLayouts[2] = (LinearLayout) findViewById(R.id.lbutton3);
        buttonLayouts[3] = (LinearLayout) findViewById(R.id.lbutton4);
        LinearLayout.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SubjectListActivity.this.onClickLButton(v);
            }
        };
        for(int i=0; i<buttonLayouts.length; i++) buttonLayouts[i].setOnClickListener(listener);


        //  初期モードをセットする
        _mode = MODE_POINT; //  点数表示モード
        setModeDisplay();

        //  教科表を取得してくる
        allSubjectName = DatabaseHelper.getAllSubjectName(db, grade, depart);

        //  データがない場合はネット上に取りに行く
        if(allSubjectName.isEmpty()){
            Log.i("SubjectListAct","subjectList is Empty.");
            firstFetch();
        }else{
            //  データが存在する場合はリストビューにセット
            //  =ダウンロードが終わったことにする
            onEndTask();
        }


    }



    /**
     * データフェッチが終了したとき
     * アダプターにセットする
     */
    private void onEndTask(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(allSubjectName.isEmpty()){
            allSubjectName = DatabaseHelper.getAllSubjectName(_db, _grade, _depart);
        }
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allSubjectName));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        String subjectName = (String)parent.getItemAtPosition(position);
        String depart = pref.getString("depart",null);
        String grade = pref.getString("grade",null);
        DatabaseHelper helper = DatabaseHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        int subjectId = DatabaseHelper.getSubjectId(db,subjectName,grade,depart,-1);

        switch(_mode){
            case MODE_SYLLABUS:
                //openPdf(url);   //  シラバスを開く
                Toast.makeText(this,"No Implemented",Toast.LENGTH_SHORT).show();
                break;
            case MODE_POINT:
                openDetailActivity(subjectId,subjectName);
                break;
            case MODE_REGISTER:
                Toast.makeText(this,"No Implemented",Toast.LENGTH_SHORT).show();
                //openRegistActivity(subject,url);
                break;


        }



    }


    /**
     * ボタンが押された時
     * モード切り替えor設定メニューの表示
     */
    private void onClickLButton(View v){
        switch(v.getId()){
            case R.id.lbutton1:
                _mode = MODE_POINT;
                setModeDisplay();
                break;
            case R.id.lbutton2:
                _mode = MODE_REGISTER;
                setModeDisplay();
                break;
            case R.id.lbutton3:
                _mode = MODE_SYLLABUS;
                setModeDisplay();
                break;
            case R.id.lbutton4:
                openConfigActivity();
                break;
        }
    }




    /**
     * 初回のみ教科リストを取得する
     * @use FetchSubjectListTask
     */
    private void firstFetch(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        DatabaseHelper helper = DatabaseHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        String depart = pref.getString("depart","");
        String grade = pref.getString("grade","");
        String[] urlArray = getResources().getStringArray(R.array.url_array);   //  学科URL表
        String[] departArray = getResources().getStringArray(R.array.depart_entryValues);   //学科表
        String url = "";
        for(int i=0; i<departArray.length; i++){
            if(depart.equals(departArray[i])){
                //  一致した番号がその学科のURL
                url = urlArray[i];  //  選択されている学科と表で一致した番号を取得
            }
        }
        Log.d("SubjectListAct","Start DL From:"+url);

        AbstractParser parser = new GnctParser(url);
        FetchSubjectListTask task = new FetchSubjectListTask(this,parser,db,depart,grade);
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
     * あとクリックラブルを外す
     */
    private void setModeDisplay(){
        for(int i=0; i<buttonLayouts.length; i++){
            buttonLayouts[i].setBackgroundColor(NON_SELECT_COLOR);
            buttonLayouts[i].setClickable(true);
        }


        switch(_mode) {
            case MODE_POINT:
                textView2.setText("教科一覧[点数表示]");
                textView2.setBackgroundColor(Color.rgb(200,0,0));
                buttonLayouts[0].setClickable(false);
                buttonLayouts[0].setBackgroundColor(SELECT_COLOR);
                break;
            case MODE_REGISTER:
                textView2.setText("教科一覧[点数登録]");
                textView2.setBackgroundColor(Color.rgb(0,200,0));
                buttonLayouts[1].setBackgroundColor(SELECT_COLOR);
                buttonLayouts[1].setClickable(false);
                break;
            case MODE_SYLLABUS:
                textView2.setText("教科一覧[シラバス]");
                textView2.setBackgroundColor(Color.rgb(0,0,200));
                buttonLayouts[2].setBackgroundColor(SELECT_COLOR);
                buttonLayouts[2].setClickable(false);
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
    private void openDetailActivity(int subjectId,String subjectName){
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("subject_id",subjectId);
        intent.putExtra("subject_name",subjectName);
        startActivity(intent);
    }

}
