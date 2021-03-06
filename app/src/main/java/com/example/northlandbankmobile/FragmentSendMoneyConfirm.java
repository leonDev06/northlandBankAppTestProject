package com.example.northlandbankmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Scanner;


public class FragmentSendMoneyConfirm extends Fragment {
    //This Fragment's view
    private View view;
    
    //Widgets
    private Button mBtnSend;
    private TextView sendToUserName, fullName, amount, message;
    private TransactionManager transactionManager;
    private Navigator navigator;

    //Keys for passing data
    public static final String KEY_AMOUNT_SENT = "fragmentSendConfirmAmountSentKey";
    public static final String KEY_USERNAME = "fragmentSendConfirmUsernameKey";
    public static final String KEY_FULL_NAME  = "fragmentSendConfirmFullNameKey";
    public static final String KEY_REF_NUM = "fragmentSendConfirmrRefNumKey";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_send_money_confirm, container, false);

        //Link Widgets
        sendToUserName = view.findViewById(R.id.fragSendMoneyRecipient);
        fullName = view.findViewById(R.id.fragSendMoneyFullName);
        amount = view.findViewById(R.id.fragSendMoneyAmount);
        message = view.findViewById(R.id.fragSendMoney);

        //Retrieve passed bundle arguments
        sendToUserName.setText(getArguments().getString(ActivitySendMoney.KEY_SEND_TO));
        amount.setText(getArguments().getString(ActivitySendMoney.KEY_AMOUNT));

        fullName.setText(getFullName());

        //Check if the recipient is a user or not of the app
        verifyRecipient();

        //Initialize helper objects
        transactionManager = new TransactionManager();
        navigator = new Navigator(getActivity());
        navigator.setGoingToAnotherActivity(true);

        //Clickable buttons
        mBtnSend = view.findViewById(R.id.fragSendMoneyBtnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Performs sendMoney transaction on user confirmation
                transactionManager.sendMoney(sendToUserName.getText().toString(), amount.getText().toString());
                if(transactionManager.isTransactionSuccess()){
                    navigator.putExtra(KEY_AMOUNT_SENT, amount.getText().toString());
                    navigator.putExtra(KEY_USERNAME, sendToUserName.getText().toString());
                    navigator.putExtra(KEY_FULL_NAME, getFullName());
                    navigator.putExtra(KEY_REF_NUM, transactionManager.getReferenceNumber());
                    navigator.redirectTo(ActivitySendMoneySuccess.class, true);
                }
            }
        });
        return view;
    }

    //Formats the full name of the recipient
    private String getFullName(){
        String fullName="";
        try {
            Scanner scan = new Scanner(Database.accessUsersTable());
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

    //Check if the receiver is a user or not of the app
    private void verifyRecipient(){
        if(!fullName.getText().toString().isEmpty() && !Database.getCurrentUser().equals(sendToUserName.getText().toString())){
            message.setText("The name is displayed for verification purposes");
        }else if(fullName.getText().toString().isEmpty()){
            message.setText("User not found in Database. Be careful when proceeding.");
        }else{
            message.setText("Congratulations.");
        }
    }
}