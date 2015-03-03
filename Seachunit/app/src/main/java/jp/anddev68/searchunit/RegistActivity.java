package jp.anddev68.searchunit;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import jp.anddev68.searchunit.database.DatabaseAccessor;
import jp.anddev68.searchunit.database.DatabaseHelper;

/**
 * 設定ボタンを押した時に開くアクティビティー
 * 学科、学年等の設定を行う
 *
 * Created by hideki on 2014/12/21.
 */
public class RegistActivity extends Activity{

    String subjectName;
    int subjectId;
    int termId;


    TextView subjectNameView;
    TextView termView;
    TextView numberView;

    String[] termArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        /**
         *  値を取得しておく
         */
        subjectId = getIntent().getIntExtra("subject_id",-1);
        subjectName = getIntent().getStringExtra("subject_name");
        termId = getIntent().getIntExtra("term_id",-1);


        /**
         * 楽器表の作成
         */
        termArray = getResources().getStringArray(R.array.term_array);


        setupWidget();
    }

    private void setupWidget(){
        subjectNameView = (TextView) findViewById(R.id.textView5);
        termView = (TextView) findViewById(R.id.textView);
        numberView = (TextView) findViewById(R.id.textView8);

        //  初期値を設定
        if(subjectName!=null) subjectNameView.setText(subjectName);
        if(termId!=-1) termView.setText(termArray[termId]);

    }

    /**
     * エンターキーが押されたときの処理
     */
    public void onClickEnter(View v){
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        int index = indexOf(termView.getText().toString());   //  termid
        int value = Integer.parseInt(numberView.getText().toString());  //  value

        if(index==-1){
            Toast.makeText(this,"学期を選択してください",Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        if( 0>value && value > 100){
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("0~100点ではありません。\n本当にこれで登録しますか？")
                    .setPositiveButton("OK",null)
                    .setNegativeButton("NO",null)
                    .show();
        }
        */

        //  値を新規登録か、上書きかの決定をデータがあるかどうかで判別する。
        int exist_flag =  DatabaseAccessor.getPointValue(db, subjectId, index, -1);
        if(exist_flag==-1) DatabaseAccessor.insertPoint(db,subjectId,index,value); // new
        else DatabaseAccessor.updatePointValue(db,subjectId,index,value); //  override


        //  TODO
        //  新規データ出ない場合はupdateメソッドに変える。

        finish();
        Toast.makeText(this,"登録完了しました",Toast.LENGTH_SHORT).show();

    }


    /**
     * 数値ボタンが押されたときの処理
     */
    public void onClickNumber(View v){
        Button button = (Button) v;
        String str = (String)button.getText();
        Log.i("TextView",numberView.getText().toString());
        if( numberView.getText().toString().equals("0")) numberView.setText("");
        numberView.setText( numberView.getText()+str);

    }


    public void onClickDelete(View v){
        numberView.setText( numberView.getText().subSequence(0,numberView.getText().length()-1) );
    }


    /**
     * 選択...が押された場合
     * 擬似ドロップダウンを開く
     */
    public void onClickSelectLabel(View v){
        final ListView listView = new ListView(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,termArray);
        listView.setAdapter(adapter);

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Select Term...")
            .setView(listView)
            .setNegativeButton("CANCEL",null)
            .create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                termView.setText(((TextView)view).getText());
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    private int indexOf(String str){
        for(int i=0; i<termArray.length; i++){
            if(termArray[i].equals(str)) return i;
        }
        return -1;
    }


}
