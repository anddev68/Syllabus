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

    TextView point,point2,point3,point4,point5;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  パラメータを取得
        subjectName = getIntent().getStringExtra("subject_name");
        subjectId = getIntent().getIntExtra("subject_id",-1);

        setContentView(R.layout.activity_detail);
        setupWidget();
    }



    private void setupWidget(){
        point = (TextView) findViewById(R.id.point1);
        point2 = (TextView) findViewById(R.id.point2);
        point3 = (TextView) findViewById(R.id.point3);
        point4 = (TextView) findViewById(R.id.point4);
        point5 = (TextView) findViewById(R.id.point5);
        textView = (TextView) findViewById(R.id.textView6);

        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        point.setText(""+DatabaseHelper.getPointValue(db,subjectId,1,-1));
        point2.setText(""+DatabaseHelper.getPointValue(db,subjectId,2,-1));
        point3.setText(""+DatabaseHelper.getPointValue(db,subjectId,3,-1));
        point4.setText(""+DatabaseHelper.getPointValue(db,subjectId,4,-1));
        point5.setText(""+DatabaseHelper.getPointValue(db,subjectId,5,-1));
    }



    private void onClickButton2(View v){
        this.finish();
    }

    private synchronized void download(String in,String out) {
        FetchPdfTask task = new FetchPdfTask();
        task.execute(in,out);
    }



}
