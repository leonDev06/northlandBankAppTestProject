package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ActivityHome extends AppCompatActivity {
    //Widgets and Buttons
    private TextView userAccountNum, userFullName, currentBalance;
    private FrameLayout confirmExitFragment;
    private Button mButtonSendMoney;
    private Button mButtonLoanCredits;
    private Button mButtonMyPocket;
    private Button mButtonViewTransactions;
    private Button mButtonViewAbout;
    private Button mButtonLogout;
    private Button mButtonRefresh;

    //FragmentManager
    private FragmentManager fragmentManager;

    //Helper Classes
    private LoginManager loginManager;
    private Navigator navigator;
    private User user;

    //Member variables

    //Test

    //Keys for passing data
    //Guides which class the ActivityEnterPin returns back to
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";
    private static final String CLASS_NAME = "com.example.northlandbankmobile.ActivityHome";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("className",getClass().getName());
        //Link widgets
        userAccountNum = findViewById(R.id.accountNumOnCard);
        userFullName = findViewById(R.id.nameOnCard);
        currentBalance = findViewById(R.id.balance);
        confirmExitFragment = findViewById(R.id.homeConfirmFragContainer);

        //Initialize important variables for the activity
        Database.initDatabase();
        Database.prepareCurrentUserData();

        //Initialize helper objects/classes
        navigator = new Navigator(this);
        fragmentManager = getSupportFragmentManager();
        user = new User();

        loginManager = new LoginManager(this);

        //Display data for UI
        displayUserData();

        //Test
        checkForDueLoans();

        //Clickable Buttons
        mButtonSendMoney = findViewById(R.id.sendMoneyButton);
        mButtonSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivitySendMoney.class);
            }
        });
        mButtonLoanCredits = findViewById(R.id.BUTTON_LOAN_CREDITS);
        mButtonLoanCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityLoanCredits.class);
            }
        });
        mButtonMyPocket = findViewById(R.id.BUTTON_HOME_DETAILS);
        mButtonMyPocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityAccountView.class);
            }
        });
        mButtonViewTransactions = findViewById(R.id.BUTTON_TRANSACTIONS);
        mButtonViewTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityTransacRecords.class);
            }
        });
        mButtonViewAbout = findViewById(R.id.BUTTON_VIEW_ABOUT);
        mButtonViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigator.redirectTo(ActivityAbout.class);
            }
        });
        mButtonLogout = findViewById(R.id.logoutButton);
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentManager.findFragmentByTag("confirm_logout")==null){
                    inflateFragment(new FragmentConfirmLogout(), "confirm_logout");
                    navigator.setGoingToAnotherActivity(true);
                }
            }
        });
        mButtonRefresh = findViewById(R.id.actHomeButtonRefresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.syncUserDetailsData();
                displayUserData();
            }
        });

    }
    //Override Lifecycle Methods
    @Override
    protected void onPause(){
        super.onPause();
        if(getSupportFragmentManager().findFragmentByTag("confirm_exit")!=null){
            fragmentManager.popBackStack();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        //Re-initialize variables/Refreshes data everytime the HomeActivity is resumed
        user.syncUserDetailsData();
        displayUserData();

        if(!navigator.isGoingToAnotherActivity()){
            navigator.putExtra(KEY_FOR_ENTER_PIN, CLASS_NAME);
            navigator.redirectTo(ActivityEnterPin.class, true);
        }
        navigator.setGoingToAnotherActivity(false);
    }

    @Override
    public void onBackPressed(){
        //Prevent redirecting to EnterPinActivity
        navigator.setGoingToAnotherActivity(true);

        //If there's an active popup dialog, destroy it. Else, inflate confirm exit dialog
        if(fragmentManager.findFragmentByTag("confirm_exit")==null && fragmentManager.findFragmentByTag("confirm_logout")==null){
            inflateFragment(new FragmentConfirmExit(), "confirm_exit");
        }else{
            fragmentManager.popBackStack();
        }
    }

    //********Helper private methods*********
    //Checks for due loans. Automatically pays and deducts from user balance. Displays a popup on user screen
    private void checkForDueLoans(){
        DateManager dateManager = new DateManager();
        TransactionManager transactionManager = new TransactionManager();
        Log.d("checkStat", "activityHomeOnCreate");

        //Checks whether current date is before/equal to/after current existing loan's due date
        //The function to deduct amount from user balance is called in the fragment. (FragmentAutoPayLoanNotice)
        if(user.hasActiveLoans() && user.getActiveLoanDateDue().compareTo(dateManager.getCurrentDate().toString())<0){
            Fragment fragment = new FragmentAutoPayLoanNotice();
            inflateFragment(fragment);
        }else if(user.hasActiveLoans() && user.getActiveLoanDateDue().compareTo(dateManager.getCurrentDate().toString())>0){
            
        }
    }

    //Gets current user data and displays the data on the user's card
    private void displayUserData(){
        //Format for account balance to be displayed on screen
        DecimalFormat balanceFormat = new DecimalFormat("#,##0.00");

        //Displays user data on Card
        userAccountNum.setText(formatAccountNum(user.getAccountNumber()));
        userFullName.setText(user.getFirstName()+" "+user.getLastName());
        currentBalance.setText(balanceFormat.format(Double.parseDouble(user.getAccountBalance())));
    }
    //Own formatting function to format accountNum. Adds spaces to the account number to show on the card
    private String formatAccountNum(String num){
        String formattedAccNum="";
        for(int i=0; i<num.length(); i++){
            if(i==3 || i==4 || i==7){
                formattedAccNum = formattedAccNum.concat(" ");
                formattedAccNum = formattedAccNum.concat(" ");
            }
            formattedAccNum = formattedAccNum.concat(String.valueOf(num.charAt(i)));
        }
        return formattedAccNum;
    }


    private void inflateFragment(Fragment fragment, String tag){
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeConfirmFragContainer, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void inflateFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.homeConfirmFragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}