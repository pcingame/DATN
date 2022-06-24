package com.example.appchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.model.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InformationGroupAdapter extends RecyclerView.Adapter<InformationGroupAdapter.InfoHolder> {

    private Context mContext;
    private List<UserModel> userModelList;

    public InformationGroupAdapter(Context mContext, List<UserModel> userModelList) {
        this.mContext = mContext;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_chat_layout,
                parent, false);
        return new InfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoHolder holder, int position) {
        UserModel userModel = userModelList.get(position);

        if (userModel == null) {
            return;
        }

        holder.tvUserName.setText(userModel.getUsername());
        holder.tvLastMessger.setText(userModel.getEmail());
        Glide.with(holder.itemView.getContext()).load(userModelList.get(position).getImageUrl()).centerCrop().into(holder.imgAvatar);
        if (userModelList.get(position).getActiveStatus().equals("online")) {
            holder.imgIsOnline.setImageResource(R.drawable.online);
        } else {
            holder.imgIsOnline.setImageResource(R.drawable.offline);
        }
    }

    @Override
    public int getItemCount() {
        if (userModelList == null) {
            return 0;
        } else {
            return userModelList.size();
        }
    }

    public class InfoHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvLastMessger, tvTime;
        private CircleImageView imgAvatar;
        private ImageView imgIsOnline;
        private RelativeLayout layoutItemContact;

        public InfoHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.userNameFrag);
            tvLastMessger = itemView.findViewById(R.id.tvUserNameEmail);
            tvTime = itemView.findViewById(R.id.tvTimeP);
            imgAvatar = itemView.findViewById(R.id.imageViewUser);
            imgIsOnline = itemView.findViewById(R.id.imgIsOnline);
            layoutItemContact = itemView.findViewById(R.id.layout_item_contact);
        }
    }
}
