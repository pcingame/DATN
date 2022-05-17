package com.example.appchatdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.GroupMessageActivity;
import com.example.appchatdemo.activities.PrivateMessageActivity;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment.ContactFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment.GroupChatFragment;
import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.model.ListChatModel;
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ListChatViewHodler> {

    private List<ListChatModel> chatModelList;
    public static final int TYPE_USER_MODEL = 1;
    public static final int TYPE_GROUP_MODEL = 2;
    private GroupModel groupModel;
    private UserModel userModel;
    private Context context;
    private List<UserModel> userModelList;
    private List<GroupModel> groupModelList;
    UserViewModel userViewModel;
    GroupChatFragment groupChatFragment;
    ContactFragment contactFragment;

    public ListChatAdapter(List<ListChatModel> chatModelList, Context context) {
        this.chatModelList = chatModelList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListChatViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_item, parent, false);
        return new ListChatViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListChatViewHodler holder, int position) {

        userModelList = new ArrayList<>();
        groupModelList = new ArrayList<>();

        ListChatModel listChatModel = chatModelList.get(position);
        if (listChatModel == null) {
            return;
        }

        if (TYPE_USER_MODEL == holder.getItemViewType()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.rcvListChat.setLayoutManager(linearLayoutManager);
            UserAdapter userAdapter = new UserAdapter(new IClickItemUserListener() {
                @Override
                public void onClickItemUser(UserModel userModel) {
                   // onClickGoToPrivateMessage(userModel);
                }
            });
            userAdapter.setUserModelList(listChatModel.getUserModelList());
            holder.rcvListChat.setAdapter(userAdapter);


        } else if (TYPE_GROUP_MODEL == holder.getItemViewType()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.rcvListChat.setLayoutManager(linearLayoutManager);
            GroupAdapter groupAdapter = new GroupAdapter(new IClickItemGroupListener() {
                @Override
                public void onClickItemGroup(GroupModel groupModel) {
                    //onClickGoToGroupMessage(groupModel);
                }
            });
            groupAdapter.setGroupModelList(listChatModel.getGroupModelList());
            holder.rcvListChat.setAdapter(groupAdapter);
        }
    }

    private void onClickGoToGroupMessage(GroupModel groupModel) {
        Intent intent = new Intent(groupChatFragment.getContext(), GroupMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("groupModel", groupModel);
        intent.putExtras(bundle);
        groupChatFragment.getContext().startActivity(intent);
        groupChatFragment.getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }

    private void onClickGoToPrivateMessage(UserModel userModel) {
        Intent intent = new Intent(contactFragment.getContext(), PrivateMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userModel", userModel);
        intent.putExtras(bundle);
        contactFragment.getContext().startActivity(intent);
        contactFragment.getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }

    @Override
    public int getItemViewType(int position) {
        return chatModelList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (chatModelList != null) {
            return chatModelList.size();
        }
        return 0;
    }

    class ListChatViewHodler extends RecyclerView.ViewHolder {

        private RecyclerView rcvListChat;

        public ListChatViewHodler(@NonNull View itemView) {
            super(itemView);
            rcvListChat = itemView.findViewById(R.id.rcvItemListChat);
        }
    }

}
