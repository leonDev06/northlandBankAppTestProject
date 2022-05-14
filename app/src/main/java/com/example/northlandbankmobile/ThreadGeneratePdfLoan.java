package com.example.northlandbankmobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ThreadGeneratePdfLoan extends Thread{
    private Context context;
    private TextView mAmount;
    private TextView mDate;
    private TextView mDueDate;
    private TextView mRefNum;

    public ThreadGeneratePdfLoan(Context context, TextView mAmount, TextView mDate, TextView mDueDate, TextView mRefNum) {
        this.context = context;
        this.mAmount = mAmount;
        this.mDate = mDate;
        this.mDueDate = mDueDate;
        this.mRefNum = mRefNum;
    }

    public void run(){
        PdfDocument pdfDocument = new PdfDocument();

        Paint text = new Paint();

        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(792, 150, 1).create();

        PdfDocument.Page myPage = pdfDocument.startPage(myPageInfo);

        Canvas canvas = myPage.getCanvas();

        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        text.setTextSize(15);
        text.setColor(ContextCompat.getColor(context, R.color.black));

        canvas.drawText("LOAN", 209, 40, text);
        canvas.drawText(("AMOUNT: "+mAmount.getText().toString()), 209, 60, text);
        canvas.drawText(("DATE: "+mDate.getText().toString()), 209, 80, text);
        canvas.drawText(("DATE DUE: "+mDueDate.getText().toString()), 209, 100, text);
        canvas.drawText(("REFERENCE NUMBER: "+mRefNum.getText().toString()), 209, 110, text);

        pdfDocument.finishPage(myPage);


        File file = new File(Database.commonDocumentDirPath
                ("Northland Bank Receipts"), ("/Loan_"+mRefNum.getText().toString()+"_Receipt.pdf"));

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Looper.prepare();
            Toast.makeText(context, "PDF Receipt Stored in Documents Folder", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }
}
