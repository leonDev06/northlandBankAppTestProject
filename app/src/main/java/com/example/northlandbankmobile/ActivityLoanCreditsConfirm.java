package com.example.northlandbankmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityLoanCreditsConfirm extends AppCompatActivity {
    //Initialize Widgets
    private TextView mAmount, mRefNum, mCurrentDate, mDueDate;
    private Button buttonPrintPdf, buttonHome;

    int pageHeight = 1120;
    int pageWidth = 792;

    //Helper Class
    private Navigator navigator;

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private final String CLASS_NAME = "com.example.northlandbankmobile.ActivityLoanCreditsConfirm";

    //PDF variables
    private static final int PERMISSION_REQUEST_CODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.R)
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
        buttonPrintPdf = findViewById(R.id.fragSendMoneyBtnBack);
        buttonPrintPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadGeneratePdfLoan generatePdfLoan = new ThreadGeneratePdfLoan
                        (getApplicationContext(), mAmount, mCurrentDate, mDueDate, mRefNum);
                generatePdfLoan.start();
            }
        });
        buttonHome = findViewById(R.id.fragSendMoneyBtnSend);
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

    }

    //private helper functions
    //Display loan data on the UI
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