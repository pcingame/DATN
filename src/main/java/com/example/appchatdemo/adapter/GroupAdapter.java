package com.example.appchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.DashBoardActivity;
import com.example.appchatdemo.interfaces.IClickItemGroupListener;
import com.example.appchatdemo.model.GroupModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyGroupHolder> {

    private Context mContext;
    private List<GroupModel> groupModelList;
    private IClickItemGroupListener iClickItemGroupListener;

    public GroupAdapter(Context mContext, IClickItemGroupListener iClickItemGroupListener) {
        this.mContext = mContext;
        this.iClickItemGroupListener = iClickItemGroupListener;
    }

    public GroupAdapter(List<GroupModel> groupModelList) {
        this.groupModelList = groupModelList;
    }

    public List<GroupModel> getGroupModelList() {
        return groupModelList;
    }

    public void setGroupModelList(List<GroupModel> groupModelList) {
        this.groupModelList = groupModelList;
    }

    @NonNull
    @Override
    public MyGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_layout, parent, false);
        return new MyGroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.MyGroupHolder holder, int position) {
        GroupModel groupModel = groupModelList.get(position);
        if (groupModel == null) {
            return;
        }
        Glide.with(holder.itemView.getContext()).load(groupModelList.get(position).getGroupAvatar()).centerCrop().into(holder.groupImg);
        holder.groupName.setText(groupModel.getGroupName());
        holder.groupNewest.setText("have new message");
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemGroupListener.onClickItemGroup(groupModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (groupModelList == null){
            return 0;
        }
        else {
            return groupModelList.size();
        }
    }

    public class MyGroupHolder extends RecyclerView.ViewHolder{
        private CircleImageView groupImg;
        private TextView groupName, groupNewest;
        private RelativeLayout layout;

        public MyGroupHolder(@NonNull View itemView) {
            super(itemView);

            groupImg = itemView.findViewById(R.id.group_img);
            groupName = itemView.findViewById(R.id.group_name);
            groupNewest = itemView.findViewById(R.id.group_newest);
            layout = itemView.findViewById(R.id.group_layout);
        }
    }
}
