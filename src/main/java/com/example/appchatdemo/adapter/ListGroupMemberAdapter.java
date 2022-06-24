package com.example.appchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
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

public class ListGroupMemberAdapter extends RecyclerView.Adapter<ListGroupMemberAdapter.GroupMemberHolder> implements Filterable {
    private List<UserModel> userModelList;
    private List<UserModel> userListOld;
    private Context mContext;
    private ArrayList<String> listMember = new ArrayList<>();
    private IClickCheckboxListener checkboxListener;

    public ListGroupMemberAdapter(Context mContext, IClickCheckboxListener checkboxListener) {
        this.mContext = mContext;
        this.checkboxListener = checkboxListener;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
        this.userListOld = userModelList;
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

        holder.memberCheckbox.setOnClickListener(v -> {
            if (holder.memberCheckbox.isChecked()) {
                listMember.add(userModel.getUserId());
            } else {
                listMember.remove(userModel.getUserId());
            }
            checkboxListener.onCheckboxMemberListener(listMember);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    userModelList = userListOld;
                } else {
                    List<UserModel> list = new ArrayList<>();
                    for (UserModel userModel : userListOld) {
                        if (userModel.getUsername().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(userModel);
                        }
                    }
                    userModelList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userModelList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userModelList = (List<UserModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
