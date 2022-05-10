package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ActivityLoanCreditsConfirm extends AppCompatActivity {
    //Initialize Widgets
    private TextView mAmount, mRefNum, mCurrentDate, mDueDate;
    private Button buttonPrintPdf, buttonHome;

    //Helper Class
    private Navigator navigator;

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private final String CLASS_NAME = "com.example.northlandbankmobile.ActivityLoanCreditsConfirm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_credits_confirm);

        //Initialize Helper classes
        navigator = new Navigator(this);
        navigator.setGoingToAnotherActivity(true);

        //Link Widgets
        mAmount = findViewById(R.id.loanSuccessAmount);
        mRefNum = findViewById(R.id.loanSuccessRefNum);
        mCurrentDate = findViewById(R.id.loanSuccessCurrentDate);
        mDueDate = findViewById(R.id.loanSuccessDueDate);

        //Display data
        displayData();

        //Clickable Buttons
        buttonPrintPdf = findViewById(R.id.loanSuccessPrintPdf);
        buttonPrintPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        buttonHome = findViewById(R.id.loanSuccessHome);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Returns to home activity
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

    //private helper functions
    private void displayData(){
        User user = new User();
        String amount = user.getActiveLoanAmount();
        String date = user.getActiveLoanDateLoaned();
        String dateDue = user.getActiveLoanDateDue();
        String refNum = user.getActiveLoanRefNum();

        mAmount.setText(amount);
        mCurrentDate.setText(date);
        mDueDate.setText(dateDue);
        mRefNum.setText(refNum);
    }

}