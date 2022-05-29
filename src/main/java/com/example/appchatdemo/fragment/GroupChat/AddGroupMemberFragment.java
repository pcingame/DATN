package com.example.appchatdemo.fragment.GroupChat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.interfaces.IClickItemMemberListener;
import com.example.appchatdemo.adapter.UserChonsenAdapter;
import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.model.MemberModel;
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.firebase.database.annotations.Nullable;


public class AddGroupMemberFragment extends Fragment {

    private ImageButton btnBack;
    private Button btnCreateGroup;
    private NavController navController;
    private RecyclerView rcvUser;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private UserChonsenAdapter chonsenAdapter;
    private UserViewModel userViewModel;
    private List<UserModel> userList;
    private SearchView searchView;
    private UserModel userModel;
    private GroupModel groupModel;
    private CollectionReference groupRefer;
    private Task<DocumentReference> usersRefer;
    private String getIdGroup;
    String takeName;
    //private String imgLinkDefaultAvatar = "https://firebasestorage.googleapis.com/v0/b/appmiochat.appspot.com/o/logo.png?alt=media&token=7e409ee9-6038-45b9-b365-206072a56490";
    private MemberModel memberModel;
    private String imgLinkDefaultAvatar = "https://firebasestorage.googleapis.com/v0/b/appmiochat.appspot.com/o/logo.png?alt=media&token=7e409ee9-6038-45b9-b365-206072a56490";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_addGroupMemberFragment_to_addGroupNameFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_group_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        memberModel = new MemberModel();
        String takeName = getArguments().getString("groupName");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnBack = view.findViewById(R.id.btn_back_member);
        btnCreateGroup = view.findViewById(R.id.btn_accepted);
        navController = Navigation.findNavController(view);

        rcvUser = view.findViewById(R.id.rv_list_member);
        rcvUser.setHasFixedSize(true);
        rcvUser.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        chonsenAdapter = new UserChonsenAdapter(new IClickItemMemberListener() {
            @Override
            public void onClickItemUser(MemberModel memberModel) {
                setCheckedAddToGroupMessenger(memberModel);
            }
        });
        String userId = auth.getCurrentUser().getUid();

        userViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                chonsenAdapter.setUserModelList(userModels);
                rcvUser.setAdapter(chonsenAdapter);
                chonsenAdapter.notifyDataSetChanged();
            }
        });
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                firestore = FirebaseFirestore.getInstance();
                groupRefer = firestore.collection("Group");

                chonsenAdapter.getUserChecked();

                Map<String, Object> group = new HashMap<>();
                group.put("groupName", takeName);
                group.put("host", auth.getCurrentUser().getUid());
                group.put("groupMember", chonsenAdapter.getUserChecked());
                group.put("groupAvata", imgLinkDefaultAvatar);
                //add many member
                firestore.collection("Group").add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "nhóm đã được lập", Toast.LENGTH_SHORT).show();
                        String id = documentReference.getId();
                        addIdFieldForGroup(id);
                    }
                });
                group.put("groupName", takeName);
                firestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("PrivateGroup").add(group).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "bạn là trưởng nhóm", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_addGroupMemberFragment_to_addGroupNameFragment);
            }
        });


    }

    private void addIdFieldForGroup(String id) {
        firestore.collection("Group").document(id).update("groupId", id);
    }


    public void setCheckedAddToGroupMessenger(MemberModel memberModel) {

    }


}
