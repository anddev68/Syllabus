package jp.anddev68.searchunit.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
	Accesserクラス
	DBのインスタンスを用いてアクセスを可能にします
	別途SQLiteDatabaseのインスタンスを確保してください	

	staticメソッドしか持ちません
*/
public class DatabaseAccessor{

    /**
     * 学年,学科（String）をキーとして
     * 一致する全ての教科コードを返す
     *
     * 非推奨メソッド
     * 多分使うことはないでしょう
     */
    public static ArrayList<Integer> getAllSubjectId(SQLiteDatabase db,String grade,String depart){
        ArrayList<Integer> subjects = new ArrayList<>();
        String sql = "SELECT subject_id FROM subject WHERE "+
                "depart= " + depart +" AND grade= " +  grade + ";";
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return null;
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            subjects.add(id);
        }
        return subjects;

    }

    /**
     * 学年,学科,学科名（String）をキーとして
     * 教科コードを返す
     *
     */
    public static int getSubjectId(SQLiteDatabase db,String subjectName,String grade,String depart,int defValue){
        String sql = String.format(
                "SELECT subject_id FROM subject WHERE "+
                        "depart= '%s' AND grade='%s' AND subject_name='%s';",depart,grade,subjectName);

        //String sql = "SELECT subject_id FROM subject WHERE "+
               // "depart= '" + depart +"' AND grade= " +  grade + "subject_name="+ subjectName +";";
        Cursor cursor = tryRawQuery(db,sql);
        int id = defValue;
        if(cursor==null) return defValue;
        while(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        return id;

    }



    /**
     * 学年,学科をキーとして
     * 一致する全ての教科名を返す
     * @param db
     * @param grade
     * @param depart
     * @return
     */
    public static ArrayList<String> getAllSubjectName(SQLiteDatabase db,String grade,String depart){
        ArrayList<String> subjects = new ArrayList<>();
        String sql = String.format(
                "SELECT subject_name FROM subject WHERE "+
                        " depart='%s' AND grade='%s';",depart,grade);

        //String sql = "SELECT subject_name FROM subject WHERE "+
                //"depart= " + depart +" AND grade= " +  grade + ";";
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return null;
        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            subjects.add(name);
        }
        return subjects;

    }



    /**
     * 任意のタームの点数を取得する
     * 変更：教科コードをキーとして点数を取得する
     */
    public static int getPointValue(SQLiteDatabase db,int subjectId,int termId,int defValue){
        String sql = String.format("SELECT value FROM point WHERE subject_id =%d AND term_id=%d",
                subjectId,termId);
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return defValue;
        int id = defValue;
        while(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    /**
     * 任意のタームの点数を更新する
     * 変更：教科コードをキーとして点数を取得する
     */
    public static int updatePointValue(SQLiteDatabase db,int subjectId,int termId,int value){
        String sql = String.format("UPDATE point SET value = %d WHERE subject_id =%d AND term_id=%d",
                value,subjectId,termId);
        tryExecSql(db,sql);
        return 0;
    }


    /**
     * 教科を追加
     */
    public static int insertSubject(SQLiteDatabase db,String subjectName,String depart,String grade){
        String sql = String.format(
                "INSERT INTO subject(depart,grade,subject_name)"+
                        " VALUES('%s','%s','%s');",depart,grade,subjectName);
        tryExecSql(db,sql);
        return 0;
    }


    /**
     * シラバスを追加
     */
    public static int insertSyllabus(SQLiteDatabase db,String syllabusCode,int subjectId){
        String sql = String.format(
                "INSERT INTO syllabus(syllabus_code,subject_id)"+
                        " VALUES('%s',%d);",syllabusCode,subjectId);
        tryExecSql(db,sql);
        return 0;
    }

    /**
     * 任意のタームの点数を追加する
     */
    public static int insertPoint(SQLiteDatabase db,int subjectId,int termId,int value){
        String sql = String.format(
                "INSERT INTO point(subject_id,term_id,value)"+
                        " VALUES(%d,%d,%d);",subjectId,termId,value);
        tryExecSql(db,sql);
        return 0;
    }


    /**
     * シラバスコードを取得
     */
    public static String getSyllabusCode(SQLiteDatabase db,int subjectId,String defValue){
        String sql = String.format("SELECT syllabus_code FROM syllabus WHERE subject_id =%d",
                subjectId);
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return defValue;
        String id = defValue;
        while(cursor.moveToNext()){
            id = cursor.getString(0);
        }
        cursor.close();
        return id;
    }


    /**
     * エラー処理を加えたクエリ発行
     * @param db
     * @return null 失敗 : cursor 成功
     */
    private static Cursor tryRawQuery(SQLiteDatabase db,String sql){
        Cursor cursor = null;
        try{
            cursor = db.rawQuery(sql,null);
            Log.i("DBHelper", "exec:" + sql);
            return cursor;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


    /**
      insertなどの値が帰らないクエリの発行
     */
    private static void tryExecSql(SQLiteDatabase db,String sql){
       try{
           Log.i("DBHelper","exec:"+sql);
           db.execSQL(sql);
       }catch(Exception e){
           e.printStackTrace();
       }
    }









}