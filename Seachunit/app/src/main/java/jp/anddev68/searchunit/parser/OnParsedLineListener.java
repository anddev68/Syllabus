package jp.anddev68.searchunit.parser;

/**
 * Created by hideki on 2014/12/11.
 */
public interface OnParsedLineListener {

    /**
     * パーサーでで一行読み込んだ結果、任意のクラスを返す
     * @param subjectName
     * @param syllabusUrl
     * @return false 次の行へ行くのをやめる
     */
    public boolean onParsedLine(String subjectName,String syllabusUrl,String syllabusCode,int gradeId);

}
