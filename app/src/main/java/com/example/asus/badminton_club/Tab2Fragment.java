package com.example.asus.badminton_club;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.badminton_club.screen.setting.SettingActivity;
import com.example.asus.pleaseplease.ClubCreate;


/**
 * Created by asus on 11/5/2017.
 */

public class Tab2Fragment extends Fragment {
    int[] IMAGES = {R.drawable.loginicon, R.drawable.loginpic, R.drawable.logo,R.drawable.loginpic, R.drawable.logo};
    String[] NAMES = {"ONETWO", "THREEFOUR", "FIVESIX", "SEVENEIGHT","NINETEN"};
    String[] DESCRIPTIONs = {"MOTHAI", "BABON", "NAMSAU", "BAYTAM", "CHINMUOI"};
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2, container, false);

        ListView listview = (ListView) view.findViewById(R.id.Listview1);
        Tab2Fragment.CustomAdapter customAdapter=new Tab2Fragment.CustomAdapter();
        listview.setAdapter(customAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent myIntent = new Intent(view.getContext(), ClubMainActivity.class);
                    startActivityForResult(myIntent,0);
                }
            }
        });

        final View button = view.findViewById(R.id.createclub);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gotoCreateclub = new Intent(getActivity(), ClubCreate.class);
                        startActivity(gotoCreateclub);
                    }
                }
        );
        return view;
    }
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount(){
            return IMAGES.length;
        }

        @Override
        public Object getItem(int i){
            return null;
        }

        @Override
        public long getItemId(int i){
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup){
            view = getActivity().getLayoutInflater().inflate(R.layout.textviewlayout,null);
            ImageView imageView=(ImageView)view.findViewById(R.id.imageView2);
            TextView textView_name=(TextView)view.findViewById(R.id.textView_name);
            TextView textView_description=(TextView)view.findViewById(R.id.textView_description);
            imageView.setImageResource(IMAGES[i]);
            textView_name.setText(NAMES[i]);
            textView_description.setText(DESCRIPTIONs[i]);
            return view;
        }

    }


}
