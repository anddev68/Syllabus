package jp.anddev68.searchunit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import android.widget.Gallery.LayoutParams;

import jp.anddev68.searchunit.structure.Subject;


/**
 *
 * TODO:右から入って左へ抜けるアニメーションの追加,Acitivtyの繊維,2回起動時の破棄
 * TODO:ライセンスをapache2.0に変更
 *
 * Created by anddev68 on 15/02/27.
 */
public class StartupActivity extends Activity implements ViewSwitcher.ViewFactory {

    ImageSwitcher imageSwitcher;

    int images[] = {
        R.drawable.slide1,
        R.drawable.slide2,
        R.drawable.slide3,
        R.drawable.slide4,
        R.drawable.slide5,
        R.drawable.slide6,
    };

    int position = 0;
    float oldX = 0;

    Animation inFromRightAnimation;
    Animation inFromLeftAnimation;
    Animation outToLeftAnimation;
    Animation outToRightAnimation;

    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inFromRightAnimation = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
        inFromLeftAnimation = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
        outToLeftAnimation = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
        outToRightAnimation = AnimationUtils.loadAnimation(this,R.anim.slide_out_right);

        setContentView(R.layout.activity_start);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        //  初回起動チェック
        if( !pref.getBoolean("first_boot",true) ){
            //  2回目以降は起動しない
            startActivity(new Intent(this,SubjectListActivity.class));
            finish();
        }

        //  初回起動フラグを消す
        pref.edit().putBoolean("first_boot",false).commit();




        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(this);
        imageSwitcher.setImageResource(images[position]);
        imageSwitcher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                        //  右に動いた
                        if(oldX < event.getX() - 10){
                            showNext();

                            //  左に動いた
                        }else if(oldX > event.getX()+10){
                            showPrevious();


                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();

                        break;


                }

                return true;
            }
        });

    }

    @Override
    public View makeView() {
        // ApiDemos->Views->ImageSwitcherのソースからメソッドを丸々コピー
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return i;
    }


    private void showNext(){
        position += 1;
        if (position >= images.length) {
            startActivity(new Intent(this, SubjectListActivity.class));
            finish();
            return;
        }
        imageSwitcher.setInAnimation(inFromRightAnimation);
        imageSwitcher.setOutAnimation(outToLeftAnimation);
        imageSwitcher.setImageResource(images[position]);
    }

    private void showPrevious(){
        position -= 1;
        if (position < 0) {
            position = images.length - 1;
        }
        imageSwitcher.setInAnimation(inFromLeftAnimation);
        imageSwitcher.setOutAnimation(outToRightAnimation);
        imageSwitcher.setImageResource(images[position]);
    }



}
