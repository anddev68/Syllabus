package jp.anddev68.searchunit.record;

import android.graphics.Color;

/**
 * 教科モデル
 */
public class Subject extends Record{
    public int id;
    public String name;
    public String url; //  シラバスへのurl
    public int grade;
    public int department;

    public Subject(int id,String name,String url,int grade,int department){
        this.id = id;
        this.name = name;
        this.url = url;
        this.grade = grade;
        this.department = department;
    }

    /**
     * 学科のシンボルカラーを取得
     */
    public int getColor(){
        switch(department){
            case 0: //  一般
                return Color.RED;
            default:
                return Color.BLUE;
        }
    }
}
