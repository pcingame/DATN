package com.example.appchatdemo.fragment.GroupChat;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.adapter.ListGroupMemberAdapter;
import com.example.appchatdemo.interfaces.IClickCheckboxListener;
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddGroupMemberFragment extends Fragment {

    private ImageButton btnBack;
    private Button btnCreateGroup;
    private NavController navController;
    private RecyclerView rcvUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ListGroupMemberAdapter listGroupMemberAdapter;
    private UserViewModel userViewModel;
    private SearchView searchView;
    private String imgLinkDefaultAvatar = "https://firebasestorage.googleapis.com/v0/b/appmiochat.appspot.com/o/Photos%2Fd0742ce2f56f4d7ea522a9149e4b8658.png?alt=media&token=044fbbae-c5f0-4e4f-81d5-0cd31c50abc1";
    private List<String> listmember;
    private String pathLocation;

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

        Bundle bundle = this.getArguments();
        String takeName = bundle.getString("groupName");
        String takeAvatar = bundle.getString("groupAvatar");
        Uri uri = Uri.parse(bundle.getString("uri"));
        pathLocation = takeAvatar;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnBack = view.findViewById(R.id.btn_back_member);
        btnCreateGroup = view.findViewById(R.id.btn_accepted);
        searchView = view.findViewById(R.id.edt_search_member);
        navController = Navigation.findNavController(requireView());

        rcvUser = view.findViewById(R.id.rv_list_member);
        rcvUser.setHasFixedSize(true);
        rcvUser.setLayoutManager(new LinearLayoutManager(getContext()));


        listGroupMemberAdapter = new ListGroupMemberAdapter(getContext(), new IClickCheckboxListener() {
            @Override
            public void onCheckboxMemberListener(ArrayList<String> arrayList) {
                listmember = new ArrayList<>(arrayList);
            }
        });

        userViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                listGroupMemberAdapter.setUserModelList(userModels);
                rcvUser.setAdapter(listGroupMemberAdapter);
                rcvUser.setItemViewCacheSize(userModels.size());
                if (listGroupMemberAdapter != null) {
                    listGroupMemberAdapter.notifyItemInserted(0);
                }
            }
        });


        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore = FirebaseFirestore.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
                Date date = new Date(System.currentTimeMillis());
                String currentTime = formatter.format(date);
                listmember.add(auth.getCurrentUser().getUid());
                if (listmember.size() <= 2) {
                    FancyToast.makeText(getContext(), getString(R.string.validate_group_member), Toast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                } else {
                    HashMap<String, Object> group = new HashMap<>();
                    group.put("groupId", currentTime + ": " + takeName);
                    group.put("groupName", takeName);
                    group.put("host", auth.getCurrentUser().getUid());
                    if (uri != null) {
                        group.put("groupAvatar", uri.toString());
                    } else group.put("groupAvatar", imgLinkDefaultAvatar);
                    group.put("lastMessageGroupTime", date);
                    for (int i = 0; i < listmember.size(); i++) {
                        group.put("memberList", Arrays.asList(listmember.toArray()));
                    }

                    firestore.collection("Groups").document(currentTime + ": " + takeName).set(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    if (!pathLocation.equals("")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference(pathLocation);
                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                    }
                                });
                            }
                        });
                    }

                    FancyToast.makeText(getContext(), getString(R.string.create_group_success), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    getActivity().finish();
                }
            }
        });


        btnBack.setOnClickListener(view1 -> navController.navigate(R.id.action_addGroupMemberFragment_to_addGroupNameFragment));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listGroupMemberAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called as and when type even a sigle letter
                listGroupMemberAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
