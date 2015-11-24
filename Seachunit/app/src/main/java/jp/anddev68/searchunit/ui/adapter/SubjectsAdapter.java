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
import jp.anddev68.searchunit.record.Subject;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private ArrayList<Subject> mDataList;

    public SubjectsAdapter(Context context, ArrayList<Subject> dataList) {
        super();
        mLayoutInflater = LayoutInflater.from(context);
        mDataList = dataList;
    }

    //  作成されたときにインフレ―トするだけ
    @Override
    public SubjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.material_list_item1, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // データサイズを返す
    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    //  アイテムをセットする
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Subject subject = mDataList.get(position);
        holder.textView.setText(subject.name);
    }


    //  ViewHolder
    //  インフレ―トしたxmlの構造に合わせてホルダーを作成
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.text);
            imageView = (ImageView) v.findViewById(R.id.icon);
        }
    }


}