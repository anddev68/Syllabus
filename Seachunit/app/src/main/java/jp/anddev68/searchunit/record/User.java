package jp.anddev68.searchunit.record;

import android.content.ContentValues;

/**
 * データベースと連携したクラス
 */
public class User extends Record{
    int id;
    String name;
    int gradeId;    //  学年
    int departId;   //  学科

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    protected ContentValues getContentValues() {
        return null;
    }
}
