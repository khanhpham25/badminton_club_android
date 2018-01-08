package com.example.asus.badminton_club.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.model.WorkingSchedule;
import com.example.asus.badminton_club.utils.Constant;

import java.util.ArrayList;

/**
 * Created by khanh on 30/11/2017.
 */

public class ClubScheduleCustomAdapter extends RecyclerView.Adapter<ClubScheduleCustomAdapter.ViewHolder> {
    private ArrayList<WorkingSchedule> dataSet;
    private Context mContext;
    private User currentUser;
    private Integer clubId;
    public static ClubScheduleCustomAdapterListener onClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtDate;
        public EditText editDate;
        public Button btnEdit;
        public Button btnDelete;
        public Button btnConfirm;
        public ViewSwitcher switcher;

        public ViewHolder(View view) {
            super(view);
            txtDate = view.findViewById(R.id.tvDateTime);
            editDate = view.findViewById(R.id.edtDateTime);
            btnEdit = view.findViewById(R.id.btnEditDate);
            btnDelete = view.findViewById(R.id.btnDeleteSchedule);
            btnConfirm = view.findViewById(R.id.btnConfirmDate);
            switcher = view.findViewById(R.id.my_switcher);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.btnDeleteSchedule(view, getAdapterPosition());
                }
            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.btnConfirmDate(view, getAdapterPosition(), switcher);
                    btnConfirm.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.VISIBLE);
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.btnEditDate(view, getAdapterPosition(), switcher);
                    btnEdit.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public ClubScheduleCustomAdapter(ArrayList<WorkingSchedule> data, Context context, ClubScheduleCustomAdapterListener listener, User currentUser, Integer clubId) {
        this.dataSet = data;
        this.mContext = context;
        this.onClickListener = listener;
        this.currentUser = currentUser;
        this.clubId = clubId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_schedules_recycler_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WorkingSchedule schedule = dataSet.get(position);
        holder.txtDate.setText(schedule.getWorkingDate());

        if(currentUser.getOwnedClubIds().contains(clubId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.GONE);
        }
    }

    public interface ClubScheduleCustomAdapterListener {

        void btnDeleteSchedule(View v, int position);
        void btnConfirmDate(View v, int position, ViewSwitcher switcher);
        void btnEditDate(View v, int position, ViewSwitcher switcher);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
