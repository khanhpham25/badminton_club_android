package com.example.asus.badminton_club.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.utils.Constant;

import java.util.ArrayList;

/**
 * Created by khanh on 17/11/2017.
 */

public class ClubCustomAdapter extends RecyclerView.Adapter<ClubCustomAdapter.ViewHolder> {
    private ArrayList<Club> dataSet;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtLocation;
        public TextView txtDescription;
        public ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.textViewClubName);
            txtLocation = view.findViewById(R.id.textViewClubLocation);
            txtDescription = view.findViewById(R.id.textViewClubDescription);
            avatar = view.findViewById(R.id.imageViewClubAvatar);
        }
    }

    public ClubCustomAdapter(ArrayList<Club> data, Context context) {
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_list_recycler_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Club club = dataSet.get(position);
        holder.txtName.setText(club.getName());
        holder.txtLocation.setText(club.getLocation());
        holder.txtDescription.setText(club.getDescription());

        if (club.getAvatar() == null || club.getAvatar().getUrl() == null || club.getAvatar().getUrl().equals("")) {
            holder.avatar.setImageDrawable(mContext.getDrawable(R.drawable.loginpic));
        } else {
            Glide.with(mContext).load(Constant.BASE_URL + club.getAvatar().getUrl()).into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
