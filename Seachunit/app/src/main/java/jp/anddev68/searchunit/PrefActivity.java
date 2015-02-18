package jp.anddev68.searchunit;

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

    SharedPreferences _sharedPref;
    SQLiteDatabase _db;
    ListPreference _box_depart;
    ListPreference _box_grade;
    ListPreference _box_school;
    CheckBoxPreference _checkBox1;
    private Button _button;

    //  ボックスで選択されたものに対応するID
    //  決定ボタン押下時にprefに保存
    int _id_grade;
    int _id_school;
    int _id_depart;
    String _name_grade;
    String _name_school;
    String _name_depart;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.setContentView(R.layout.pref_activity);
        this.addPreferencesFromResource(R.xml.pref);

        _sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //  二回目以降は開かないなら開かない
        /*
        boolean non_display = _sharedPref.getBoolean("setting_display",false);
        if(non_display){
            Intent intent = new Intent(this,SubjectListActivity.class);
            this.startActivity(intent);
        }*/


        DatabaseHelper helper = DatabaseHelper.getInstance(getApplicationContext());
        _db = helper.getWritableDatabase();
        setupWidgets();

    }

    private void setupWidgets(){
        _box_depart = (ListPreference) this.findPreference("depart_name");
        _box_depart.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        return onChangedComboBox2((String)newValue);
                    }
                });
        _box_school = (ListPreference) this.findPreference("school_name");
        _box_school.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        return onChangedComboBox1((String)newValue);
                    }
                });
        _box_grade = (ListPreference) this.findPreference("grade_name");
        _box_grade.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        return onChangedComboBox3((String) newValue);
                    }
                });
        _button = (Button) this.findViewById(R.id.button);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickButton(v);
            }
        });

        _checkBox1 = (CheckBoxPreference) this.findPreference("setting_display");

        //  データベースから取得する予定です
        //  現在は岐阜高専のみ対応
        CharSequence[] cs = {"岐阜高専"};
        _box_school.setEntryValues(cs);
        _box_school.setEntries(cs);

        _box_depart.setEnabled(false);
        _box_grade.setEnabled(false);

        //  2回目以降は選択されているものをデフォルトにする
        String val = _sharedPref.getString("school_name",null);
        String val2 = _sharedPref.getString("depart_name",null);
        String val3 = _sharedPref.getString("grade_name",null);
        if(val==null){
            onChangedComboBox1(val);
            onChangedComboBox2(val2);
            onChangedComboBox3(val3);
        }

    }


    /**
     * 決定ボタンが押されたときの処理
     * @param v
     */
    private void onClickButton(View v){
        //  prefに設定
        SharedPreferences.Editor editor = _sharedPref.edit();
        editor.putInt("depart_id",_id_depart);
        editor.putInt("grade_id",_id_grade);
        editor.putInt("school_id",_id_school);
        editor.putString("depart_name", _name_depart);
        editor.putString("grade_name",_name_grade);
        editor.putString("school_name",_name_school);
        editor.putBoolean("setting_display",_checkBox1.isChecked());
        editor.commit();

        //  出力
        Log.i(TAG, "以下のデータが選択されました");
        Log.i(TAG,"学校ID:"+_id_school +" 学校名:"+_name_school);
        Log.i(TAG,"学科ID:"+_id_depart +" 学科名:"+_name_depart);
        Log.i(TAG,"学年ID:"+_id_grade +" 学年名:"+_name_grade);

        this.setResult(RESULT_OK);
        this.finish();

    }

    /**
     * 学校名が選択されたとき
     * その学校名をキーとしてデータベースから学科を作成
     */
    private boolean onChangedComboBox1(String newValue){
        this._name_school = newValue;
        this._box_school.setSummary(newValue);
        this._id_school = DatabaseHelper.getSchoolId(_db,_name_school,-1);

        //  学科一覧をデータベースから取得
        CharSequence[] cs = DatabaseHelper.getDepartNameList(_db,_id_school).toArray(new CharSequence[0]);
        _box_depart.setEntryValues(cs);
        _box_depart.setEntries(cs);
        _box_depart.setEnabled(true);
        return true;
    }

    /**
     * 学科名が選択されたとき
     * 学年名を作成
     */
    private boolean onChangedComboBox2(String newValue){
        this._name_depart = newValue;
        this._box_depart.setSummary(newValue);
        this._id_depart = DatabaseHelper.getDepartId(_db,_id_school,_name_depart,-1);

        //  学科名も(ry
        CharSequence[] cs = {"1年","2年","3年","4年","5年"};
        _box_grade.setEntryValues(cs);
        _box_grade.setEntries(cs);
        _box_grade.setEnabled(true);

        return true;


    }


    private boolean onChangedComboBox3(String newValue){
        this._name_grade = newValue;
        this._box_grade.setSummary(newValue);
        this._id_grade = DatabaseHelper.getGradeId(_db,_name_grade,-1);
        return true;
    }


    /**
     * toString()メソッドでEntriesを作成する
     */
    private CharSequence[] toEntries(ArrayList list){
        CharSequence[] cs = new CharSequence[list.size()];
        for(int i=0; i<list.size(); i++){
            cs[i] = list.toString();
        }
        return cs;
    }


}
