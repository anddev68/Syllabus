package jp.anddev68.searchunit;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;

import jp.anddev68.searchunit.database.DatabaseHelper;


/**
 * 点数詳細アクティビティー
 *
 * Created by hideki on 2014/12/11.
 */
public class DetailActivity extends Activity{

    String subjectName;
    int subjectId;

    TextView[] textViews;    //  点数表示テーブル
    TextView subjectTextView;   //  教科名表示
    TextView unitTextView;      //  目標単位数
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  インテントで教科名と教科コードを渡す
        subjectName = getIntent().getStringExtra("subject_name");
        subjectId = getIntent().getIntExtra("subject_id",-1);

        setContentView(R.layout.activity_detail);

        setupWidget();
    }



    private void setupWidget(){
        subjectTextView = (TextView) findViewById(R.id.subject);
        subjectTextView.setText(subjectName);
        unitTextView = (TextView) findViewById(R.id.unit);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                unitTextView.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        textViews = new TextView[10];
        textViews[0] = (TextView) findViewById(R.id.text0_1);
        textViews[1] = (TextView) findViewById(R.id.text0_2);
        textViews[2] = (TextView) findViewById(R.id.text1_1);
        textViews[3] = (TextView) findViewById(R.id.text1_2);
        textViews[4] = (TextView) findViewById(R.id.text2_1);
        textViews[5] = (TextView) findViewById(R.id.text2_2);
        textViews[6] = (TextView) findViewById(R.id.text3_1);
        textViews[7] = (TextView) findViewById(R.id.text3_2);
        textViews[8] = (TextView) findViewById(R.id.text4_1);
        textViews[9] = (TextView) findViewById(R.id.text4_2);

        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        for(int i=0; i<textViews.length; i++){
            //  タームIDはtextViewの添字と一緒
            //  0=中間点数,1=中間max・・・
            DatabaseHelper.getPointValue(db,subjectId,i,-1);
        }

    }


    private synchronized void download(String in,String out) {
        FetchPdfTask task = new FetchPdfTask();
        task.execute(in,out);
    }



}
