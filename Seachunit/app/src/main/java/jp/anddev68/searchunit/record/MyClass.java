package jp.anddev68.searchunit.record;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 受講モデル
 */
public class MyClass extends Record{
    int id;
    int userId;
    int subjectId;

    public MyClass(Cursor c){
        id = c.getInt(0);
        userId = c.getInt(1);
        subjectId = c.getInt(2);
    }

    @Override
    protected String getTableName() {
        return "my_class";
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("_id",id);
        values.put("user_id",userId);
        values.put("subject_id",subjectId);
        return values;
    }

    @Override
    public String toString(){
        return getContentValues().toString();
    }
}
