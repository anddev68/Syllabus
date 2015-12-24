package jp.anddev68.searchunit.record;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * データベースと連携したクラス(Railsでいうところのモデル）
 */
public abstract class Record {

    protected abstract String getTableName();
    protected abstract ContentValues getContentValues();

    public final void insert(SQLiteDatabase db){
        db.insert(getTableName(),null,getContentValues());
    }



    @Override
    public String toString(){
        return getContentValues().toString();
    }



}
