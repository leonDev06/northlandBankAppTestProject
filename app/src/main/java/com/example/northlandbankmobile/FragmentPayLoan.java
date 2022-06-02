package com.example.northlandbankmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentPayLoan extends Fragment {
    //This fragment's view
    private View view;

    //Widgets
    private TextView mAmount, mDateDue, mRefNum, mErrMsg;
    private Button mBtnPayLoan;

    //Helper Classes
    private TransactionManager transactionManager;
    private User user;

    //Member Variables
    private String amount, dateDue, refNum;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pay_loan, container, false);
        
        //Link Widgets
        mAmount = view.findViewById(R.id.payLoanFragAmount);
        mDateDue = view.findViewById(R.id.payLoanFragDateDue);
        mRefNum = view.findViewById(R.id.payLoanFragRefNum);
        mErrMsg = view.findViewById(R.id.fragPayLoanErrMsg);

        //Helper Classes
        transactionManager = new TransactionManager();
        user = new User();

        //Display data to user
        displayCurrentLoan();

        //Clickable buttons
        mBtnPayLoan = view.findViewById(R.id.payLoanFragPayButton);
        mBtnPayLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enoughBalance = Double.parseDouble(new User().getAccountBalance()) >= Double.parseDouble(amount);
                
                /*Makes sure that:
                    -The user has enough balance to pay his/her existing loan
                  Redirects the user to FragmentNoExistingLoan
                 */
                if(enoughBalance){
                    transactionManager.payUnpaidLoan();
                    replaceFragment(R.id.actLoanCreditsMainFragHolder, new FragmentNoExistingLoan());

                }else{
                    //Display the error to user
                    mErrMsg.setText("You currently don't have enough balance.");
                }
            }
        });
        
        return view;
    }

    private void displayCurrentLoan(){
        //Get user's active loan data and passes it to this fragment's variables

        amount = user.getActiveLoanAmount();
        refNum = user.getActiveLoanRefNum();
        dateDue = user.getActiveLoanDateDue();

        //Display data to TextViews
        mAmount.setText(amount);
        mRefNum.setText(refNum);
        mDateDue.setText(dateDue);
    }

    private void replaceFragment(int fragmentHolder, Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentHolder, fragment);
        fragmentTransaction.commit();
    }
}