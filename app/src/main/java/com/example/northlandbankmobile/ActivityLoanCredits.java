package com.example.northlandbankmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityLoanCredits extends AppCompatActivity {
    //Widgets
    private Button buttonNewLoan, buttonPayExistingLoan;
    private Fragment fragment;

    //Helper Classes/Utility
    private TransactionManager transactionManager;
    private FragmentManager fragmentManager;
    private User user;
    private Navigator navigator;

    //Keys for passing data
    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private final String CLASS_NAME = "com.example.northlandbankmobile.ActivityLoanCredits";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_credits);

        //Helper classes
        transactionManager = new TransactionManager();
        fragmentManager = getSupportFragmentManager();
        user = new User();
        navigator = new Navigator(this);
        navigator.setGoingToAnotherActivity(true);

        //Decides which fragment to display initially on Activity's onCreate
        displayInitialFragmentOnCreate();

        //Clickable Buttons
        buttonNewLoan = findViewById(R.id.actLoanCreditsButtonNewLoan);
        buttonNewLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragmentView(R.id.actLoanCreditsMainFragHolder, new FragmentNewLoan());
            }
        });
        buttonPayExistingLoan = findViewById(R.id.buttonPayLoan);
        buttonPayExistingLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.hasActiveLoans()){
                    fragment = new FragmentPayLoan();
                }else{
                    fragment = new FragmentNoExistingLoan();
                }
                replaceFragmentView(R.id.actLoanCreditsMainFragHolder, fragment);
            }
        });
    }

    //Replace the fragment view
    private void replaceFragmentView(int fragmentHolder, Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentHolder, fragment);
        fragmentTransaction.commit();
    }

    private void displayInitialFragmentOnCreate(){
        if(user.hasActiveLoans()){
            fragment = new FragmentPayLoan();
        }else{
            fragment = new FragmentNewLoan();
        }
        replaceFragmentView(R.id.actLoanCreditsMainFragHolder, fragment);
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
}