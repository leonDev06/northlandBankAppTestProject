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
    TextView mAmount, refNum, currentDate, dueDate;
    Button PRINT_PDF_BUTTON, HOME_BUTTON;

    //Helper Class
    Navigator navigator;

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivityLoanCreditsConfirm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_credits_confirm);

        //Initialize Helper classes
        navigator = new Navigator(this);
        navigator.setGoingToAnotherActivity(true);

        //Link Widgets
        mAmount = findViewById(R.id.loanSuccessAmount);
        refNum = findViewById(R.id.loanSuccessRefNum);
        currentDate = findViewById(R.id.loanSuccessCurrentDate);
        dueDate = findViewById(R.id.loanSuccessDueDate);

        //Display data
        displayData();

        //Test




        //Clickable Buttons
        PRINT_PDF_BUTTON = findViewById(R.id.loanSuccessPrintPdf);
        PRINT_PDF_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        HOME_BUTTON = findViewById(R.id.loanSuccessHome);
        HOME_BUTTON.setOnClickListener(new View.OnClickListener() {
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
        String amount = null;
        String date = null;
        String dateDue = null;
        String refNum = null;
        try {
            Scanner loansTable = new Scanner(Database.getLoansTable());
            String scannedLine;
            String[] loansDataIndex;
            while(loansTable.hasNextLine()){
                scannedLine = loansTable.nextLine();
                loansDataIndex = scannedLine.split(",");
                if(Database.getCurrentUser().equals(loansDataIndex[0]) && loansDataIndex[2].equals("unpaid")){
                    amount = loansDataIndex[1];
                    date = loansDataIndex[3];
                    dateDue = loansDataIndex[4];
                    refNum = loansDataIndex[5];
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mAmount.setText(amount);
        currentDate.setText(date);
        dueDate.setText(dateDue);
        this.refNum.setText(refNum);
    }

}