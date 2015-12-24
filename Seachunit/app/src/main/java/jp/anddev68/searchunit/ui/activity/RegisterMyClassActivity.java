package jp.anddev68.searchunit.ui.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.record.Subject;
import jp.anddev68.searchunit.ui.adapter.SubjectsAdapter;

/**
 * RegisterMyClassActivity.java
 * 受講授業登録用アクティビティー
 */
public class RegisterMyClassActivity extends AppCompatActivity{

    FragmentPagerAdapter fragmentPagerAdapter;
    FragmentManager fragmentManager;
    ViewPager viewPager;
    Button nextButton;
    Button prevButton;
    TextView titleTextView;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_register_my_class);
        titleTextView = (TextView) findViewById(R.id.title);

        fragmentManager = getSupportFragmentManager();
        fragmentPagerAdapter = new MyAdapter(fragmentManager);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentPagerAdapter);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNextButton();
            }
        });
        prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPrevButton();
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }


    /**
     * NextButtonを押したときの処理
     */
    private void onClickNextButton(){
        int page = viewPager.getCurrentItem();
        switch(page){
            case 0:
                //  次のページへ
                viewPager.setCurrentItem(1,true);
                onChangePageFrom1To2();
                //  Widgetの変更
                nextButton.setText("CLOSE");
                titleTextView.setText("追加する授業の確認");
                break;
            case 1:
                //  終了
                registerMyClass();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    /**
     * 戻るボタンを押したときの処理
     */
    private void onClickPrevButton(){
        int page = viewPager.getCurrentItem();
        switch(page){
            case 0:
                //  なにもせず終了
                setResult(RESULT_CANCELED);
                finish();
                break;
            case 1:
                //  Widgetの変更
                nextButton.setText("NEXT");
                titleTextView.setText("受講リストの作成");
                //  戻る
                viewPager.setCurrentItem(0,true);
                break;
        }
    }

    /**
     * ページを切り替えたときの処理
     * Fragment1からFragment2へ変数を渡す
     * Fragment2はAdapterを更新する
     */
    private void onChangePageFrom1To2(){
        Log.d("RegisterActivity","onChangePage");
        Fragment1 fragment1 = Fragment1.getInstance();
        int gradeId = fragment1.getGradeId();
        int departId = fragment1.getDepartId();
        Fragment2 fragment2 = Fragment2.getInstance();
        fragment2.initAdapter(gradeId,departId);
    }


    /**
     * 受講教科を追加するメソッド
     * 学年と学科を指定して一括で登録する
     * 2枚目のFragment終了時に呼ばれます
     */
    private void registerMyClass(){
        /* 学年と学科が一致するクラスを追加 */
        Long gradeId = (long)Fragment1.getInstance().getGradeId();
        Long departId = (long) Fragment1.getInstance().getDepartId();
        Long userId = 0l;
        String sql = getResources().getString(R.string.sql_insert_my_class_recursively);
        Object[] args = {userId,gradeId,departId};
        SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase();
        database.beginTransaction();
        database.execSQL(sql,args);
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }


    private class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return Fragment1.getInstance();
                default:
                    return Fragment2.getInstance();
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class Fragment1 extends Fragment{
        private static Fragment1 instance;
        public static Fragment1 getInstance(){
            if(instance==null) instance = new Fragment1();
            return instance;
        }

        View rootView;
        Spinner spinner1;
        Spinner spinner2;

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle bundle){
            rootView = inflater.inflate(R.layout.fragment_my_class_1,container,false);
            spinner1 = (Spinner) findViewById(R.id.spinner1);
            spinner2 = (Spinner) findViewById(R.id.spinner2);
            return rootView;
        }

        @Override
        public void onPause(){
            super.onPause();
            Log.d("onPause","Fragment-onPause");
        }

        public int getDepartId(){
            return spinner2.getSelectedItemPosition();
        }

        public int getGradeId(){
            return spinner1.getSelectedItemPosition();
        }


        private View findViewById(int id){
            return rootView.findViewById(id);
        }

    }

    public static class Fragment2 extends Fragment{
        private static Fragment2 instance;
        public static Fragment2 getInstance(){
            if(instance==null) instance = new Fragment2();
            return instance;
        }

        View rootView;
        RecyclerView recyclerView;
        SubjectsAdapter adapter;
        ArrayList<Subject> subjects;

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle bundle){
            rootView = inflater.inflate(R.layout.fragment_my_class_2,container,false);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            return rootView;
        }

        @Override
        public void onPause(){
            super.onPause();
            Log.d("onPause","Fragment-onPause");
        }

        public void initAdapter(int gradeId,int departId){
            subjects = new ArrayList<>();
            SQLiteDatabase database = new DatabaseHelper(getActivity()).getReadableDatabase();
            String sql = getResources().getString(R.string.sql_query_subjects);
            String[] args = {""+gradeId,""+departId};
            Cursor cursor = database.rawQuery(sql,args);
            while(cursor.moveToNext()){
                subjects.add(new Subject(cursor));
            }
            Log.d("Adapter size", "" + subjects.size());
            adapter = new SubjectsAdapter(getActivity(),null,subjects);
            recyclerView.setAdapter(adapter);
            database.close();
        }



        private View findViewById(int id){
            return rootView.findViewById(id);
        }
    }


}
