package jp.anddev68.searchunit.structure;

/**
 * Created by hideki on 2014/12/10.
 */
public class Subject {
    public int subjectId;
    public String subjectName;
    public String grade;
    public String depart;
    public String syllabusCode;

    public Subject(int id,String name){
        this.subjectId = id;
        this.subjectName = name;
    }

    @Override
    public String toString(){
        return this.subjectName;
    }
}
