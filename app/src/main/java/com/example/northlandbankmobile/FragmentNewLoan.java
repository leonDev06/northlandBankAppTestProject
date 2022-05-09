package com.example.northlandbankmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class FragmentNewLoan extends Fragment {
    //Widgets
    private EditText amountLoan;
    private TextView errMsg;
    private Button buttonProcessLoan;

    //Helper Classes/Utility
    private Navigator navigator;
    private TransactionManager transactionManager;
    private User user;

    //Keys for passing data
    public static final String KEY_AMOUNT = "loanAmount";
    
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_loan_new, container, false);

        //Link widgets
        amountLoan = view.findViewById(R.id.amount_loan);
        errMsg = view.findViewById(R.id.loanErrMsg4);

        //Instantiates object classes
        transactionManager = new TransactionManager();
        navigator = new Navigator(getActivity());
        navigator.setGoingToAnotherActivity(false);

        user = new User();


        //Clickable Buttons
        buttonProcessLoan = view.findViewById(R.id.loan_button4);
        buttonProcessLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean amountOverLimit=null;
                Boolean amountUnderLimit=null;
                boolean noUserInput = amountLoan.getText().toString().isEmpty();
                /* Makes sure that:
                    -the amount text box is not empty
                    -amount is not less than 500PHP
                    -amount is not over 10,000PHP
                   Gives detail about transaction. Pass or Fail.
                 */
                if(!noUserInput){
                    amountOverLimit=Integer.parseInt(amountLoan.getText().toString())>10000;
                    amountUnderLimit=Integer.parseInt(amountLoan.getText().toString())<=100;
                    if(!amountOverLimit && !amountUnderLimit){
                        transactionManager.loanCredits(amountLoan.getText().toString());
                    }
                }
                //Proceeds to success screen if the transaction is passed
                if(transactionManager.isTransactionSuccess()){
                    navigator.getIntent().putExtra(KEY_AMOUNT, amountLoan.getText().toString());
                    navigator.redirectTo(ActivityLoanCreditsConfirm.class, true);
                }else{
                    //Check the error to display correct error message. Stay on current screen
                    if(user.hasActiveLoans()){
                        errMsg.setText("Please pay your existing loan first.");
                    } else if(noUserInput){
                        errMsg.setText("Please enter amount.");
                    } else if(amountOverLimit){
                        errMsg.setText("Amount can't exceed 10,000PHP");
                    }else if(amountUnderLimit){
                        errMsg.setText("Minimum loan is 100Php");
                    }else{
                        Log.d("loanError", "UnknownError");
                    }
                }
            }
        });
        return view;
    }


}