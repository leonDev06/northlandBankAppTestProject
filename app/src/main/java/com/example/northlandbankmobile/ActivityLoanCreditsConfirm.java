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
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityLoanCreditsConfirm extends AppCompatActivity {
    //Initialize Widgets
    private TextView mAmount, mRefNum, mCurrentDate, mDueDate;
    private Button buttonPrintPdf, buttonHome;
    private ProgressBar progressBar;

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
        progressBar = findViewById(R.id.loanProgressBar);

        //Display data
        displayData();

        //Clickable Buttons
        buttonPrintPdf = findViewById(R.id.fragSendMoneyBtnBack);
        buttonPrintPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePdfReceipt();
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
        if(!navigator.isGoingToAnotherActivity()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
        navigator.setGoingToAnotherActivity(false);
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

    private void generatePdfReceipt(){
        //Start the thread that generates the PDF
        //This thread will keep running until a PDF receipt has been generated
        ThreadGeneratePdfLoan generatePdfLoan = new ThreadGeneratePdfLoan();
        generatePdfLoan.start();

        //Start the thread that handles whether to show or not the spinning loading icon
        //This thread will display the progress icon while the generate PDF thread is running, and will hide it when the PDF thread finishes
        //This thread will keep running until PDF receipt has been generated
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean runThread = true;
                while (runThread) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (generatePdfLoan.isAlive()) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    if(!generatePdfLoan.isAlive()){
                        runThread=false;
                    }
                }
            }
        }).start();
    }


    //Thread Class for generating a PDF receipt
    class ThreadGeneratePdfLoan extends Thread {

        public void run() {
            //Creates a PdfDocument
            PdfDocument pdfDocument = new PdfDocument();

            //The painter which will be used to paint inside the canvas
            Paint text = new Paint();

            //Create the PDF page information
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(792, 150, 1).create();
            PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);

            //The canvas wherein the information will be drawn. The page of the PDF.
            Canvas canvas = myPage.getCanvas();

            //Initialize and setup the painter.
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            text.setTextSize(15);
            text.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            //Paint the information
            canvas.drawText("LOAN", 209, 40, text);
            canvas.drawText(("AMOUNT: " + mAmount.getText().toString()), 209, 60, text);
            canvas.drawText(("DATE: " + mCurrentDate.getText().toString()), 209, 80, text);
            canvas.drawText(("DATE DUE: " + mDueDate.getText().toString()), 209, 100, text);
            canvas.drawText(("REFERENCE NUMBER: " + mRefNum.getText().toString()), 209, 120, text);

            //Finish
            pdfDocument.finishPage(myPage);

            //Create a PDF file in the phone storage
            File file = new File(Database.commonDocumentDirPath
                    ("Northland Bank Receipts"), ("/Loan_" + mRefNum.getText().toString() + "_Receipt.pdf"));

            //Store the generated Pdf info in the PDF file created in the phone storage
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