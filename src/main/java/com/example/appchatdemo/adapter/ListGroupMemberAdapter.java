package com.example.appchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.interfaces.IClickCheckboxListener;
import com.example.appchatdemo.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListGroupMemberAdapter extends RecyclerView.Adapter<ListGroupMemberAdapter.GroupMemberHolder> {
    private List<UserModel> userModelList;
    private Context mContext;
    private ArrayList<String> listMember = new ArrayList<>();
    private IClickCheckboxListener checkboxListener;

    public ListGroupMemberAdapter(Context mContext, IClickCheckboxListener checkboxListener) {
        this.mContext = mContext;
        this.checkboxListener = checkboxListener;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupMemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_chonsen_layout, parent, false);
        return new GroupMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberHolder holder, int position) {
        UserModel userModel = userModelList.get(position);

        if (userModel == null) {
            return;
        }

        Glide.with(holder.itemView.getContext()).load(userModelList.get(position).getImageUrl()).centerCrop().into(holder.memberAvatar);
        holder.memberName.setText(userModel.getUsername());
        holder.memberEmail.setText(userModel.getEmail());
        holder.memberCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.memberCheckbox.isChecked()) {
                    listMember.add(userModel.getUserId());
                } else {
                    listMember.remove(userModel.getUserId());
                }
                checkboxListener.onCheckboxMemberListener(listMember);
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


    public class GroupMemberHolder extends RecyclerView.ViewHolder {

        private CircleImageView memberAvatar;
        private TextView memberName, memberEmail;
        private CheckBox memberCheckbox;

        public GroupMemberHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            memberName = itemView.findViewById(R.id.member_name);
            memberEmail = itemView.findViewById(R.id.member_email);
            memberCheckbox = itemView.findViewById(R.id.member_checkbox);
        }
    }
}
