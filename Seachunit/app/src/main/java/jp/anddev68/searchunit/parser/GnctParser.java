package jp.anddev68.searchunit.parser;

import android.util.Log;

import org.apache.http.HttpRequest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.anddev68.searchunit.structure.Subject;

/**
 * GNCT専用のパーサです
 * 一般科のみMED/CAのような区切りがあるので対策を行っています
 */
public class GnctParser extends AbstractParser{

    String _top_url;

    public GnctParser(String url){
        _top_url = url;
    }

    @Override
    public void start(){
        try {
            URL url = new URL(_top_url);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"sjis"));
            String line =  null;
            CharSequence[] cs = {"１年","２年","３年","４年","５年"};
            String[] cs2 = {"地理(","英語C(","政治経済(","英語A("};
            int grade = 0;

            Log.i("GnctParser","URL:"+_top_url);

            while((line=br.readLine())!=null) {
                //  学年別に読む処理
                if( grade<cs.length) {
                    if (line.contains(cs[grade])) grade++;
                    if (grade == 0) continue;
                }

                Pattern pattern = Pattern.compile("<a.*?href=\"(.*?)\".*?>(.*?)</a>");
                Matcher m = pattern.matcher(line);
                while(m.find()){
                    String abs_path = _top_url.substring(0,_top_url.lastIndexOf('/'))+"/";
                    String code = m.group(1).replaceAll("\\s", "").replace(".pdf","");  //  xxxxxx.pdfのxxxx部分をコードとする
                    String href = abs_path + m.group(1).replaceAll("\\s", "");  //  URLは絶対パス
                    String text = m.group(2).replaceAll("\\s", "");  // 科目名

                    //  一般科の特殊なものを処理
                    for(String str:cs2){
                        if(line.contains(str)){
                            text = str+text+")";
                        }
                    }

                    //Log.i("GnctParser",
                    //    String.format("grade=%d href=%s text=%s",grade,href,text));
                    if(! _listener.onParsedLine(text,href,code,grade) ){
                        //  終了処理を加える
                    }
                }

            }

            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
