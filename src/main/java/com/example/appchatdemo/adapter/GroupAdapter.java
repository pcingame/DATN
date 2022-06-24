package com.example.appchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.DashBoardActivity;
import com.example.appchatdemo.interfaces.IClickItemGroupListener;
import com.example.appchatdemo.model.GroupMessageModel;
import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.model.PrivateMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyGroupHolder> {

    private Context mContext;
    private List<GroupModel> groupModelList;
    private IClickItemGroupListener iClickItemGroupListener;

    private String theLastGroupMessage;
    private String theLastTime;
    private String theMember;
    private String myId;

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
        lastMessage(groupModel.getGroupId(), holder.groupNewest, holder.tvTime);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemGroupListener.onClickItemGroup(groupModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (groupModelList == null) {
            return 0;
        } else {
            return groupModelList.size();
        }
    }

    public class MyGroupHolder extends RecyclerView.ViewHolder {
        private CircleImageView groupImg;
        private TextView groupName, groupNewest, tvTime;
        private RelativeLayout layout;

        public MyGroupHolder(@NonNull View itemView) {
            super(itemView);

            groupImg = itemView.findViewById(R.id.imgGroupN);
            groupName = itemView.findViewById(R.id.tvGroupNameN);
            groupNewest = itemView.findViewById(R.id.tvLastMessageGroup);
            tvTime = itemView.findViewById(R.id.tvTime);
            layout = itemView.findViewById(R.id.group_layout);
        }
    }

    private void lastMessage(final String groupId, final TextView last_msg, TextView tvTime) {
        theLastGroupMessage = "default";
        theLastTime = "";

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);

        fireStore.collection("GroupMessages").orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot ds : value.getDocuments()) {
                    GroupMessageModel groupMessageModel = ds.toObject(GroupMessageModel.class);
                    if (groupMessageModel != null) {
                        if (groupMessageModel.getReceiver().equals(groupId)) {
                            theMember = groupMessageModel.getMemberName();
                            theLastGroupMessage = groupMessageModel.getMessage();
                            theLastTime = groupMessageModel.getTime();
                            myId = groupMessageModel.getSender();
                        }
                    }
                }
                if ("default".equals(theLastGroupMessage)) {
                    last_msg.setText("No Message");
                    tvTime.setText("");
                } else {
                    if (myId.equals(userId)) {
                        if ("".equals(theLastGroupMessage)) {
                            last_msg.setText("Bạn" + ": đã gửi 1 tệp đính kèm");
                        } else {
                            last_msg.setText("Bạn" + ": " + theLastGroupMessage);
                        }
                        tvTime.setText(theLastTime);
                    } else {
                        if ("".equals(theLastGroupMessage)) {
                            last_msg.setText(theMember + ": đã gửi 1 tệp đính kèm");
                        } else {
                            last_msg.setText(theMember + ": " + theLastGroupMessage);
                        }
                        tvTime.setText(theLastTime);
                    }
                }
                theLastGroupMessage = "default";
            }

        });
    }
}
