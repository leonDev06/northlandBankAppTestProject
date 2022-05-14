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

                generatePdf();
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

    private void generatePdf(){
        PdfDocument pdfDocument = new PdfDocument();

        Paint text = new Paint();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);

        Canvas canvas = myPage.getCanvas();

        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        text.setTextSize(15);
        text.setColor(ContextCompat.getColor(this, R.color.black));

        canvas.drawText("LOAN", 209, 100, text);
        canvas.drawText(("AMOUNT: "+mAmount.getText().toString()), 209, 80, text);
        canvas.drawText(("DATE: "+mCurrentDate.getText().toString()), 209, 60, text);
        canvas.drawText(("DUE: "+mDueDate.getText().toString()), 209, 40, text);
        canvas.drawText(("REFERENCE NUMBER: "+mRefNum.getText().toString()), 209, 20, text);

        pdfDocument.finishPage(myPage);


        File file = new File(commonDocumentDirPath("Northland Bank Receipts"), ("/"+mRefNum.getText().toString()+"Receipt.pdf"));

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF Receipt Stored in Documents Folder", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "PDFGeneratednOT", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        pdfDocument.close();

    }

    public static File commonDocumentDirPath(String name){
        File dir = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+name);
        }else{
            dir = new File(Environment.getExternalStorageDirectory()+"/"+name);
        }

        if(!dir.exists()){
            boolean success = dir.mkdirs();
            if(!success){
                dir = null;
            }
        }
        return dir;
    }
}