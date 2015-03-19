package jp.anddev68.searchunit;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
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
public class DetailActivity extends ActionBarActivity implements Toolbar.OnMenuItemClickListener{

    String subjectName;
    int subjectId;

    TextView[] textViews;    //  点数表示テーブル
    TextView shortPointView;    //  不足点数用
    TextView unitTextView;      //  目標単位数

    SeekBar seekBar;
    int[] points;   //  点数データ


    //  widget
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerListView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  インテントで教科名と教科コードを渡す
        subjectName = getIntent().getStringExtra("subject_name");
        subjectId = getIntent().getIntExtra("subject_id",-1);

        setContentView(R.layout.activity_detail);

        findViews();
        setupActionBar();
        setupWidget();

    }

    @Override
    protected void onResume(){
        super.onResume();

        setupWidget();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(this,"PRESS:"+item.getTitle(),Toast.LENGTH_SHORT).show();



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this,"PRESS:"+item.getTitle(),Toast.LENGTH_SHORT).show();

        return true;
    }


    private void findViews(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerListView = (ListView) findViewById(R.id.drawerListView);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        unitTextView = (TextView) findViewById(R.id.unit);
        shortPointView = (TextView) findViewById(R.id.textView10);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
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
    }

    private void setupWidget(){

        seekBar.setOnSeekBarChangeListener(new UnitSeekBarChangeListener());
        drawerListView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,new String[]{"通年","前期","後期"}));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        points = new int[textViews.length];

        PointTextClickListener listener = new PointTextClickListener();
        for(int i=0; i<textViews.length; i++){
            textViews[i].setOnClickListener(listener);

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

    private void setupActionBar(){
        toolbar.setNavigationIcon(R.drawable.icon);
        toolbar.setOnMenuItemClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(subjectName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.drawable.ic_launcher,R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
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
    /*
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
    */


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





    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String str = ((TextView)view).getText().toString();
            Log.d("ItemClick",str);
            switch(str){
                case "通年":
                    changeSemester(0);
                    break;
                case "前期":
                    changeSemester(1);
                    break;
                case "後期":
                    changeSemester(2);
                    break;
            }
            drawerLayout.closeDrawers();
        }
    }

    private class PointTextClickListener implements TextView.OnClickListener{

        @Override
        public void onClick(View v) {
            for(int i=0; i<textViews.length; i++){
                if( textViews[i].getId() == v.getId() ){
                    openRegisterActivity(i);
                    return;
                }
            }
        }
    }

    private class UnitSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
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
    }





    private TextView findTextView(int id){
        return (TextView) findViewById(id);
    }


}
