package jp.anddev68.searchunit.parser;

/**
 * Created by hideki on 2014/12/11.
 */
public abstract class AbstractParser {

    OnParsedLineListener _listener;
    public void setOnParsedLineListener(OnParsedLineListener l){_listener = l;}

    public abstract void start();

    public static AbstractParser create(int schoolId,String url){
        if(schoolId==1){    //  gnct
            return new GnctParser(url);
        }
        return null;
    }

}
