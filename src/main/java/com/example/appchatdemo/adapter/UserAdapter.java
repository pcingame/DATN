package com.example.appchatdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.interfaces.IClickItemUserListener;
import com.example.appchatdemo.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyUserHolder> implements Filterable {

    private List<UserModel> userModelList;
    private List<UserModel> userListOld;
    private IClickItemUserListener iClickItemUserListener;

    public UserAdapter(IClickItemUserListener iClickItemUserListener) {
        this.iClickItemUserListener = iClickItemUserListener;

    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    //add list user from the viewmodel into adapter
    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
        this.userListOld = userModelList;
    }

    @NonNull
    @Override
    public MyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout,
                parent, false);
        return new MyUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserHolder holder, int position) {
        UserModel userModel = userModelList.get(position);

        if (userModel == null) {
            return;
        }

        holder.tvUserName.setText(userModel.getUsername());
        Glide.with(holder.itemView.getContext()).load(userModelList.get(position).getImageUrl()).centerCrop().into(holder.imgAvatar);
        holder.tvUserEmail.setText(userModel.getEmail());
        holder.tvStatus.setText(userModel.getStatus());
        if (userModelList.get(position).getActiveStatus().equals("online")) {
            holder.imgIsOnline.setImageResource(R.drawable.online);
        } else {
            holder.imgIsOnline.setImageResource(R.drawable.offline);
        }


        holder.layoutItemContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemUserListener.onClickItemUser(userModel);
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


    class MyUserHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        private TextView tvUserName, tvUserEmail, tvStatus;
        private CircleImageView imgAvatar;
        private ImageView imgIsOnline;
        private RelativeLayout layoutItemContact;

        public MyUserHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.userNameFrag);
            tvUserEmail = itemView.findViewById(R.id.tvUserNameEmail);
            tvStatus = itemView.findViewById(R.id.userStatusFrag);
            imgAvatar = itemView.findViewById(R.id.imageViewUser);
            imgIsOnline = itemView.findViewById(R.id.imgIsOnline);
            layoutItemContact = itemView.findViewById(R.id.layout_item_contact);

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
