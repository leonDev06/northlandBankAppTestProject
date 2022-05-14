package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivitySendMoneySuccess extends AppCompatActivity {
    private Button btnHome, btnPdf;
    private TextView mUserName, mFullName, mAmount, mDate, mRefNum;

    private TransactionManager transactionManager;
    private Navigator navigator;



    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivitySendMoney";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_success);

        //LinkWidgets
        mUserName = findViewById(R.id.sendToConfirm);
        mFullName = findViewById(R.id.fullnameConfirm);
        mAmount = findViewById(R.id.amountConfirm);
        mDate = findViewById(R.id.actSendSuccessDate);
        mRefNum = findViewById(R.id.actSendSuccesRefNum);


        transactionManager = new TransactionManager();
        navigator = new Navigator(this);
        navigator.setGoingToAnotherActivity(true);

        displayDataOnUi();

        //Clickable Buttons
        btnHome = findViewById(R.id.actSendSuccessBtnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityHome.class, true);
            }
        });
        btnPdf = findViewById(R.id.actSendSuccessBtnPdf);
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadGeneratePdfSendMoney generatePdf = new ThreadGeneratePdfSendMoney(mUserName, mAmount, mDate, mRefNum, getApplicationContext());
                generatePdf.start();
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

    //Private Methods

    //Retrieves data that was sent from FragmentSendMoneyConfirm and use it to display data on the corresponding TextView
    private void displayDataOnUi(){
        mUserName.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_USERNAME));
        mFullName.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_FULL_NAME));
        mAmount.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_AMOUNT_SENT));
        mDate.setText(new DateManager().getCurrentDate().toString());
        mRefNum.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_REF_NUM));
    }
}