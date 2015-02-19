package jp.anddev68.searchunit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import jp.anddev68.searchunit.database.DatabaseHelper;

/**
 * 設定ボタンを押した時に開くアクティビティー
 * Created by hideki on 2014/12/10.
 */
public class PrefActivity extends PreferenceActivity{

    private static final String TAG = "PrefActivity";

    ListPreference _box_depart;
    ListPreference _box_grade;

    private Button _button;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.setContentView(R.layout.pref_activity);
        this.addPreferencesFromResource(R.xml.pref);

        //  二回目以降は開かないなら開かない
        /*
        boolean non_display = _sharedPref.getBoolean("setting_display",false);
        if(non_display){
            Intent intent = new Intent(this,SubjectListActivity.class);
            this.startActivity(intent);
        }*/


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


        _button = (Button) this.findViewById(R.id.button);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(v);
            }
        });



        //  サマリーを変更
        setSummary();

        //String def1 = _sharedPref.getString("depart",null);
        //String def2 = _sharedPref.getString("grade",null);
        //if(def1!=null) _box_grade.setDefaultValue(def1);
       // if(def2!=null) _box_depart.setDefaultValue(def2);

    }


    /**
     * 決定ボタンが押されたときの処理
     * @param v
     */
    private void onClickButton(View v){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String grade = sharedPreferences.getString("grade",null);
        String depart = sharedPreferences.getString("depart",null);

        //  整合性チェック：失敗
        if(grade==null || depart==null){
            new AlertDialog.Builder(this)
            .setTitle("入力エラー")
            .setMessage("正しい内容を入力してください")
            .setNegativeButton("OK", null)
            .show();
            return;
        }

        //  値を保存
        Log.i(TAG, "以下のデータが選択されました");
        Log.i(TAG,grade);
        Log.i(TAG,depart);

        //  prefに設定
        //SharedPreferences.Editor editor = _sharedPref.edit();
        //editor.putString("depart", depart);
        //editor.putString("grade",grade);
        //editor.commit();

        //  終了
        this.setResult(RESULT_OK);
        this.finish();

    }


    private void setSummary(){
        //  2回目以降は選択されているものをデフォルトにする
        //  サマリーを変更
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String grade = sharedPreferences.getString("grade",null);
        String depart = sharedPreferences.getString("depart",null);
        if(depart!=null)
            _box_depart.setSummary(depart);
        if(grade!=null)
            _box_grade.setSummary(grade);
    }




}
