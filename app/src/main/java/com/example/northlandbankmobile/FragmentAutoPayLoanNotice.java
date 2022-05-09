package com.example.northlandbankmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class FragmentAutoPayLoanNotice extends Fragment {
    //Widgets and Buttons
    private TextView mAmountPaid, mRefNum, mMessage;
    private Button mButtonConfirm;

    //Helper classes
    private User user;

    //Member Variables
    Double testData1;
    Double testData2;

    //This fragment's view
    private View view;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_auto_pay_loan_notice, container, false);

        //Link Widgets
        mMessage = view.findViewById(R.id.fragmentNoticePayLoanMessage);
        mAmountPaid = view.findViewById(R.id.fragmentNoticePayLoanAmount);
        mRefNum = view.findViewById(R.id.fragmentNoticePayLoanRefNum);
        mButtonConfirm = view.findViewById(R.id.fragAutoPayNoticeButtonConfirm);




        //Display data for this fragment
        displayData();
        deductAmountFromUser();

        //Clickable Buttons
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }

    private void displayData(){
        user = new User();
        testData1 = Double.parseDouble(user.getAccountBalance());
        testData2 = Double.parseDouble(user.getActiveLoanAmount());

        mAmountPaid.setText(user.getActiveLoanAmount());
        mRefNum.setText(user.getActiveLoanRefNum());
        if(testData1 < testData2){
            mMessage.setText(getString(R.string.fragAutoPayLoanNoticeNotEnoughBalance));
        }
    }
    private void deductAmountFromUser(){
        new TransactionManager(getActivity()).payExistingLoan();
    }
}