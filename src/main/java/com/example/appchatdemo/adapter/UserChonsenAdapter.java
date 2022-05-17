package com.example.appchatdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.model.MemberModel;
import com.example.appchatdemo.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChonsenAdapter extends RecyclerView.Adapter<UserChonsenAdapter.ChonsenHolder> {
    private List<UserModel> userModelList;
    private List<MemberModel> userChecked;
    private MemberModel memberModel;



    public List<MemberModel> getUserChecked() {
        return userChecked;
    }

    public void setUserChecked(List<MemberModel> userChecked) {
        this.userChecked = userChecked;
    }

    public UserChonsenAdapter(IClickItemMemberListener iClickItemUserListener) {
        this.userModelList = userModelList;
    }


    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public ChonsenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_chonsen_layout, parent,false);
        return new ChonsenHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChonsenHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        MemberModel memberModel = new MemberModel();
        userChecked = new ArrayList<>();
        if (memberModel == null){
            return;
        }
        Glide.with(holder.itemView.getContext()).load(userModelList.get(position).getImageUrl()).centerCrop().into(holder.memberAvata);
        holder.memberName.setText(userModel.getUsername());
        holder.memberId.setText("ID: " + userModel.getUserId());
        holder.chonsenOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.chonsenOne.isChecked()) {
                    holder.chonsenOne.setChecked(true);
//                    memberModel.setMembername(userModel.getUsername());
//                    memberModel.setMemberid(userModel.getUserId());
//                    memberModel.setMemberurl(userModel.getImageUrl());
//                    userChecked.add(memberModel);
                } else if(!holder.chonsenOne.isChecked()){
                    holder.chonsenOne.setChecked(false);
//                    memberModel.setMembername(userModel.getUsername());
//                    memberModel.setMemberid(userModel.getUserId());
//                    memberModel.setMemberurl(userModel.getImageUrl());
//                    userChecked.remove(memberModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userModelList == null) {
            return 0;
        } else {
            return userModelList.size();
        }
    }


    public class ChonsenHolder extends RecyclerView.ViewHolder{

        private CircleImageView memberAvata;
        private TextView memberName, memberId;
        private CheckBox chonsenOne;

        public ChonsenHolder(@NonNull View itemView) {
            super(itemView);
            memberAvata = itemView.findViewById(R.id.member_avata);
            memberName = itemView.findViewById(R.id.member_name);
            memberId = itemView.findViewById(R.id.member_id);
            chonsenOne = itemView.findViewById(R.id.member_chonsen);
        }
    }
}
