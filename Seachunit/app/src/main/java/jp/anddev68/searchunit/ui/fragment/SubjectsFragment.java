package jp.anddev68.searchunit.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import jp.anddev68.searchunit.MainActivity;
import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.record.Subject;
import jp.anddev68.searchunit.task.SubjectsDownloader;
import jp.anddev68.searchunit.ui.adapter.SubjectListAdapter;
import jp.anddev68.searchunit.ui.adapter.SubjectsAdapter;

/**
 * フラグメント管理に変更
 * 教科一覧を表示するためのフラグメント
 */
public class SubjectsFragment extends Fragment{


    RecyclerView recyclerView;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle bundle){
        rootView = inflater.inflate(R.layout.fragment_subjects,container,false);
        initRecyclerView();

        /* データベースからデータを取ってくる */


        /* 取得できない場合はデータがないと判断して教科リストをDLする */
        /* 時間がかかるので注意 */
        new AlertDialog.Builder(getActivity())
                .setTitle("注意").setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("初期データをダウンロードします。\nダウンロードには時間がかかります。\nアプリを終了させないでください。")
                .show();

        return rootView;
    }


    /**
     * 教科リストをダウンロードするタスクを開始する
     * プログレスダイアログを開く
     */
    private void startDownloadingSubjects(){
        /* ダウンロードタスクを開始する */
        getLoaderManager().initLoader(0,null,new LoaderManager.LoaderCallbacks<ArrayList<Subject>>() {
            @Override
            public Loader<ArrayList<Subject>> onCreateLoader(int i, Bundle bundle) {
                Loader loader = new SubjectsDownloader(getActivity());
                loader.forceLoad();
                return loader;
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Subject>> objectLoader, ArrayList<Subject> o) {
                endDownloadingSubjects(o);
            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Subject>> objectLoader) {

            }
        });

    }


    /**
     * ダウンロードするタスクが終了したときに呼ばれる
     * ダウンロードしてきたデータをデータベースに突っ込む
     * プログレスダイアログが開いていれば閉じる
     */
    private void endDownloadingSubjects(ArrayList<Subject> subjects){
        /* データベースへ放り投げる */

    }


    /**
     * RecyclerViewのセットアップ
     */
    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(0, "卒業研究", null,0,0));
        subjects.add(new Subject(1, "ドイツ語", null,0,0));

        SubjectsAdapter adapter = new SubjectsAdapter(getActivity(), subjects);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d("", "OnMove");
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();

                Log.d("", "OnSwiped");
            }
        });
        helper.attachToRecyclerView(recyclerView);

    }

    /**
     * AdapterViewのセットアップ
     */

    /**
     * データベースチェック
     */



    /**
     * findViewByIdのラッパー
     * @param id
     * @return
     */
    private View findViewById(int id){
        return rootView.findViewById(id);
    }



}
