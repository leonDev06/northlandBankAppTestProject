package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySendMoney extends AppCompatActivity {
    //Widgets
    private TextView errorMessage;
    private EditText sendToUserName, sendAmount;
    private Button BUTTON_SEND;
    private FrameLayout fragmentHolder;

    //Helper Classes
    private Navigator navigator;
    private TransactionManager transactionManager;
    private User user;

    //Member Variables
    private String username, amount;


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
        fragmentHolder = findViewById(R.id.actSendMoneyFragHolder);

        //Initializes helper classes
        navigator = new Navigator(this);
        transactionManager = new TransactionManager();
        user = new User();


        //Clickable buttons
        BUTTON_SEND = findViewById(R.id.BUTTON_SEND);
        BUTTON_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Variables for readability
                username = sendToUserName.getText().toString();
                amount = sendAmount.getText().toString();

                //Filters for empty field / user has enough balance / user is not trying to send money to self
                //Redirects to confirmation activity after passing the filter
                if
                (!username.isEmpty() && !amount.isEmpty() && isValidAmount()){
                    double currentUserBalance = Double.parseDouble(Database.getUserBalance());
                    double amountToSend = Double.parseDouble(sendAmount.getText().toString());
                    errorMessage.setText("");
                    if(currentUserBalance>=amountToSend
                            && !sendToUserName.getText().toString().equals(user.getUserName())
                            && !(amountToSend <=0)){


                        Bundle bundle = new Bundle();
                        Fragment confirmationScreen = new FragmentSendMoneyConfirm();
                        bundle.putString(KEY_SEND_TO, sendToUserName.getText().toString());
                        bundle.putString(KEY_AMOUNT, sendAmount.getText().toString());
                        confirmationScreen.setArguments(bundle);
                        inflateFragment(R.id.actSendMoneyFragHolder, confirmationScreen);
                        Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    }else if (sendToUserName.getText().toString().equals(Database.getCurrentUser())){
                        errorMessage.setText("You can't send money to yourself.");
                    }else if(amountToSend <=0){
                        errorMessage.setText("Amount must be greater than 0");
                    }else{
                        errorMessage.setText("You don't have enough money to send this amount.");
                    }
                }else if(!isValidAmount()) {
                    errorMessage.setText("Invalid Amount");
                }
                else{
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

    //private helper methods
    private void inflateFragment(int layout, Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layout, fragment);
        fragmentTransaction.commit();
    }

    //Check if entered digits are valid
    private boolean isValidAmount(){
        int decimalPointCount=0;
        int decimalPointPosition=0;

        //Prevents user from entering characters that is not a digit or a decimal point.
        for(int i=0; i<amount.length(); i++){
            if(!Character.isDigit(amount.charAt(i)) && amount.charAt(i)!='.'){
                return  false;
            }
            //Check if the number is a decimal. Permits only 1 decimal point aka a dot.
            if(i>=2 && amount.charAt(i)=='.'){
                decimalPointCount++;
                if (decimalPointCount>1){
                    return false;
                }
            }else if(i<2 && amount.charAt(i)=='.'){
                return false;
            }

            //Makes sure that, if there are decimals, that there would only be 2 and only 2 decimal places
            if(amount.charAt(i)=='.'){
                decimalPointPosition=i;
                if(amount.length()-(decimalPointPosition+1) != 2){
                    return false;
                }
            }
        }
        //First digit must be a whole number
        if(amount.charAt(0)=='0' || amount.charAt(0)=='.'){
            return false;
        }

        //If the amount passed all the filters, the amount is valid. Return true.
        return true;
    }
}