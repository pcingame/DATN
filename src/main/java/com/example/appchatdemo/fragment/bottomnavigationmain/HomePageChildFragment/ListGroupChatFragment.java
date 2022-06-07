package com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class ListGroupChatFragment extends Fragment {

    private FloatingActionButton btnFloat;
    FirebaseAuth auth;
    FirebaseStorage fireStore;
    RecyclerView rcvGroup;
    GroupAdapter groupAdapter;
    GroupViewModel groupViewModel;

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

        groupAdapter = new GroupAdapter(getContext(), new IClickItemGroupListener() {
            @Override
            public void onClickItemGroup(GroupModel groupModel) {
                onClickGoToGroupMessage(groupModel);
            }
        });

        groupViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())).get(GroupViewModel.class);
        groupViewModel.getAllGroupJoin().observe(getViewLifecycleOwner(), new Observer<List<GroupModel>>() {
            @Override
            public void onChanged(List<GroupModel> groupModels) {
                if (groupModels.size() == 0) {
                    Toast.makeText(getContext(), "Chưa có nhóm nào", Toast.LENGTH_SHORT).show();
                }
                groupAdapter.setGroupModelList(groupModels);
                rcvGroup.setAdapter(groupAdapter);
                groupAdapter.notifyDataSetChanged();
            }
        });

        btnFloat = view.findViewById(R.id.add_group_chat);

        btnFloat.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), CreateGroupChatActivity.class);
            startActivity(intent);
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