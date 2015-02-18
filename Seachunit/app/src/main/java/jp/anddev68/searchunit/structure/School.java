package jp.anddev68.searchunit.structure;

/**
 * Created by hideki on 2014/12/10.
 */
public class School {
    public String schoolName;   //  高専名
    public int schoolId;     //  高専のID

    public School(int id,String name){
        this.schoolId = id;
        this.schoolName = name;
    }
}
