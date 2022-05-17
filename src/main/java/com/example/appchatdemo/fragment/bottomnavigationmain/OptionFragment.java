package com.example.appchatdemo.fragment.bottomnavigationmain;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.PrivateMessageActivity;
import com.example.appchatdemo.adapter.IClickItemUserListener;
import com.example.appchatdemo.adapter.UserAdapter;
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class OptionFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    RecyclerView rcvUser;
    UserAdapter mUserAdapter;
    UserViewModel userViewModel;
    SearchView searchUserContact;
    List<UserModel> userList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!searchUserContact.isIconified()){
                    searchUserContact.setIconified(true);
                    return;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        searchUserContact = view.findViewById(R.id.contactSearchView);
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchUserContact.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchUserContact.setMaxWidth(Integer.MAX_VALUE);

        rcvUser = view.findViewById(R.id.recyclerViewUser);
        rcvUser.setHasFixedSize(true);
        rcvUser.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();

        mUserAdapter = new UserAdapter( new IClickItemUserListener() {
            @Override
            public void onClickItemUser(UserModel userModel) {
                onClickGoToPrivateMessage(userModel);   ;
            }
        });

        userViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                mUserAdapter.setUserModelList(userModels);
                rcvUser.setAdapter(mUserAdapter);
                if(mUserAdapter != null){
                    mUserAdapter.notifyItemInserted(0);
                    mUserAdapter.notifyDataSetChanged();
                }
            }

        });

        searchUserContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mUserAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called as and when type even a sigle letter
                mUserAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    private void onClickGoToPrivateMessage(UserModel userModel) {
        Intent intent = new Intent(getContext(), PrivateMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userModel", userModel);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }
}