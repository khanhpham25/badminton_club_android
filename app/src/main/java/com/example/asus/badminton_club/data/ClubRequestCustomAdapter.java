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
import com.example.asus.badminton_club.utils.Constant;

import java.util.ArrayList;

/**
 * Created by khanh on 30/11/2017.
 */

public class ClubRequestCustomAdapter extends RecyclerView.Adapter<ClubRequestCustomAdapter.ViewHolder> {
    private ArrayList<JoinRequest> dataSet;
    private Context mContext;
    public static ClubRequestCustomAdapterListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public Button btnAccept;
        public Button btnDeny;
        public ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.textViewUserName);
            btnAccept = view.findViewById(R.id.btnAcceptRequest);
            btnDeny = view.findViewById(R.id.btnDenyRequest);
            avatar = view.findViewById(R.id.imageViewUserAvatar);

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.btnAcceptOnClick(view, getAdapterPosition());
                }
            });

            btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.btnDenyOnClick(view, getAdapterPosition());
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

    public ClubRequestCustomAdapter(ArrayList<JoinRequest> data, Context context, ClubRequestCustomAdapterListener listener) {
        this.dataSet = data;
        this.mContext = context;
        this.onClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_requests_recycler_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JoinRequest joinRequest = dataSet.get(position);
        holder.txtName.setText(joinRequest.getUser().getName());

        if (joinRequest.getUser() == null || joinRequest.getUser().getAvatar() == null ||
            joinRequest.getUser().getAvatar().getUrl() == null || joinRequest.getUser().getAvatar().getUrl().equals("")) {
            holder.avatar.setImageDrawable(mContext.getDrawable(R.drawable.defaultuser));
        } else {
            Glide.with(mContext).load(Constant.BASE_URL + joinRequest.getUser().getAvatar().getUrl()).into(holder.avatar);
        }
    }

    public interface ClubRequestCustomAdapterListener {

        void btnAcceptOnClick(View v, int position);

        void btnDenyOnClick(View v, int position);

        void imageViewAvatarOnClick(View v, int position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
