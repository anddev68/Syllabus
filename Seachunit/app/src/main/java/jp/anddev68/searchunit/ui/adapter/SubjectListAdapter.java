package jp.anddev68.searchunit.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jp.anddev68.searchunit.R;


public class SubjectListAdapter  extends BaseAdapter{
    Context context;
    LayoutInflater inflater;
    ArrayList<String> subjects;

    public SubjectListAdapter(Context context){
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSubjects(ArrayList<String> subjects){
        this.subjects = subjects;
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Override
    public Object getItem(int position) {
        return subjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.material_list_item1,parent,false);

        ((TextView)convertView.findViewById(R.id.text)).setText(subjects.get(position));

        return convertView;
    }


}
