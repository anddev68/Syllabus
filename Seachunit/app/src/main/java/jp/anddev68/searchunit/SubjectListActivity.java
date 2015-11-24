package jp.anddev68.searchunit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import jp.anddev68.searchunit.database.DatabaseAccessor;
import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.parser.GnctParser;
import jp.anddev68.searchunit.ui.adapter.SubjectListAdapter;
import jp.anddev68.searchunit.widget.drawer.DrawerAdapter;

/**
	教科一覧のアクティビティー
*/

public class SubjectListActivity extends AppCompatActivity {

    private static final int MODE_POINT = 1;    //  点数表示モード
    private static final int MODE_REGISTER = 2;   //  点数登録モード
    private static final int MODE_SYLLABUS = 3;     //  シラバス閲覧モード

    private int _mode;

    private String _grade;
    private boolean _plusGMode;  //  一般科追加モード
    private String _depart;
    private SharedPreferences _pref;

    public static final int REQUEST_CODE_PREF = 0;

    TextView textView2;
    ListView listView;
    LinearLayout[] buttonLayouts;   //  擬似ボタン
    LinearLayout rootView;
    TextView configText;



    //  教科表
    ArrayList<String> allSubjectName;
    //  一般科教科表
    ArrayList<String> gSubjectName;

    SubjectListAdapter adapter;


    /* Widget */
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    ListView drawerListView;

    /* Adapter */
    DrawerAdapter drawerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        /* ツールバーの初期化 */
        initToolbar();
        /* メニュードロワーの初期化 */
        initDrawer();

        /* 初回起動時に設定画面を表示する */


        /* 初回起動時でない場合はデータを持ってくる */



        //  設定画面を開くかどうかのチェックを行う
        if(configCheck()){
            openConfigActivity();
            return;
        }

    }

    @Override
    protected void onResume(){
        super.onResume();

        //  設定値を取得
        _pref = PreferenceManager.getDefaultSharedPreferences(this);
        _grade = _pref.getString("grade","");
        _depart = _pref.getString("depart","");
        _plusGMode = _pref.getBoolean("plusG",false);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_CODE_PREF:
                switch(resultCode){
                    case SubjectListActivity.RESULT_CANCELED:
                        if(configCheck()) finish(); //  初期状態で何もせず戻るボタンを押した場合は終了させる
                        return;
                    case SubjectListActivity.RESULT_FIRST_USER:
                        //  スタートアップガイドを開く
                        //  fixme:boot_flagを書き換えることで擬似的にガイドを開いている
                        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_boot",true).commit();
                        Intent intent = new Intent(this,StartupActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                }
            break;
        }



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }





    /**
     *  Drawerを初期化する
     */
    private void initDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        drawerListView = (ListView) findViewById(R.id.drawerListView);
        drawerAdapter = new DrawerAdapter(this);
        drawerListView.setAdapter(drawerAdapter);

    }

    /**
     *  ツールバーを初期化する
     */
    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * すべての学年、学科の教科データを取得する、タスクを開始する
     */
    private void startDownloadingAllSubjects(){

    }



    /**
     * 教科データの取得終了後のハンドラー
     */





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
        String grade = pref.getString("grade","");
        String depart = pref.getString("depart","");

        //  ウィジェットの取得
        //textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.subject_list);
        rootView =(LinearLayout) findViewById(R.id.root);
        //configText = (TextView) findViewById(R.id.system_mes);



        //  アダプター初期化
       /*
        adapter = new ArrayAdapter<String>(this,R.layout.custom_list_item1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubjectListActivity.this.onListView1ItemClick(parent,view,position,id);
            }
        });
        listView.setAdapter(adapter);
        */


        //  初期モードをセットする
        _mode = MODE_POINT; //  点数表示モード
        setModeDisplay();

        //  データを取得する
        AbstractParser parser = new GnctParser();
        FetchSubjectListTask task = new FetchSubjectListTask(this,parser,_grade);
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        allSubjectName = DatabaseAccessor.getAllSubjectName(db, _grade, _depart);
        gSubjectName = DatabaseAccessor.getAllSubjectName(db, _grade, "ALL");

        Log.d("データ数","ALL:"+allSubjectName.size());
        Log.d("データ数","G:"+gSubjectName.size());

        //  存在しないデータについてはネットからダウンロードを行う
        if(allSubjectName.isEmpty()) task.addDownloadSrc(getTopUrl(_depart,null),_depart);
        if(_plusGMode && gSubjectName.isEmpty() ) task.addDownloadSrc(getTopUrl("ALL",null),"ALL");


        //  タスクの数が0以下ならデータを追加して終了
        if ( task.getSrcCount() <= 0){
            onEndTask();
        }

        //  タスクを実行する
        Log.d("","タスク実行" + task.getSrcCount());
        task.setTaskEndListener(new FetchSubjectListTask.TaskEndListener() {
            @Override
            public void onEndTask() {
                SubjectListActivity.this.onEndTask();
            }
        });
        task.execute("");

    }



    /**
     * データフェッチが終了したとき
     * アダプターにセットする
     *
     * DBをもう一回取得しなおす方式に変更
     *
     */
    private void onEndTask(){
    	Log.d("", "+++onEndTask()");
        //adapter.clear();
	    DatabaseHelper helper = new DatabaseHelper(this);
	    SQLiteDatabase db = helper.getReadableDatabase();
        allSubjectName = DatabaseAccessor.getAllSubjectName(db, _grade, _depart);
        gSubjectName = DatabaseAccessor.getAllSubjectName(db, _grade, "ALL");
        adapter = new SubjectListAdapter(this);

        if (!allSubjectName.isEmpty()) adapter.setSubjects(allSubjectName); //adapter.addAll(allSubjectName);
        if (_plusGMode && !gSubjectName.isEmpty() && !_depart.equals("ALL")) adapter.setSubjects(gSubjectName); // adapter.addAll(gSubjectName);
        listView.setAdapter(adapter);
        db.close();
    }

    /**
     * 教科リストがクリックされたとき
     * クリックされたTextViewを取得する
     *
     * 現在のモード別に起動するアクティビティーを変更する
     */
    private void onListView1ItemClick(AdapterView<?> parent,View view,int position,long id){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String subjectName = (String)parent.getItemAtPosition(position);
        String depart = pref.getString("depart",null);
        String grade = pref.getString("grade",null);
        int subjectId = DatabaseAccessor.getSubjectId(db,subjectName,grade,depart,-1);

        //  専門科に該当教科がない場合については一般科として扱う
        if(subjectId==-1&&_plusGMode){
            subjectId = DatabaseAccessor.getSubjectId(db,subjectName,grade,"ALL",-1);
            depart = "ALL";
        }

        switch(_mode){
            case MODE_SYLLABUS:
                String url = getTopUrl(depart,null);
                String code = DatabaseAccessor.getSyllabusCode(db,subjectId,null);

                String abs_path = url.substring(0,url.lastIndexOf('/'))+"/";    //  URLを絶対パスに変換
                abs_path = abs_path+code+".pdf";
                openPdf(abs_path);   //  シラバスを開く

                //Toast.makeText(this,code,Toast.LENGTH_SHORT).show();
                break;
            case MODE_POINT:    //  点数詳細画面
                openDetailActivity(subjectId,subjectName);
                break;
            case MODE_REGISTER:
                //  Toast.makeText(this,"No Implemented",Toast.LENGTH_SHORT).show();
                openRegistActivity(subjectId,subjectName);
                break;


        }
        db.close();


    }





    /**
     * 学科からURLを特定する
     * 学科はEE,Mなどの略表記
     */
    private String getTopUrl(String depart,String defValue){
        String[] urlArray = getResources().getStringArray(R.array.url_array);   //  学科URL表
        String[] departArray = getResources().getStringArray(R.array.depart_entryValues);   //学科表
        String url = defValue;
        for(int i=0; i<departArray.length; i++){
            if(depart.equals(departArray[i])){
                //  一致した番号がその学科のURL
                url = urlArray[i];  //  選択されている学科と表で一致した番号を取得
            }
        }
        return url;
    }


    /**
     * モードに応じて表示する色を変える
     * あとクリックラブルを外す
     */
    private void setModeDisplay(){

    }


    /**
     * 設定画面を開く
     */
    private void openConfigActivity(){
        Intent intent = new Intent(this,PrefActivity.class);
        this.startActivityForResult(intent,REQUEST_CODE_PREF);
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
     * @param subjectName 科目
     */
    private void openRegistActivity(int subjectId,String subjectName){

        Intent intent = new Intent(this,RegistActivity.class);
        intent.putExtra("subject_name",subjectName);
        intent.putExtra("subject_id",subjectId);
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

