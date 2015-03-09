package jp.anddev68.searchunit;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by anddev68 on 15/03/08.
 */
public class MainPageAdapter extends PagerAdapter{

    private static final String[] PAGE_TITLE = {"得点表示","得点登録","シラバス"};
    private static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    Context context;

    public MainPageAdapter(Context c){
        super();
        context = c;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(MP,MP));
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setText("" + position);

        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View)object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLE[position];
    }

}
