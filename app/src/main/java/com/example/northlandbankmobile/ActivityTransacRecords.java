package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ActivityTransacRecords extends AppCompatActivity {
    //Widgets
    private RecyclerView mReceiptView;
    private TextView currentDate;

    //Helper Classes
    private Navigator navigator;

    //Receipt Display
    private ArrayList<UserTransactions> mTransactions = new ArrayList<>();

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivityTransacRecords";

    //Pdf Variables
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transac_records);

        //Link Widgets
        mReceiptView = findViewById(R.id.mRecyclerReceipt);
        currentDate = findViewById(R.id.actTransactRecCurrentDate);

        //Helper Objects
        navigator = new Navigator(this);
        navigator.setGoingToAnotherActivity(true);

        //Setup Widgets Display
        setUpDisplay();

        //Load in Transaction Receipts
        setUpTransactionReceipts();
        ReceiptViewAdapter adapter = new ReceiptViewAdapter(this, mTransactions);
        mReceiptView.setAdapter(adapter);
        mReceiptView.setLayoutManager(new LinearLayoutManager(this));


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

    //private methods
    private void setUpDisplay(){
        currentDate.setText("Transactions as of "+new DateManager().getCurrentDate().toString());
    }

    private void setUpTransactionReceipts(){
        ArrayList<String> rRefNum = new ArrayList<>();
        ArrayList<String> rSender = new ArrayList<>();
        ArrayList<String> rReceiver = new ArrayList<>();
        ArrayList<String> rAmount = new ArrayList<>();
        ArrayList<String> rTransactType = new ArrayList<>();
        ArrayList<String> rTransactDate = new ArrayList<>();

        String scannedLine;
        String [] transactionData;
        try {
            Scanner getTransactions = new Scanner(Database.accessUserTransactions());
            while (getTransactions.hasNextLine()){
                scannedLine = getTransactions.nextLine();
                transactionData = scannedLine.split(",");
                rRefNum.add(transactionData[0]);
                rSender.add(transactionData[1]);
                rReceiver.add(transactionData[2]);
                rAmount.add(transactionData[3]);
                rTransactType.add(transactionData[4]);
                rTransactDate.add(transactionData[5]);
            }
            getTransactions.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i=0; i<rRefNum.size(); i++){
            mTransactions.add(new UserTransactions(rRefNum.get(i),
                    rSender.get(i),
                    rReceiver.get(i),
                    rAmount.get(i),
                    rTransactType.get(i),
                    rTransactDate.get(i)));
        }
    }

    //private methods


}