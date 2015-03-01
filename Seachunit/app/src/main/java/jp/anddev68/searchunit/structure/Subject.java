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

    public Subject(int id,String name,String depart,String grade){
        this.subjectId = id;
        this.subjectName = name;
        this.depart = depart;
        this.grade = grade;
    }

    @Override
    public String toString(){
        return this.subjectName;
    }
}
