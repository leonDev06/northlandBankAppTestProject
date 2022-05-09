package com.example.northlandbankmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentConfirmLogout extends Fragment {
    //Widgets
    private Button cancelButton;
    private Button confirmButton;
    //View
    private View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_logout_confirm, container, false);

        //Link Buttons
        cancelButton = view.findViewById(R.id.logoutCancelButton);
        confirmButton = view.findViewById(R.id.logoutConfirmButton);

        //Clickable buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginManager(getActivity()).logout();
            }
        });

        return view;
    }
}