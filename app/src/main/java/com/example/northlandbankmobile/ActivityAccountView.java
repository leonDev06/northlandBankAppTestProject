package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ActivityAccountView extends AppCompatActivity {
    //Widgets
    TextView fullName, username, email, currentBalance, accountNumber;
    Button VIEW_RECORDS_BUTTON, BACK_HOME_BUTTON;

    //Helper Classes
    Navigator navigator;

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private final String CLASS_NAME = "com.example.northlandbankmobile.ActivityAccountView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        //Link widgets
        fullName = findViewById(R.id.detailsFullName);
        accountNumber = findViewById(R.id.detailsAccountNum);
        username = findViewById(R.id.detailsUsername);
        email = findViewById(R.id.detailsEmail);
        currentBalance = findViewById(R.id.detailsBalance);
        //Display User Info
        showUserDetails();

        //Initialize helper objects
        navigator = new Navigator(this);

        //Clickable buttons
        VIEW_RECORDS_BUTTON = findViewById(R.id.BUTTON_DETAILS_RECORD);
        VIEW_RECORDS_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityTransacRecords.class);
            }
        });

        BACK_HOME_BUTTON = findViewById(R.id.BUTTON_DETAILS_BACK);
        BACK_HOME_BUTTON.setOnClickListener(new View.OnClickListener() {
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
        if(!navigator.isGoingToAnotherActivity()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
        navigator.setGoingToAnotherActivity(false);
    }

    //Displays user info on the screen
    public void showUserDetails(){
        try {
            Scanner getUserData = new Scanner(Database.getCurrentUserData());
            String userDetailsLine = getUserData.nextLine();
            String[] userDetails = userDetailsLine.split(",");
            fullName.setText(userDetails[0]+" "+userDetails[1]);
            username.setText(userDetails[3]);
            accountNumber.setText(userDetails[5]);
            email.setText(userDetails[2]);
            currentBalance.setText(userDetails[6]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}