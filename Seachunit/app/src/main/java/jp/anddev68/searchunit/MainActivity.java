package jp.anddev68.searchunit;

/**
 * Created by Administrator on 2015/11/20.
 */

import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.record.Subject;
import jp.anddev68.searchunit.ui.activity.RegisterMyClassActivity;
import jp.anddev68.searchunit.ui.activity.ShowSubjectActivity;
import jp.anddev68.searchunit.ui.fragment.SubjectsFragment;
import jp.anddev68.searchunit.widget.drawer.DrawerAdapter;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    FloatingActionButton floatingActionButton;
    SubjectsFragment fragment;

    final int REQUEST_REGISTER_MY_CLASS = 0;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        initDatabase();
        initToolbar();
        initDrawer();
        initFAB();

        fragment = new SubjectsFragment();
        FragmentTransaction transaction  = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject_list, menu);
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_REGISTER_MY_CLASS:
                if(resultCode==RESULT_OK){
                    //  AdapterViewの再描画
                    fragment.initAdapter();
                }
        }
    }

    /**
     *  Drawerを初期化する
     */
    private void initDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);

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
     * ここでデータベースを初期化しておく
     * 1回特になにもせず、呼ぶだけの処理
     */
    private void initDatabase(){
        SQLiteDatabase database  = new DatabaseHelper(this).getReadableDatabase();
        database.close();
    }

    /**
     * FloatingActionButtonを初期化する
     * ボタンを押したときに受講登録Activityを開く
     */
    private void initFAB(){
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterMyClassActivity();
            }
        });
    }


    private void openRegisterMyClassActivity(){

        // スケールアップ用 ActivityOptions をインスタンス化
        ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(
                floatingActionButton,
                0, 0, floatingActionButton.getWidth(), floatingActionButton.getHeight());

        // アニメーションを指定してアクティビティを起動
        startActivityForResult(new Intent(this, RegisterMyClassActivity.class), REQUEST_REGISTER_MY_CLASS,opts.toBundle());

    }


}