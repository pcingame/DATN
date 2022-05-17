package com.example.appchatdemo.adapter;

import android.content.Context;
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
import com.example.appchatdemo.model.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.MyUserHolder>{

    Context context;
    List<UserModel> userModelList;
    OnUserChecked interfaceForAddUser;

    public AddUserAdapter(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    OnUserChecked interfaceForUser;

    public AddUserAdapter(OnUserChecked interfaceForAddUser) {
        this.interfaceForUser = interfaceForAddUser;
    }

    //add list user from the viewmodel into adapter
    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public MyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_contact_item_layout,
                parent, false);
        return new MyUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        Glide.with(holder.itemView.getContext()).load(userModelList.get(position).getImageUrl()).centerCrop().into(holder.imgAvatar);
        holder.tvUserName.setText(userModel.getUsername());
        holder.tvUserId.setText(userModel.getUserId());
//fix checkbox
        holder.chonseOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(compoundButton.getContext(), "checked this one", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userModelList == null){
            return 0;
        }
        else {
            return userModelList.size();
        }
    }

    class MyUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUserName, tvUserId;
        private CircleImageView imgAvatar;
        private CheckBox chonseOne;

        public MyUserHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.addUserName);
            tvUserId = itemView.findViewById(R.id.addUserId);
            imgAvatar = itemView.findViewById(R.id.addImageUser);
            chonseOne = itemView.findViewById(R.id.chonse_one);

            tvUserName.setOnClickListener(this);
            imgAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            interfaceForAddUser.UserChecked(getAdapterPosition(), userModelList);
        }
    }

    public interface OnUserChecked {
        void UserChecked(int adapterPosition, List<UserModel> userModelList);
    }
}
