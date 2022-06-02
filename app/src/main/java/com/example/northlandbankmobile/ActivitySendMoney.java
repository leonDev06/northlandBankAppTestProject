package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ActivitySendMoney extends AppCompatActivity {
    //Widgets
    private TextView errorMessage;
    private EditText mUsername, mAmount;
    private Button mBtnSend;
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
        mUsername = findViewById(R.id.sendToUserName);
        mAmount = findViewById(R.id.sendAmount);
        errorMessage = findViewById(R.id.sendMoneyErrMsg);
        fragmentHolder = findViewById(R.id.actSendMoneyFragHolder);

        //Initializes helper classes
        navigator = new Navigator(this);
        transactionManager = new TransactionManager();
        user = new User();

        //Clickable buttons
        mBtnSend = findViewById(R.id.BUTTON_SEND);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Variables for readability
                username = mUsername.getText().toString();
                amount = mAmount.getText().toString();

                //Filters for empty field / user has enough balance / user is not trying to send money to self
                //Inflate confirmation fragment if filters are passed
                if
                (!username.isEmpty() && !amount.isEmpty()
                        && transactionManager.isValidAmount(amount)){
                    double currentUserBalance = Double.parseDouble(user.getAccountBalance());
                    double amountToSend = Double.parseDouble(mAmount.getText().toString());
                    errorMessage.setText("");
                    if(currentUserBalance>=amountToSend
                            && !mUsername.getText().toString().equals(user.getUserName())
                            && !(amountToSend <=0)){
                        //Filters passed. Begin Transaction
                        //Pass the required transaction details to FragmentConfirmation and launch Fragment
                        Bundle bundle = new Bundle();
                        Fragment confirmationScreen = new FragmentSendMoneyConfirm();
                        bundle.putString(KEY_SEND_TO, mUsername.getText().toString());
                        bundle.putString(KEY_AMOUNT, mAmount.getText().toString());
                        confirmationScreen.setArguments(bundle);
                        inflateFragment(R.id.actSendMoneyFragHolder, confirmationScreen);

                        hideKeyboard();
                        setTextFieldsEnabled(false);

                        //Transaction not initiated. Invalidated. Display error message to user.
                    }else if (mUsername.getText().toString().equals(user.getUserName())){
                        errorMessage.setText("You can't send money to yourself.");
                    }else if(amountToSend <=0){
                        errorMessage.setText("Amount must be greater than 0");
                    }else{
                        errorMessage.setText("You don't have enough money to send this amount.");
                    }
                }else if(!transactionManager.isValidAmount(amount)) {
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
    protected void onResume(){
        super.onResume();
        if(!navigator.isGoingToAnotherActivity()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
        navigator.setGoingToAnotherActivity(false);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        setTextFieldsEnabled(true);
    }

    //private helper methods
    private void inflateFragment(int layout, Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    //Used to make the text fields non=clickable when the confirmation fragment pops up and clickable if not
    public void setTextFieldsEnabled(boolean enabled){
        mUsername.setEnabled(enabled);
        mAmount.setEnabled(enabled);
    }
    //Hide the Keyboard when called. Used for better UI
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fragmentHolder.getWindowToken(), 0);
    }


}