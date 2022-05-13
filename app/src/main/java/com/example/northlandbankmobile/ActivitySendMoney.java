package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivitySendMoney extends AppCompatActivity {
    //Widgets
    private TextView errorMessage;
    private EditText sendToUserName, sendAmount;
    private Button BUTTON_SEND;

    //Helper Classes
    private Navigator navigator;
    private TransactionManager transactionManager;
    private User user;


    //Keys for Passing Data
    //Data keys for transferring data to confirmation activity
    public static final String KEY_SEND_TO  = "sendTo";
    public static final String KEY_AMOUNT = "amount";
    //DATA to pass to enter pin to set this class as its return class (Class to redirect to when entering correct pin.)
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivitySendMoney";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        //Link Widgets
        sendToUserName = findViewById(R.id.sendToUserName);
        sendAmount = findViewById(R.id.sendAmount);
        errorMessage = findViewById(R.id.sendMoneyErrMsg);

        //Initializes helper classes
        navigator = new Navigator(this);
        transactionManager = new TransactionManager();
        user = new User();


        //Clickable buttons
        BUTTON_SEND = findViewById(R.id.BUTTON_SEND);
        BUTTON_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Filters for empty field / user has enough balance / user is not trying to send money to self
                //Redirects to confirmation activity after passing the filter
                if
                (!sendToUserName.getText().toString().isEmpty() && !sendAmount.getText().toString().isEmpty()){
                    double currentUserBalance = Double.parseDouble(Database.getUserBalance());
                    double amountToSend = Double.parseDouble(sendAmount.getText().toString());
                    errorMessage.setText("");
                    if(currentUserBalance>=amountToSend
                            && !sendToUserName.getText().toString().equals(user.getUserName())
                            && !(amountToSend <=0)){
                        navigator.getIntent().putExtra(KEY_SEND_TO, sendToUserName.getText().toString());
                        navigator.getIntent().putExtra(KEY_AMOUNT, sendAmount.getText().toString());
                        navigator.redirectTo(ActivitySendMoneyConfirm.class);
                    }else if (sendToUserName.getText().toString().equals(Database.getCurrentUser())){
                        errorMessage.setText("You can't send money to yourself.");
                    }else if(!(amountToSend <=0)){
                        errorMessage.setText("Invalid amount.");
                    }
                    else{
                        errorMessage.setText("You don't have enough money to send this amount.");
                    }
                }else{
                    errorMessage.setText("Please fill-up the fields.");
                }
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

}