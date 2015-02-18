package jp.anddev68.searchunit.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jp.anddev68.searchunit.structure.School;
import jp.anddev68.searchunit.structure.Subject;

/**
 * Created by hideki on 2014/12/10.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    public static String DB_NAME = "data3.db";
    public static int DB_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        _context = context;
    }

    /**
     * シングルトン用
     */
    private static DatabaseHelper _helper;
    public static DatabaseHelper getInstance(Context context){
        if(_helper==null) {
            _helper = new DatabaseHelper(context);
        }
        return _helper;
    }


    private Context _context;

    /**
     * 学校名を指定してIDを取得する
     * @param db データベース
     * @param name 学校名
     * @return 失敗:-1 取得:そのID
     */
    public static int getSchoolId(SQLiteDatabase db,String name){
        return -1;
    }




    /**
     * 学科IDと学年IDを指定して一致したすべての教科IDおよび教科名を返す
     * @param db
     * @param departId
     * @param gradeId
     */
    public static ArrayList<Subject> getSubjectList(SQLiteDatabase db,int departId,int gradeId){
        ArrayList<Subject> subjects = new ArrayList<>();
        String sql = "SELECT subject_id,subject_name FROM subject WHERE "+
                "depart_id= " + departId +" AND grade_id= " + gradeId + ";";
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return subjects;
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Subject subject = new Subject(id,name);
            subjects.add(subject);
        }

        return subjects;
    }

    /**
     * 学科IDと学年IDと科目名を指定して教科IDを取得する
     */
    public static int getSubjectId(SQLiteDatabase db,int departId,int gradeId,int defValue){
        String sql = String.format(
            "SELECT subject_id FROM subject WHERE depart_id=%d AND grade_id =%d",
                departId,gradeId);
        Cursor cursor = tryRawQuery(db, sql);
        if(cursor==null) return defValue;
        int id = defValue;
        while(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    /**
     * 教科IDを指定してシラバスのURLを取得する
     */
    public static String getSyllabusUrl(SQLiteDatabase db,int subjectId,String defValue) {
        String sql = "SELECT syllabus_url FROM syllabus WHERE subject_id = " + subjectId + ";";
        Cursor cursor = tryRawQuery(db, sql);
        if (cursor == null) return defValue;
        String str = defValue;
        while (cursor.moveToNext()) {
            str = cursor.getString(0);
        }
        cursor.close();
        return str;
    }

    /**
     * 任意のタームの点数を取得する
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
     * 学校IDと学校名のリストを返す
     * @param db
     */
    public static ArrayList<School> getSchoolList(SQLiteDatabase db){
        ArrayList<School> schools = new ArrayList<>();
        String sql = "SELECT subject_id,subject_name FROM school";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            School s = new School(id,name);
            schools.add(s);
        }

        return schools;
    }

    /**
     * 学校名でIDを逆引き
     * @param db
     */
    public static int getSchoolId(SQLiteDatabase db,String name,int defValue){
        String sql = "SELECT school_id FROM school WHERE school_name = '"+name+"';";
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
     * IDから学科名を引き
     */
    public static String getDepartName(SQLiteDatabase db,int departId,String defValue){
        String sql = "SELECT depart_name FROM depart WHERE depart_id = "+departId+";";
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return defValue;
        String str = defValue;
        while(cursor.moveToNext()){
            str = cursor.getString(0);
        }
        cursor.close();
        return str;
    }

    /**
     * IDから学科TOPを引く
     */
    public static String getDepartUrl(SQLiteDatabase db,int departId,String defValue){
        String sql = "SELECT depart_url FROM depart WHERE depart_id = "+departId+";";
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return defValue;
        String str = defValue;
        while(cursor.moveToNext()){
            str = cursor.getString(0);
        }
        cursor.close();
        return str;
    }


    /**
     * 学科IDを逆引きする
     * @param db
     */
    public static int getDepartId(SQLiteDatabase db,int schoolId,String departName,int defValue){
        String sql = "SELECT depart_id FROM depart "+
                "WHERE school_id = "+ schoolId + " AND depart_name = '"+departName+"';";
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
     * 学科名の一覧を取得する
     * @return 失敗 空のリスト
     */
    public static ArrayList<String> getDepartNameList(SQLiteDatabase db,int schoolId){
        ArrayList<String> nameList = new ArrayList<>();
        String sql = "SELECT depart_name FROM depart "
                +"WHERE school_id =" + schoolId;
        Cursor cursor = tryRawQuery(db,sql);
        if(cursor==null) return nameList;
        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            nameList.add(name);
        }


        return nameList;
    }

    /**
     * 学年IDを取得する
     */
    public static int getGradeId(SQLiteDatabase db,String gradeName,int defValue){
        String sql = "SELECT grade_id FROM grade "+
                "WHERE grade_name = '"+gradeName+"';";
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
     * 教科を追加
     */
    public static int insertSubject(SQLiteDatabase db,String subjectName,int departId,int gradeId){
        String sql = String.format(
                "INSERT INTO subject(depart_id,grade_id,subject_name)"+
                " VALUES(%d,%d,'%s');",departId,gradeId,subjectName);
        tryExecSql(db,sql);
        return 0;
    }

    /**
     * シラバスを追加
     */
    public static int insertSyllabus(SQLiteDatabase db,int subjectId,String url){
        String sql = String.format(
                "INSERT INTO syllabus(subject_id,syllabus_url)"+
                        " VALUES(%d,'%s');",subjectId,url);
        tryExecSql(db,sql);
        return 0;
    }

    /**
     * 点数を追加
     */
    public static int insertPoint(SQLiteDatabase db,int subjectId,int termId,int value){
        String sql = String.format(
                "INSERT INTO point(subject_id,term_id,value)"+
                        " VALUES(%d,%d,%d);",subjectId,termId,value);
        tryExecSql(db,sql);
        return 0;
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
            Log.i("DBHelper","exec:"+sql);
            return cursor;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


    private static void tryExecSql(SQLiteDatabase db,String sql){
       try{
           db.execSQL(sql);
       }catch(Exception e){
           e.printStackTrace();
       }
    }


    boolean init;
    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onOpen(db);
        init = true;    //  初回


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        init = true;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database = super.getReadableDatabase();
        if (init) {
            try {
                database = copyDatabase(database);
            } catch (IOException e) {
            }
        }
        return database;
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = super.getWritableDatabase();
        if (init) {
            try {
                database = copyDatabase(database);
            } catch (IOException e) {
            }
        }
        return database;
    }

    private SQLiteDatabase copyDatabase(SQLiteDatabase database) throws IOException {
        // dbがひらきっぱなしなので、書き換えできるように閉じる
        database.close();

        // コピー！
        InputStream input = _context.getAssets().open(DB_NAME);
        OutputStream output = new FileOutputStream(_context.getDatabasePath(DB_NAME));
        copy(input, output);

        init = false;
        // dbを閉じたので、また開く
        return super.getWritableDatabase();
    }




    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}