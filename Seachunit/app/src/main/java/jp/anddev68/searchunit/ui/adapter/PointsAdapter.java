package jp.anddev68.searchunit.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.anddev68.searchunit.R;
import jp.anddev68.searchunit.structure.Point;

/**
 * 点数一覧のカード
 */
public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {

    LayoutInflater mLayoutInflater;
    OnItemClickListener mListener;
    Context mContext;
    ArrayList<Point> mData;

    public PointsAdapter(Context context, OnItemClickListener l,ArrayList<Point> data){
        super();
        mListener = l;
        mContext = context;
        mData = data;
    }

    @Override
    public PointsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.material_card_grid_item1, parent, false);
        final PointsAdapter.ViewHolder viewHolder = new PointsAdapter.ViewHolder(v);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //  クリックしたときの処理
                final int pos = viewHolder.getAdapterPosition();
                if(mListener!=null) mListener.onItemClick(view,pos);
            }
        });
        return viewHolder;
    }

    //  ここでアイテムをセットする
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Point point = mData.get(position);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    //  ViewHolder
    //  インフレ―トしたxmlの構造に合わせてホルダーを作成
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView iconText;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.text);
            imageView = (ImageView) v.findViewById(R.id.icon);
            iconText = (TextView) v.findViewById(R.id.iconText);
        }
    }


    public interface OnItemClickListener{
        void onItemClick(View v,int index);
    }

}
