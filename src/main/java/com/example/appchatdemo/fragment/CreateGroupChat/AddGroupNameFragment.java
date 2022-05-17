package com.example.appchatdemo.fragment.CreateGroupChat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.DashBoardActivity;


public class AddGroupNameFragment extends Fragment {

    private Button btnAddMember;
    private ImageButton btnBack;
    private EditText edtGroupName;
    private NavController navController;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToDashBoardActivity();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_name_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddMember = view.findViewById(R.id.btnAddMember);
        btnBack = view.findViewById(R.id.btnBack_name);
        edtGroupName = view.findViewById(R.id.edtGroupName);
        navController = Navigation.findNavController(view);

        btnAddMember.setEnabled(true);



        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtGroupName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Nhóm phải có tên", Toast.LENGTH_SHORT).show();
                } else {
                    AddGroupMemberFragment fragment = new AddGroupMemberFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    Bundle result = new Bundle();
                    result.putString("groupName", edtGroupName.getText().toString());
                    fragment.setArguments(result);
                    transaction.replace(R.id.nav_host_fragment_create_group_chat, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToDashBoardActivity();
            }
        });
    }

    public void backToDashBoardActivity(){
        Intent intent = new Intent(getContext(), DashBoardActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        getActivity().finish();
    }


}