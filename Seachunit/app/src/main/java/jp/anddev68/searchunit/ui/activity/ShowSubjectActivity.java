package jp.anddev68.searchunit.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.ui.adapter.PointsAdapter;

/**
 * Subjectの詳細を表示する
 * 詳細画面には登録した点数とシラバスを開く画面を用意する
 */
public class ShowSubjectActivity extends AppCompatActivity{

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    Toolbar toolbar;

    String subjectName;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_show_subject);

        subjectName = getIntent().getStringExtra("subject_name");

        initRecyclerView();
        initToolbar();
    }


    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(subjectName);
    }



}
