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

import jp.anddev68.searchunit.database.DatabaseAccessor;
import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.parser.AbstractParser;
import jp.anddev68.searchunit.parser.GnctParser;
import jp.anddev68.searchunit.structure.Subject;

/**
	教科一覧のアクティビティー
*/

public class SubjectListActivity extends Activity {

    private static final int MODE_POINT = 1;    //  点数表示モード
    private static final int MODE_REGISTER = 2;   //  点数登録モード
    private static final int MODE_SYLLABUS = 3;     //  シラバス閲覧モード
    private static final int NON_SELECT_COLOR = Color.rgb(0x99,0x99,0x99);
    private static final int SELECT_COLOR = Color.rgb(0x55,0x55,0x55);
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

    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

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
        textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.subject_list);
        rootView =(LinearLayout) findViewById(R.id.root);
        configText = (TextView) findViewById(R.id.system_mes);

        configText.setText("現在の設定:"+_grade+_depart+(_plusGMode?" +一般科":""));

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

        //  アダプター初期化
        adapter = new ArrayAdapter<String>(this,R.layout.custom_list_item1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubjectListActivity.this.onListView1ItemClick(parent,view,position,id);
            }
        });
        listView.setAdapter(adapter);

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
        adapter.clear();
	    DatabaseHelper helper = new DatabaseHelper(this);
	    SQLiteDatabase db = helper.getReadableDatabase();
        allSubjectName = DatabaseAccessor.getAllSubjectName(db, _grade, _depart);
        gSubjectName = DatabaseAccessor.getAllSubjectName(db, _grade, "ALL");
        if (!allSubjectName.isEmpty()) adapter.addAll(allSubjectName);
        if (_plusGMode && !gSubjectName.isEmpty() && !_depart.equals("ALL")) adapter.addAll(gSubjectName);
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
        for(int i=0; i<buttonLayouts.length; i++){
            buttonLayouts[i].setBackgroundColor(NON_SELECT_COLOR);
            buttonLayouts[i].setClickable(true);
        }


        switch(_mode) {
            case MODE_POINT:
                textView2.setText("教科一覧[点数表示]");
                textView2.setBackgroundResource(R.drawable.btn039_01);
                rootView.setBackgroundResource(R.drawable.repeat_bg100_01);
                buttonLayouts[0].setClickable(false);
                buttonLayouts[0].setBackgroundColor(SELECT_COLOR);
                break;
            case MODE_REGISTER:
                textView2.setText("教科一覧[点数登録]");
                //textView2.setBackgroundColor(Color.rgb(0,200,0));
                textView2.setBackgroundResource(R.drawable.btn039_04);
                rootView.setBackgroundResource(R.drawable.repeat_bg100_04);
                buttonLayouts[1].setBackgroundColor(SELECT_COLOR);
                buttonLayouts[1].setClickable(false);
                break;
            case MODE_SYLLABUS:
                textView2.setText("教科一覧[シラバス]");
                textView2.setBackgroundResource(R.drawable.btn039_05);
                rootView.setBackgroundResource(R.drawable.repeat_bg100_05);
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

