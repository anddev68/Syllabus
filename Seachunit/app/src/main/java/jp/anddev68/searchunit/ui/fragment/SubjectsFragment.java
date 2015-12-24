package jp.anddev68.searchunit.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import jp.anddev68.searchunit.MainActivity;
import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.database.DatabaseHelper;
import jp.anddev68.searchunit.record.MyClass;
import jp.anddev68.searchunit.record.Subject;
import jp.anddev68.searchunit.task.SubjectsDownloader;
import jp.anddev68.searchunit.ui.activity.ShowSubjectActivity;
import jp.anddev68.searchunit.ui.adapter.SubjectsAdapter;

/**
 * フラグメント管理に変更
 * 教科一覧を表示するためのフラグメント
 */
public class SubjectsFragment extends Fragment{
    private SubjectsFragment instance;
    public SubjectsFragment getInstance(){
        if(instance==null) instance = new SubjectsFragment();
        return instance;
    }

    RecyclerView recyclerView;
    View rootView;
    AlertDialog dialog;

    /* データベース接続用 */
    SQLiteDatabase database;
    DatabaseHelper dbHelper;

    /* アダプターに表示している教科データ */
    ArrayList<Subject> subjects;
    SubjectsAdapter subjectsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle bundle){
        rootView = inflater.inflate(R.layout.fragment_subjects,container,false);

        /* Widgetの初期化 */
        initRecyclerView();

        /* データベースからデータを取ってくる */
        initDatabase();

        /* 一度ダウンロードしてある場合はダウンロードを省略する*/
        /* アダプターをキャッシュで初期化する */
        /* キャッシュで初期化できない場合は教科データのDLを行ってからセットする */
        if(!initAdapter()){
            startDownloadingSubjects();
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        database.close();
        super.onDestroy();
    }


    /**
     * 教科リストをダウンロードするタスクを開始する
     * プログレスダイアログを開く
     */
    private void startDownloadingSubjects(){
        /* 取得できない場合はデータがないと判断して教科リストをDLする */
        /* 時間がかかるのでダイアログを表示 */
        /* このダイアログはendDownloadingで閉じます */
        dialog = new AlertDialog.Builder(getActivity())
                .setTitle("注意").setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("初期データをダウンロードします。\nダウンロードには時間がかかります。\nアプリを終了させないでください。")
                .show();

        /* ダウンロードタスクを開始する */
        Log.d("SubjectsFragment","教科一覧ダウンロード開始");
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
     * データを表示する
     * プログレスダイアログが開いていれば閉じる
     */
    private void endDownloadingSubjects(ArrayList<Subject> subjects){
        /* データベースへ放り投げる */
        try {
            database.beginTransaction();
            for (Subject subject : subjects) {
                subject.insert(database);
                Log.d("SubjectsFragment", subject.toString());
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }catch(Exception e){
            e.printStackTrace();
        }
        /* 今ダウンロードしてきたデータを表示させる */
        initAdapter();

        /* ダイアログを消す */
        if(dialog!=null) {
            dialog.dismiss();
        }
    }


    /**
     * RecyclerViewのセットアップ
     */
    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d("", "OnMove");
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d("", "OnSwiped");
                int pos = viewHolder.getAdapterPosition();
                Subject subject = subjects.remove(pos);   //  データを削除
                subjectsAdapter.notifyDataSetChanged(); //  アダプターを呼ぶ
                //  データベースから削除
                String sql = getResources().getString(R.string.sql_delete_my_class);
                String[] args = {""+subject.id,"0"}; //  1個目の引数:教科ID,2個目の引数:ユーザID
                database.beginTransaction();
                database.execSQL(sql,args);
                database.setTransactionSuccessful();
                database.endTransaction();
                printMyClass(0L);
                Log.d("id",""+subject.id);
            }
        });
        helper.attachToRecyclerView(recyclerView);


    }


    /**
     * AdapterViewのセットアップ
     * Databaseから教科一覧を取得し、アダプターに張り付ける
     * @return true
     *  Databaseからの教科一覧取得に成功した場合にtrueを返す
     *  取得した教科一覧を用いてアダプターを作成
     *  アダプターをviewにセットする
     * @return false
     *  取得できない場合はこのメソッドがfalseを返す
     */
    public boolean initAdapter(){
        /* 受講している教科一覧を取得する */
        /* TODO:ユーザが受講しているものだけ表示する */
        /* ユーザで切り替える場合は服問い合わせのwhere句にANDでuser_id==を追加したらいいんじゃないでしょうか */
        String sql = "SELECT * FROM subject WHERE EXISTS ( SELECT * FROM my_class WHERE subject._id = my_class.subject_id)";
        Cursor cursor = database.rawQuery(sql,null);
        subjects = new ArrayList<>();
        while(cursor.moveToNext()){
            Log.d("query",cursor.toString());
            subjects.add(new Subject(cursor));
        }
        if(subjects.isEmpty()) return false; /* データなし */

        subjectsAdapter = new SubjectsAdapter(getActivity(), new SubjectsAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View v, int index) {
                /* 教科をクリックしたときの処理 */
                Subject subject = subjects.get(index);
                Log.d("",""+index);
                Intent intent = new Intent(getActivity(), ShowSubjectActivity.class);
                intent.putExtra("subject_name",subject.name);
                startActivity(intent);
            }
        },subjects);
        recyclerView.setAdapter(subjectsAdapter);
        return true;
    }


    /**
     * PDFを開く
     */
    public void openPDF(Subject subject){
        Uri uri = Uri.parse("http://docs.google.com/viewer?url="+subject.url);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }




    /**
     * データベースを初期化する
     */
    private void initDatabase(){
        dbHelper = new DatabaseHelper(getActivity());
        database = dbHelper.getWritableDatabase();
    }




    //
    //  ---------------------------------------------------------------------------------------------
    //  データベース操作用メソッド
    //

    /**
     * 受講教科を追加するメソッド
     * 学年と学科を指定して一括で登録する
     */
   private void registerMyClass(Long gradeId,Long departId,Long userId){
       /* 学年と学科が一致するクラスを追加 */
        String sql = getResources().getString(R.string.sql_insert_my_class_recursively);
        Object[] args = {userId,gradeId,departId};
        database.beginTransaction();
        database.execSQL(sql,args);
        database.setTransactionSuccessful();
        database.endTransaction();
   }



    /**
     * 出力
     */
    private void printMyClass(Long userId){
        String sql = "SELECT * FROM my_class WHERE user_id = " + userId;
        Cursor cursor = database.rawQuery(sql,null);
        while(cursor.moveToNext()){
            MyClass myclass = new MyClass(cursor);
            Log.d("printMyClass()",myclass.toString());
        }
    }

    private void printSubject(Long gradeId,Long departId){
        String sql = "SELECT * FROM subject WHERE depart_id =? AND grade_id = ?";
        String[] args = {""+departId,""+gradeId};
        Cursor cursor = database.rawQuery(sql,args);
        while(cursor.moveToNext()){
            Subject subject = new Subject(cursor);
            Log.d("printSubject()",subject.toString());
        }
    }




    //
    //  データベース操作用メソッドここまで
    //  --------------------------------------------------------------------------------------------------
    //




    /**
     * findViewByIdのラッパー
     * @param id
     * @return
     */
    private View findViewById(int id){
        return rootView.findViewById(id);
    }



}
