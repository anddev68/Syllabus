package jp.anddev68.searchunit;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;

import jp.anddev68.searchunit.database.DatabaseAccessor;
import jp.anddev68.searchunit.database.DatabaseHelper;


/**
 * 点数詳細アクティビティー
 *
 * Created by hideki on 2014/12/11.
 */
public class DetailActivity extends ActionBarActivity implements View.OnClickListener{

    String subjectName;
    int subjectId;

    TextView[] textViews;    //  点数表示テーブル
    TextView shortPointView;    //  不足点数用
    TextView subjectTextView;   //  教科名表示
    TextView unitTextView;      //  目標単位数
    RadioGroup radioGroup;     //  semesterチェック用

    SeekBar seekBar;
    int[] points;   //  点数データ


    //  widget
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  インテントで教科名と教科コードを渡す
        subjectName = getIntent().getStringExtra("subject_name");
        subjectId = getIntent().getIntExtra("subject_id",-1);

        setContentView(R.layout.activity_detail);

        //  findView
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toolbar = (Toolbar) findViewById(R.id.toolBar);

        //  ツールバーの設定
        toolbar.setNavigationIcon(R.drawable.icon);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(subjectName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //  Actionbarの設定
        setupActionBar();

        setupWidget();

    }

    @Override
    protected void onResume(){
        super.onResume();

        setupWidget();
    }



    private void setupWidget(){

        unitTextView = (TextView) findViewById(R.id.unit);
        shortPointView = (TextView) findViewById(R.id.textView10);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                unitTextView.setText(""+progress);
                //  不足点数をセットする
               setShortPointView(getShortPoint(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onChangeRadioButton(checkedId);
            }
        });




        textViews = new TextView[10];
        textViews[0] = (TextView) findViewById(R.id.text0_1);
        textViews[1] = (TextView) findViewById(R.id.text0_2);
        textViews[2] = (TextView) findViewById(R.id.text1_1);
        textViews[3] = (TextView) findViewById(R.id.text1_2);
        textViews[4] = (TextView) findViewById(R.id.text2_1);
        textViews[5] = (TextView) findViewById(R.id.text2_2);
        textViews[6] = (TextView) findViewById(R.id.text3_1);
        textViews[7] = (TextView) findViewById(R.id.text3_2);
        textViews[8] = (TextView) findViewById(R.id.text4_1);
        textViews[9] = (TextView) findViewById(R.id.text4_2);

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        points = new int[textViews.length];


        for(int i=0; i<textViews.length; i++){
            textViews[i].setOnClickListener(this);

            //  タームIDはtextViewの添字と一緒
            //  0=中間点数,1=中間max・・・
            int point = DatabaseAccessor.getPointValue(db,subjectId,i,-1);
            if( point!=-1){
                //  点数が存在したらそれを貼る
                points[i] = point;
                textViews[i].setText(""+point);
            }else{
                //  点数が存在しなければMAX100で作成する
                if(i%2==1 && i!=9){
                    DatabaseAccessor.insertPoint(db,subjectId,i,100);
                }
            }




        }

        //  不足点数をセットする
        setShortPointView(getShortPoint(seekBar.getProgress()));

    }





    /**
     * タームIDと科目名,教科IDを渡して開く
     * @param termId
     */
    private void openRegisterActivity(int termId){
        Intent intent = new Intent(this,RegistActivity.class);
        intent.putExtra("subject_id",subjectId);
        intent.putExtra("subject_name",subjectName);
        intent.putExtra("term_id",termId);
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        for(int i=0; i<textViews.length; i++){
            if( textViews[i].getId() == v.getId() ){
                openRegisterActivity(i);
                return;
            }
        }
    }


    /**
     * 不足点数を算出
     */
    private int getShortPoint(int unit){
        int max = 0;
        int current = 0;
        double per = 0.0;

        for(int i=0; i<points.length; i+=2){
            if(textViews[i].isEnabled())
                current+=points[i];
        }

        for(int i=1; i<points.length; i+=2){
            if(textViews[i].isEnabled())
                max += points[i];
        }

        //  10のみ95%に変更
        if(unit==10) per = 0.95;
        else per = (double)unit / 10.0;

        //  最低必要な点数 - 現在の点数
        //  切り上げ処理を行っています
        int r = (int)Math.ceil(max*per) - current;
        if(r<0) r = 0;

        return r;

    }

    /**
     * 点数をセット
     */
    private void setShortPointView(int point){
        if(point<=0)
            shortPointView.setText("CLEAR!!!");
        else
            shortPointView.setText(""+point);
    }


    /**
     *
     */
    private void onChangeRadioButton(int id){
        switch(id){
            case R.id.radioButton:
                changeSemester(0);
                return;
            case R.id.radioButton2:
                changeSemester(1);
                return;
            case R.id.radioButton3:
                changeSemester(2);
                return;


        }
    }


    /**
     * semesterの変更を行う
     * 該当テキストビューを無効化して計算から取り除く
     */
    private void changeSemester(int sem){
        int i;
        for(i=0; i<10; i++) {
            textViews[i].setEnabled(true);
        }

        switch(sem){
            case 0: //  通年
                break;
            case 1: //  前期
                for(i=4; i<8; i++) {
                    textViews[i].setEnabled(false);
                }
                break;
            case 2: //  後期
                for(i=0; i<4; i++) {
                    textViews[i].setEnabled(false);
                }
                break;

        }


        //  不足点数をセットする
        setShortPointView(getShortPoint(seekBar.getProgress()));

    }


    private TextView findTextView(int id){
        return (TextView) findViewById(id);
    }





    /**
     * ActionBarの設定
     */
    private void setupActionBar(){
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.drawable.ic_launcher,R.string.drawer_close){

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Log.i("DA", "onDrawerClosed");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.i("DA", "onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();




    }




}
