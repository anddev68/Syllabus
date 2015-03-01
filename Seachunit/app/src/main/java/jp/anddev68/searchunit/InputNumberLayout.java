package jp.anddev68.searchunit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

/**
 *
 *
 * Created by anddev68 on 15/03/01.
 */
public class InputNumberLayout extends GridLayout {

    LinearLayout[] rows;
    Button[] buttons;
    OnClickListener listener;

    public InputNumberLayout(Context context) {
        super(context);
        setup();
    }

    public InputNumberLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public InputNumberLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @Override
    public void setOnClickListener(OnClickListener l){
        listener = l;
    }

    /**
     * Layoutのサイズを縦に4分割し、ボタンを配置します
     *  7 8 9
     *  4 5 6
     *  1 2 3
     *  0 del enter
     */
    private void setup(){

        //  自レイアウトの設定
        this.setColumnCount(3); //  横
        this.setRowCount(4);    //  縦

        //  ボタン作成
        buttons = new Button[12];
        for(int i=0; i<buttons.length; i++){
            //  TODO:ボタンのレイアウト設定
            buttons[i] = new Button(getContext());
            buttons[i].setId(i);
            buttons[i].setText(i);
            //buttons[i].setWidth(this.getWidth()/3);
            //buttons[i].setHeight(this.getHeight()/4);
            //  リスナをセット
            buttons[i].setOnClickListener(listener);

            //  ボタンを追加する
            this.addView(buttons[i]);
        }







    }



}
