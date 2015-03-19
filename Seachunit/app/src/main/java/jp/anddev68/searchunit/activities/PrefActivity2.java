package jp.anddev68.searchunit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.SubjectListActivity;
import jp.anddev68.searchunit.database.DatabaseHelper;

/**
 * 設定画面
 * 3.x以上のバージョン用に変更してあります
 *
 * Created by anddev68 on 15/03/16.
 */
public class PrefActivity2 extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        getFragmentManager().beginTransaction().replace(R.id.content_frame,new PrefFragment()).commit();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("設定");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            setResult(SubjectListActivity.RESULT_CANCELED);
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public static class PrefFragment extends PreferenceFragment{

        ListPreference _box_depart;
        ListPreference _box_grade;

        Preference _deletePointButton;
        Preference _deleteSubjectButton;
        Preference _guideButton;

        Preference _license;
        ActionBarActivity _activity;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);

            _activity = (ActionBarActivity) getActivity();

            setupWidgets();

        }

        private void setupWidgets(){
            //  ウィジェットを取得
            _box_depart = (ListPreference) this.findPreference("depart");
            _box_depart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue!=null)
                        _box_depart.setSummary(newValue.toString());
                    return true;
                }
            });
            _box_grade = (ListPreference) this.findPreference("grade");
            _box_grade.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue!=null)
                        _box_grade.setSummary(newValue.toString());
                    return true;
                }
            });
            _deletePointButton = this.findPreference("dlPoint");
            _deleteSubjectButton = this.findPreference("dlSubject");

            _deleteSubjectButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    onClickDelSubjectTable();
                    return true;
                }
            });


            _license = this.findPreference("osp");
            _license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //  ライセンス条文の表示
                    onClickLicense();
                    return true;
                }
            });

            _guideButton = findPreference("guide");
            _guideButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    _activity.setResult(SubjectListActivity.RESULT_FIRST_USER);
                    _activity.finish();
                    return true;
                }
            });

            //  サマリーを変更
            setSummary();


        }


        private void setSummary(){
            //  2回目以降は選択されているものをデフォルトにする
            //  サマリーを変更
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_activity);
            String grade = sharedPreferences.getString("grade",null);
            String depart = sharedPreferences.getString("depart",null);
            if(depart!=null)
                _box_depart.setSummary(depart);
            if(grade!=null)
                _box_grade.setSummary(grade);
        }


        /**
         * ライセンスが押された時
         */
        private void onClickLicense(){
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getResources().getAssets().open("license")));
                while((line=br.readLine())!=null){
                    sb.append(line);
                    sb.append("\n");
                }
                br.close();

                ScrollView scrollView = new ScrollView(_activity);
                TextView textView = new TextView(_activity);
                textView.setText(sb.toString());
                scrollView.addView(textView);

                AlertDialog dialog = new AlertDialog.Builder(_activity)
                        .setTitle("License")
                        .setView(scrollView).create();
                dialog.show();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }



        /**
         * 教科テーブルの全削除
         */
        private void onClickDelSubjectTable(){
            new AlertDialog.Builder(_activity)
                    .setTitle("確認")
                    .setMessage("消去します。よろしいですか？")
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteSubjectTable();
                            Toast.makeText(_activity, "消去成功！", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("CANCEL",null)
                    .show();
        }
        private void deleteSubjectTable(){
            SQLiteDatabase db = new DatabaseHelper(_activity).getWritableDatabase();
            db.delete("subject",null,null);
            db.delete("syllabus",null,null);
            db.close();
        }

    }


}
