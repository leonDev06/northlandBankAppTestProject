package com.example.northlandbankmobile;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivitySendMoneySuccess extends AppCompatActivity {
    private Button btnHome, btnPdf;
    private TextView mUserName, mFullName, mAmount, mDate, mRefNum;
    private ProgressBar progressBar;

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
        progressBar = findViewById(R.id.progressBar);


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
                generatePdfReceipt();
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

    //Retrieves data that was sent from FragmentSendMoneyConfirm and use it to display data on the corresponding TextView
    private void displayDataOnUi(){
        mUserName.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_USERNAME));
        mFullName.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_FULL_NAME));
        mAmount.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_AMOUNT_SENT));
        mDate.setText(new DateManager().getCurrentDate().toString());
        mRefNum.setText(getIntent().getStringExtra(FragmentSendMoneyConfirm.KEY_REF_NUM));
    }

    private void generatePdfReceipt(){
        ThreadGeneratePdfSendMoney generatePdf = new ThreadGeneratePdfSendMoney();
        generatePdf.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean runThread = true;
                while (runThread) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (generatePdf.isAlive()) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    if(!generatePdf.isAlive()){
                        runThread=false;
                    }
                }
            }
        }).start();
    }

    class ThreadGeneratePdfSendMoney extends Thread{
        public void run(){
            //See ActivityLoanCreditsConfirm's documentation on how generating a Pdf File works
            //Has the same flow as ActivityLoanConfirm's thread, but different information
            PdfDocument pdfDocument = new PdfDocument();

            Paint text = new Paint();

            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(792, 150, 1).create();

            PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);

            Canvas canvas = myPage.getCanvas();

            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            text.setTextSize(15);
            text.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            canvas.drawText("SEND MONEY", 209, 40, text);
            canvas.drawText(("RECEIVER: "+mUserName.getText().toString()), 209, 60, text);
            canvas.drawText(("AMOUNT: "+mAmount.getText().toString()), 209, 80, text);
            canvas.drawText(("DATE: "+mDate.getText().toString()), 209, 100, text);
            canvas.drawText(("REFERENCE NUMBER: "+mRefNum.getText().toString()), 209, 120, text);

            pdfDocument.finishPage(myPage);

            File file = new File(Database.commonDocumentDirPath
                    ("Northland Bank Receipts"), ("/SendMoney_"+Database.getCurrentUser()+"_"+mRefNum.getText().toString()+"_Receipt.pdf"));
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "PDF Receipt Stored in Documents Folder", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
        }
    }
}