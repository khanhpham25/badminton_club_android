package com.example.asus.badminton_club.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.JoinRequest;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.utils.Constant;

import java.util.ArrayList;

/**
 * Created by khanh on 30/11/2017.
 */

public class ClubMemberCustomAdapter extends RecyclerView.Adapter<ClubMemberCustomAdapter.ViewHolder> {
    private ArrayList<User> dataSet;
    private Context mContext;
    private User currentUser;
    private Integer clubId;
    public static ClubMemberCustomAdapterListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtMobile;
        public TextView txtLevel;
        public Button btnDelete;
        public ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.textViewMemberName);
            txtMobile = view.findViewById(R.id.textViewMemberMobile);
            txtLevel = view.findViewById(R.id.textViewMemberLevel);
            btnDelete = view.findViewById(R.id.btnDeleteMember);
            avatar = view.findViewById(R.id.imageViewMemberAvatar);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.btnDeleteOnClick(view, getAdapterPosition());
                }
            });

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.imageViewAvatarOnClick(view, getAdapterPosition());
                }
            });
        }
    }

    public ClubMemberCustomAdapter(ArrayList<User> data, Context context, ClubMemberCustomAdapterListener listener, User currentUser, Integer clubId) {
        this.dataSet = data;
        this.mContext = context;
        this.onClickListener = listener;
        this.currentUser = currentUser;
        this.clubId = clubId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_members_recycler_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User member = dataSet.get(position);
        holder.txtName.setText(member.getName());
        if (member.getOwner()) {
            holder.txtName.setCompoundDrawables(mContext.getDrawable(R.drawable.ic_crown), null, null, null);
        }
        holder.txtMobile.setText("Mobile: " + member.getMobile());
        String level = "";
        if(member.getBadmintonLevel() != null && member.getBadmintonLevel() == 1 ) {
            level = "Beginner";
        } else if (member.getBadmintonLevel() != null && member.getBadmintonLevel() == 2){
            level = "Amateur";
        } else if (member.getBadmintonLevel() != null && member.getBadmintonLevel() == 3){
            level = "Professional";
        }
        holder.txtLevel.setText("Level: " + level);

        if (member.getAvatar() == null || member.getAvatar().getUrl() == null ||
            member.getAvatar().getUrl().equals("")) {
            holder.avatar.setImageDrawable(mContext.getDrawable(R.drawable.defaultuser));
        } else {
            Glide.with(mContext).load(Constant.BASE_URL + member.getAvatar().getUrl()).into(holder.avatar);
        }

        if(currentUser.getOwnedClubIds().contains(clubId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    public interface ClubMemberCustomAdapterListener {

        void btnDeleteOnClick(View v, int position);

        void imageViewAvatarOnClick(View v, int position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
