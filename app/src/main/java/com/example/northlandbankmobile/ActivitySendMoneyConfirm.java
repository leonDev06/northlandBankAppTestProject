package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ActivitySendMoneyConfirm extends AppCompatActivity {
    private Button SEND, BACK;
    private TextView sendToUserName, fullName, amount, message;
    private TransactionManager transactionManager;
    private Navigator navigator;

    private static final String KEY_SEND_TO = "sendTo";
    private static final String KEY_AMOUNT = "amount";

    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivitySendMoney";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_confirm);

        sendToUserName = findViewById(R.id.sendToConfirm);
        fullName = findViewById(R.id.fullnameConfirm);
        amount = findViewById(R.id.amountConfirm);
        message = findViewById(R.id.recVerifMessage);


        sendToUserName.setText(getIntent().getStringExtra(ActivitySendMoney.KEY_SEND_TO));
        fullName.setText(getFullName());
        amount.setText(getIntent().getStringExtra(ActivitySendMoney.KEY_AMOUNT));
        verifyRecipient();

        transactionManager = new TransactionManager();
        navigator = new Navigator(this);
        navigator.setGoingToAnotherActivity(true);


        SEND = findViewById(R.id.BUTTON_SEND_CONFIRM);
        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Performs sendMoney transaction on user confirmation
                transactionManager.sendMoney(sendToUserName.getText().toString(), amount.getText().toString());
                if(transactionManager.isTransactionSuccess()){
                    navigator.redirectTo(ActivityHome.class, true);
                }else{

                }
            }
        });
        BACK = findViewById(R.id.BUTTON_SEND_CONF_BACK);
        BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public void sendMoney(){

    }
    private String getFullName(){
        String fullName="";

        try {
            Scanner scan = new Scanner(Database.getMainDB());
            while (scan.hasNextLine()){
                String line = scan.nextLine();
                String[] lineData = line.split(",");
                if(lineData[3].equals(sendToUserName.getText().toString())){
                    fullName=fullName.concat(lineData[0] + " " + lineData[1]);
                    break;
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fullName;
    }

    private void verifyRecipient(){
        if(!fullName.getText().toString().isEmpty() && Database.getCurrentUser()!=sendToUserName.getText().toString()){
            message.setText("The name is displayed for verification purposes");
        }else if(fullName.getText().toString().isEmpty()){
            message.setText("User not found in Database. Be careful when proceeding.");
        }else{
            message.setText("Congratulations.");
        }
    }
}