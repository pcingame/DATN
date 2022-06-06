package com.example.appchatdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.interfaces.IClickItemUserListener;
import com.example.appchatdemo.model.PrivateMessageModel;
import com.example.appchatdemo.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateChatListAdapter extends RecyclerView.Adapter<PrivateChatListAdapter.PrivateChatListHolder> {

    private Context mContext;
    private List<UserModel> userModelList;
    private IClickItemUserListener iClickItemUserListener;
    List<PrivateMessageModel> privateMessageModelsList = new ArrayList<>();

    private String theLastMessage;

    public PrivateChatListAdapter(Context mContext, List<UserModel> userModelList, IClickItemUserListener iClickItemUserListener) {
        this.mContext = mContext;
        this.userModelList = userModelList;
        this.iClickItemUserListener = iClickItemUserListener;
    }

    @NonNull
    @Override
    public PrivateChatListAdapter.PrivateChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout,
                parent, false);
        return new PrivateChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateChatListAdapter.PrivateChatListHolder holder, int position) {
        UserModel userModel = userModelList.get(position);

        if (userModel == null) {
            return;
        }

        holder.tvUserName.setText(userModel.getUsername());
        Glide.with(holder.itemView.getContext()).load(userModelList.get(position).getImageUrl()).centerCrop().into(holder.imgAvatar);
        if (userModelList.get(position).getActiveStatus().equals("online")) {
            holder.imgIsOnline.setImageResource(R.drawable.online);
        } else {
            holder.imgIsOnline.setImageResource(R.drawable.offline);
        }
        lastMessage(userModel.getUserId(), holder.tvLastMessger);
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

    class PrivateChatListHolder extends RecyclerView.ViewHolder {

        private TextView tvUserName, tvLastMessger;
        private CircleImageView imgAvatar;
        private ImageView imgIsOnline;
        private RelativeLayout layoutItemContact;

        public PrivateChatListHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.userNameFrag);
            tvLastMessger = itemView.findViewById(R.id.tvUserNameEmail);
            imgAvatar = itemView.findViewById(R.id.imageViewUser);
            imgIsOnline = itemView.findViewById(R.id.imgIsOnline);
            layoutItemContact = itemView.findViewById(R.id.layout_item_contact);

        }

    }

    //check for last message
    private void lastMessage(final String friend, final TextView last_msg) {
        theLastMessage = "default";

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        String userId;
        userId = auth.getCurrentUser().getUid();

        fireStore.collection("PrivateMessages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot ds : value.getDocuments()) {
                    PrivateMessageModel privateMessageModel = ds.toObject(PrivateMessageModel.class);
                    if (privateMessageModel != null) {
                        if (privateMessageModel.getSender().equals(userId) && privateMessageModel.getReceiver().equals(friend)
                                || privateMessageModel.getReceiver().equals(userId)
                                && privateMessageModel.getSender().equals(friend)) {
                            theLastMessage = privateMessageModel.getMessage();
                        }
                    }
                }
                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }
        });
    }
}
