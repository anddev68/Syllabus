package jp.anddev68.searchunit.widget.drawer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import jp.anddev68.searchunit.R;

/**
 * Drawer用のAdapterを作成する
 */
public class DrawerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    /**
     * Drawerに表示するアイテムを作成
     */
    final DrawerItem[] items ={
            new DrawerItem("未ログイン",R.layout.drawer_list_header),
            new DrawerItem("設定",R.layout.material_list_item1),
            new DrawerItem("ご意見・ご要望",R.layout.material_list_item1)
    };


    public DrawerAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }



    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        inflater = LayoutInflater.from(context);

        DrawerItem item = items[position];
        convertView = inflater.inflate(item.layout,parent,false);

        ((TextView)convertView.findViewById(R.id.text)).setText(item.title);
        return convertView;
    }

    private class DrawerItem{
        String title;
        int layout;
        DrawerItem(String title,int layout){
            this.title = title;
            this.layout = layout;
        }
    }

}
