package com.example.asus.pleaseplease;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.badminton_club.R;

import java.util.List;

public class ClubList extends AppCompatActivity {

    int[] IMAGES = {R.drawable.loginicon, R.drawable.loginpic, R.drawable.logo,R.drawable.loginpic, R.drawable.logo};
    String[] NAMES = {"ONETWO", "THREEFOUR", "FIVESIX", "SEVENEIGHT","NINETEN"};
    String[] DESCRIPTIONs = {"MOTHAI", "BABON", "NAMSAU", "BAYTAM", "CHINMUOI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);

        ListView listview = (ListView) findViewById(R.id.Listview1);

        CustomAdapter customAdapter=new CustomAdapter();
        listview.setAdapter(customAdapter);

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
            view = getLayoutInflater().inflate(R.layout.textviewlayout,null);
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
