package jp.anddev68.searchunit.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import jp.anddev68.searchunit.R;

/**
 * ユーザ情報用のフラグメント
 */
public class UserInfoFragment extends Fragment{

    RecyclerView recyclerView;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle bundle){
        rootView = inflater.inflate(R.layout.fragment_user_info,container,false);
        return rootView;
    }





    private View findViewById(int id){
        return rootView.findViewById(id);
    }

}
