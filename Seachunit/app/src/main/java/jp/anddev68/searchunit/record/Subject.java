package jp.anddev68.searchunit.record;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;

/**
 * 教科モデル
 */
public class Subject extends Record{
    public int id;
    public String name;
    public String url; //  シラバスへのurl
    public int gradeId;
    public int departId;

    /* デフォルトコンストラクタ */
    public Subject(String name,String url,int grade,int departId){
        this.id = -1;
        this.name = name;
        this.url = url;
        this.gradeId = grade;
        this.departId = departId;
    }

    /* Databaseからcursorで初期化 */
    public Subject(Cursor cursor){
        this.id = cursor.getInt(0);
        this.name = cursor.getString(1);
        this.url = cursor.getString(2);
        this.gradeId = cursor.getInt(3);
        this.departId = cursor.getInt(4);
    }



    @Override
    protected String getTableName() {
        return "subject";
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        /* idがある場合はidを入れる */
        if(this.id!=-1) values.put("_id", this.id);
        values.put("grade_id",this.gradeId);
        values.put("depart_id",this.departId);
        values.put("name",this.name);
        values.put("url",this.url);
        return values;
    }



}
