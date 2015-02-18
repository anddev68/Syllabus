package jp.anddev68.searchunit;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import jp.anddev68.searchunit.database.DatabaseHelper;

/**
 * 設定ボタンを押した時に開くアクティビティー
 * 学科、学年等の設定を行う
 *
 * Created by hideki on 2014/12/21.
 */
public class RegistActivity extends Activity{

    RadioGroup radioGroup;
    TextView textView,textView2;
    Button button,button2;
    Button button3;
    String subjectName;
    int subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        /**
         *
         */
        subjectId = getIntent().getIntExtra("subject_id",-1);
        subjectName = getIntent().getStringExtra("subject_name");

        setupWidget();
    }

    private void setupWidget(){
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        textView = (TextView) findViewById(R.id.textView4);     //  数値
        textView2 = (TextView) findViewById(R.id.textView5);    //  教科名
        textView2.setText(subjectName);

        button = (Button) findViewById(R.id.button15); //delete
        button2 = (Button) findViewById(R.id.button16); //enter

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("0");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEnter();
            }
        });
    }

    /**
     * エンターキーが押されたときの処理
     */
    private void onClickEnter(){
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        int index = 1;
        switch( radioGroup.getCheckedRadioButtonId() ){
            case R.id.radioButton2: index = 2; break;
            case R.id.radioButton3: index = 3; break;
            case R.id.radioButton4: index = 4; break;
            case R.id.radioButton5: index = 5; break;
        }

        int value = Integer.parseInt(textView.getText().toString());
        DatabaseHelper.insertPoint(db,subjectId,index,value);

        finish();
        Toast.makeText(this,"登録完了しました",Toast.LENGTH_SHORT).show();
    }

    /**
     * 数値ボタンが押されたときの処理
     */
    public void onClick(View v){
        Button button = (Button) v;
        String str = (String)button.getText();
        Log.i("TextView",textView.getText().toString());
        if( textView.getText().toString().equals("0")) textView.setText("");
        textView.setText( textView.getText()+str);



    }




}
