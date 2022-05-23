package com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.CreateGroupChatActivity;
import com.example.appchatdemo.activities.GroupMessageActivity;
import com.example.appchatdemo.adapter.GroupAdapter;
import com.example.appchatdemo.interfaces.IClickItemGroupListener;
import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.viewmodel.GroupViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GroupChatFragment extends Fragment {

    private FloatingActionButton btnFloat;
    public static final String TAG = GroupChatFragment.class.getName();

    FirebaseAuth auth;
    FirebaseStorage fireStore;
    String userId;
    RecyclerView rcvGroup;
    GroupAdapter groupAdapter;
    GroupViewModel groupViewModel;
    List<GroupModel> groupList;

    public GroupChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseStorage.getInstance();

        rcvGroup = view.findViewById(R.id.rv_list_group);
        rcvGroup.setHasFixedSize(true);
        rcvGroup.setLayoutManager(new LinearLayoutManager(getContext()));

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter( new IClickItemGroupListener() {
            @Override
            public void onClickItemGroup(GroupModel groupModel) {
                onClickGoToGroupMessage(groupModel);
            }
        });

        String groupId = auth.getCurrentUser().getUid();

        groupViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication())).get(GroupViewModel.class);
        groupViewModel.getAllGroupJoin().observe(this, new Observer<List<GroupModel>>() {
            @Override
            public void onChanged(List<GroupModel> groupModels) {
                groupAdapter.setGroupModelList(groupModels);
                rcvGroup.setAdapter(groupAdapter);
                groupAdapter.notifyDataSetChanged();
            }
        });

        btnFloat = view.findViewById(R.id.add_group_chat);

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateGroupChatActivity.class);
                startActivity(intent);
                //Toast.makeText(getContext(), "move to create group activity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickGoToGroupMessage(GroupModel groupModel) {
        Intent intent = new Intent(getContext(), GroupMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("groupModel", groupModel);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }
}