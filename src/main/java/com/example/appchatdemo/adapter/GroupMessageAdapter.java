package com.example.appchatdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.interfaces.IClickItemFileInGroup;
import com.example.appchatdemo.model.GroupMessageModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageHolder> {

    List<GroupMessageModel> groupMessageModelList;
    private IClickItemFileInGroup iClickItemFileInGroup;

    public static final int MESSAGE_RIGHT = 0;//for user layout
    public static final int MESSAGE_LEFT = 1;//for group layout

    public GroupMessageAdapter(List<GroupMessageModel> groupMessageModelList, IClickItemFileInGroup iClickItemFileInGroup) {
        this.groupMessageModelList = groupMessageModelList;
        this.iClickItemFileInGroup = iClickItemFileInGroup;
    }

    public void setGroupMessageModelList(List<GroupMessageModel> groupMessageModelList) {
        this.groupMessageModelList = groupMessageModelList;
    }


    @NonNull
    @Override
    public GroupMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitemright,
                    parent, false);
            return new GroupMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitemleft,
                    parent, false);
            return new GroupMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageAdapter.GroupMessageHolder holder, int position) {
        GroupMessageModel messageModel = groupMessageModelList.get(position);
        if (messageModel == null) {
            return;
        }

        holder.tvShowMessage.setVisibility(View.VISIBLE);
        holder.tvTime.setVisibility(View.VISIBLE);
        holder.tvShowMessage.setText(groupMessageModelList.get(position).getMessage());
        holder.tvTime.setText(groupMessageModelList.get(position).getTime());

        holder.layoutFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemFileInGroup.onClickItemFile(messageModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (groupMessageModelList == null) {
            return 0;
        } else {
            return groupMessageModelList.size();
        }
    }


    public class GroupMessageHolder extends RecyclerView.ViewHolder {

        private TextView tvShowMessage, tvTime, tvFileName;
        private ImageView imgMessage, imgFile;
        private LinearLayout layoutFile;

        public GroupMessageHolder(@NonNull View itemView) {
            super(itemView);

            tvShowMessage = itemView.findViewById(R.id.show_message);
            tvTime = itemView.findViewById(R.id.displaytime);
            imgMessage = itemView.findViewById(R.id.imgMessage);
            layoutFile = itemView.findViewById(R.id.layout_file);
            imgFile = itemView.findViewById(R.id.imgFile);
            tvFileName = itemView.findViewById(R.id.tvFileName);
        }
    }

    public int getItemViewType(int position) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        GroupMessageModel groupMessageModel = groupMessageModelList.get(position);

        if (groupMessageModel.getSender().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }
    }
}
