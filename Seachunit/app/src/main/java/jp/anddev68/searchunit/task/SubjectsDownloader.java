package jp.anddev68.searchunit.task;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.record.Subject;

/**
 * 教科一覧を取得してきます
 * 現在はGNCT用に合わせて作成してあります
 * 必要に応じて変更をかけてください
 */
public class SubjectsDownloader extends AsyncTaskLoader<ArrayList<Subject>> {

    public SubjectsDownloader(Context context) {
        super(context);
    }

    /* メインダウンロードタスク */
    /* すべての学科のデータを一気に落とします */
    @Override
    public ArrayList<Subject> loadInBackground() {
        /* すべての教科データ */
        ArrayList<Subject> all = new ArrayList<>();
        /* URLリスト */
        String[] urls = getContext().getResources().getStringArray(R.array.url_array);
        /* すべてのURLに対して処理する */
        for (int department = 0; department < urls.length; department++) {
            try {
                //  トップURLを指定してパースをかける
                ArrayList<Subject> subjects = parse(urls[department]);
                //  一括処理
                //  学科を対応するものに変更
                for (Subject subject : subjects) subject.departId= department;
                //  allに足す
                all.addAll(subjects);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        //  すべての教科データを返す
        return all;
    }


    /**
     * URLを解析して教科データで渡す
     * @return
     */
    private ArrayList<Subject> parse(String url) throws IOException {
        final CharSequence[] cs = {"１年", "２年", "３年", "４年", "５年"};
        final String[] cs2 = {"地理(", "英語C(", "政治経済(", "英語A("};
        ArrayList<Subject> subjects = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "sjis"));
        String line = null;
        int grade = 0;
        while ((line = br.readLine()) != null) {
            //  学年別に読む処理
            if (grade < cs.length) {
                if (line.contains(cs[grade])) grade++;
                if (grade == 0) continue;
            }
            //  あとはパターンマッチング
            Pattern pattern = Pattern.compile("<a.*?href=\"(.*?)\".*?>(.*?)</a>");
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                String abs_path = url.substring(0, url.lastIndexOf('/')) + "/";
                String href = abs_path + m.group(1).replaceAll("\\s", "");  //  URLは絶対パス
                String text = m.group(2).replaceAll("\\s", "");  // 科目名

                //  一般科の特殊なものを処理
                for (String str : cs2) {
                    if (line.contains(str)) {
                        text = str + text + ")";
                    }
                }
                //  教科データ作成
                Subject subject = new Subject(text, href, grade, -1);
                //  追加
                subjects.add(subject);
            }
        }
        br.close();
        return subjects;
    }



}
