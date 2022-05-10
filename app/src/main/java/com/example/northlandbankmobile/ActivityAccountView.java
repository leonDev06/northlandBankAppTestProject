package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Scanner;

public class ActivityAccountView extends AppCompatActivity {
    //Widgets
    private TextView mFullName, mUsername, mEmail, mCurrentBalance, mAccountNumber;
    private Button buttonViewRecords, buttonHome;

    //Helper Classes
    private Navigator navigator;

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private final String CLASS_NAME = "com.example.northlandbankmobile.ActivityAccountView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        //Link widgets
        mFullName = findViewById(R.id.detailsFullName);
        mAccountNumber = findViewById(R.id.detailsAccountNum);
        mUsername = findViewById(R.id.detailsUsername);
        mEmail = findViewById(R.id.detailsEmail);
        mCurrentBalance = findViewById(R.id.detailsBalance);

        //Display User Info
        showUserDetails();

        //Initialize helper objects
        navigator = new Navigator(this);

        //Clickable buttons
        buttonViewRecords = findViewById(R.id.BUTTON_DETAILS_RECORD);
        buttonViewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityTransacRecords.class);
            }
        });

        buttonHome = findViewById(R.id.BUTTON_DETAILS_BACK);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    //Overwrite Lifecycle Methods
    @Override
    protected void onPause(){
        super.onPause();

    }

    @Override
    protected void onResume(){
        super.onResume();

        /*Redirect user to Enter Pin Activity whenever they leave the app
            This is done to provide additional security to users
         */
        if(!navigator.isGoingToAnotherActivity()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
        navigator.setGoingToAnotherActivity(false);
    }

    //Displays user info on the screen
    public void showUserDetails(){
        try {
            User user = new User();
            mFullName.setText(user.getFirstName() + " " +  user.getLastName());
            mUsername.setText(user.getUserName());
            mAccountNumber.setText(user.getAccountNumber());
            mEmail.setText(user.getEmail());
            mCurrentBalance.setText(user.getAccountBalance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}