package com.example.appchatdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.interfaces.IClickItemFile;
import com.example.appchatdemo.model.PrivateMessageModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.MyPrivateMessageHolder>{

    List<PrivateMessageModel> privateMessageModelList;
    private IClickItemFile iClickItemFile;

    public static final int MESSAGE_RIGHT = 0;//for user layout
    public static final int MESSAGE_LEFT = 1;//for friend layout

    public void setPrivateMessageModelList(List<PrivateMessageModel> privateMessageModelList) {
        this.privateMessageModelList = privateMessageModelList;
    }

    public PrivateMessageAdapter(List<PrivateMessageModel> privateMessageModelList, IClickItemFile iClickItemFile) {
        this.privateMessageModelList = privateMessageModelList;
        this.iClickItemFile = iClickItemFile;
    }

    @NonNull
    @Override
    public MyPrivateMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitemright,
                    parent, false);
            return new MyPrivateMessageHolder(view);
        } else  {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitemleft,
                    parent, false);
            return new MyPrivateMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyPrivateMessageHolder holder, int position) {

        PrivateMessageModel messageModel = privateMessageModelList.get(position);
        if(messageModel == null){
            return;
        }

        holder.tvShowMessage.setVisibility(View.VISIBLE);
        holder.tvTime.setVisibility(View.VISIBLE);
        holder.tvShowMessage.setText( privateMessageModelList.get(position).getMessage());
        holder.tvTime.setText(privateMessageModelList.get(position).getTime());

        if(privateMessageModelList.get(position).getFileType().equals("image")){
            holder.imgMessage.setVisibility(View.VISIBLE);
            holder.tvShowMessage.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext()).load(privateMessageModelList.get(position).getFile()).centerCrop().into(holder.imgMessage);
        }

         if(privateMessageModelList.get(position).getFileType().equals("pdf")){
            holder.layoutFile.setVisibility(View.VISIBLE);
            holder.tvShowMessage.setVisibility(View.GONE);
            holder.tvFileName.setVisibility(View.VISIBLE);
            holder.tvFileName.setText(privateMessageModelList.get(position).getFileName());
            holder.imgFile.setImageResource(R.drawable.pdf);
        }

        if(privateMessageModelList.get(position).getFileType().equals("docx")){
            holder.layoutFile.setVisibility(View.VISIBLE);
            holder.tvShowMessage.setVisibility(View.GONE);
            holder.tvFileName.setVisibility(View.VISIBLE);
            holder.tvFileName.setText(privateMessageModelList.get(position).getFileName());
            holder.imgFile.setImageResource(R.drawable.word);
        }

        if(privateMessageModelList.get(position).getFileType().equals("other")){
            holder.layoutFile.setVisibility(View.VISIBLE);
            holder.tvShowMessage.setVisibility(View.GONE);
            holder.tvFileName.setVisibility(View.VISIBLE);
            holder.tvFileName.setText(privateMessageModelList.get(position).getFileName());
            holder.imgFile.setImageResource(R.drawable.file);
        }

        holder.layoutFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemFile.onClickItemFile(messageModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (privateMessageModelList == null) {
            return 0;
        } else {
            return privateMessageModelList.size();
        }
    }

    class MyPrivateMessageHolder extends RecyclerView.ViewHolder {

        private  TextView tvShowMessage, tvTime, tvFileName;
        private  ImageView imgMessage, imgFile;
        private LinearLayout layoutFile;


        public MyPrivateMessageHolder(@NonNull View itemView) {
            super(itemView);
            tvShowMessage = itemView.findViewById(R.id.show_message);
            tvTime = itemView.findViewById(R.id.displaytime);
            imgMessage = itemView.findViewById(R.id.imgMessage);
            layoutFile = itemView.findViewById(R.id.layout_file);
            imgFile = itemView.findViewById(R.id.imgFile);
            tvFileName = itemView.findViewById(R.id.tvFileName);
        }
    }

    @Override
    public int getItemViewType(int position) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        PrivateMessageModel privateMessageModel = privateMessageModelList.get(position);

        if (privateMessageModel.getSender().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }
    }
}
