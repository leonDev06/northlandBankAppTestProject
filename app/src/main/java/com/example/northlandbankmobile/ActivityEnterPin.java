package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ActivityEnterPin extends AppCompatActivity {
    //Widgets
    @SuppressWarnings("FieldCanBeLocal")
    private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven,
            btnEight, btnNine, btnZero, btnClear, btnEnter, btnLogout;
    private EditText mDigitOne, mDigitTwo, mDigitThree, mDigitFour;
    private TextView mCurrentUser, mErrMsg;

    //Helper Classes
    private Navigator navigator;
    private LoginManager loginManager;

    //Used to determine which activity triggered this EnterPin Activity
    private Class ActivityReturnActivity;
    private java.lang.String returnActivityName;

    //Used to retrieve data passed to this activity. Responsible for determining which activity to return to.
    private static final String KEY_FOR_ENTER_PIN = "EnterPinReturnClass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        //initialize helper objects
        navigator = new Navigator(this);
        loginManager = new LoginManager(this);

        //Get returnActivity passed by Activity that triggered this ActivityEnterPin class
        determineReturnActivity();

        //Link buttons
        btnOne = findViewById(R.id.buttonPin1);
        btnTwo = findViewById(R.id.buttonPin2);
        btnThree = findViewById(R.id.buttonPin3);
        btnFour = findViewById(R.id.buttonPin4);
        btnFive = findViewById(R.id.buttonPin5);
        btnSix = findViewById(R.id.buttonPin6);
        btnSeven = findViewById(R.id.buttonPin7);
        btnEight = findViewById(R.id.buttonPin8);
        btnNine = findViewById(R.id.buttonPin9);
        btnZero = findViewById(R.id.buttonPin0);
        btnClear = findViewById(R.id.buttonPinClear);
        btnEnter = findViewById(R.id.buttonPinConfirm);
        btnLogout = findViewById(R.id.buttonPinLogout);

        //Link EditText
        mDigitOne = findViewById(R.id.digit1);
        mDigitTwo = findViewById(R.id.digit2);
        mDigitThree = findViewById(R.id.digit3);
        mDigitFour = findViewById(R.id.digit4);
        //Link TextView
        mCurrentUser = findViewById(R.id.enterPinCurrentUser);
        mErrMsg = findViewById(R.id.errPinMsg);

        //Display the current user on the screen
        mCurrentUser.setText(new User().getUserName());

        //Create the Pin Check (Looping) Thread
        runPinCheckThread();

        //Clickable Buttons
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("1");
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("2");
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("3");
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("4");
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("5");
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("6");
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("7");
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("8");
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("9");
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("0");
            }
        });
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to the previous activity where the user left off if entered pin is correct
                if(isCorrectPin()) {
                    try{
                        navigator.redirectTo(ActivityReturnActivity, true);
                    }catch(Exception e) {
                        navigator.redirectTo(ActivityHome.class, true);
                    }
                }
                clearComposingText();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInput();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logout();
                navigator.redirectTo(ActivityLogin.class, true);
            }
        });

    }
    //Override Methods
    @Override
    public void onBackPressed(){
        //Exits app when user presses back button when on Enter Pin Activity
        this.finishAffinity();
    }

    //Private Helper Methods

    //Determines whether or not a digit slot is empty
    //If the slot is empty, type userInput into that slot (LTR)
    private void typeUserInput(String input){
        if(mDigitOne.getText().toString().isEmpty()){
            mDigitOne.setText(input);
        }else if(mDigitTwo.getText().toString().isEmpty()){
            mDigitTwo.setText(input);
        }else if(mDigitThree.getText().toString().isEmpty()){
            mDigitThree.setText(input);
        }else if(mDigitFour.getText().toString().isEmpty()){
            mDigitFour.setText(input);
        }
    }

    //Deletes one digit input per call (RTL)
    private void deleteInput(){
        if(!mDigitFour.getText().toString().isEmpty()){
            mDigitFour.setText("");
        }else if(!mDigitThree.getText().toString().isEmpty()){
            mDigitThree.setText("");;
        }else if(!mDigitTwo.getText().toString().isEmpty()){
            mDigitTwo.setText("");
        }else if(!mDigitOne.getText().toString().isEmpty()){
            mDigitOne.setText("");
        }
    }

    //Clears all 4 digits
    private void clearComposingText(){
        mDigitOne.setText("");
        mDigitTwo.setText("");
        mDigitThree.setText("");
        mDigitFour.setText("");
    }

    //Checks whether the enteredPin matches with the correctUserPin from the database
    private boolean isCorrectPin(){
        //Gets user entered pin and store it in a single string
        String enteredPin =
                mDigitOne.getText().toString() + "" + mDigitTwo.getText().toString() + "" + mDigitThree.getText().toString() + ""
                + mDigitFour.getText().toString();
        String correctUserPin="";

        //Retrieve the correct user pin from the database
        try {
            Scanner scanGetCorrectPin = new Scanner(Database.accessCurrentUserDataFile());
            String readUserData = scanGetCorrectPin.nextLine();
            String userData[] = readUserData.split(",");
            correctUserPin = userData[7];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Check if pins match
        return enteredPin.equals(correctUserPin);
    }

    //Set where the return activity should be.
    /*
    Return activity is where this activity should redirect to if the user has entered the pin correctly.
    This is determined by the data passed to this class by the activity where the user was
    before they triggered this EnterPin Activity
     */
    private void determineReturnActivity(){
        returnActivityName = getIntent().getStringExtra(KEY_FOR_ENTER_PIN);
        try {
            ActivityReturnActivity = Class.forName(returnActivityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*Create a thread that constantly checks if the pin entered by the user is correct
     without obligating the user to press OK button.
     */
    //This Thread will run indefinitely until the user entered his/her correct pin.
    //This will only check the entered pin once all 4 digits have been entered by the user.
    private void runPinCheckThread(){
        //Create the pinCheck Runnable
        Runnable pinCheck = new Runnable() {
            @Override
            public void run() {
                //Keep the thread alive/running until user has not entered correct pin.
                while (!isCorrectPin()){
                    //Check if all 4 digits are entered
                    if(!mDigitFour.getText().toString().isEmpty()){
                        //If correct pin is entered, redirect to return activity
                        if(isCorrectPin()) {
                            try{
                                navigator.redirectTo(ActivityReturnActivity, true);
                            }catch(Exception e) {
                                navigator.redirectTo(ActivityHome.class, true);
                            }
                        }else{
                            //Display error message (Incorrect Pin)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    clearComposingText();
                                    if(!isCorrectPin()){
                                        mErrMsg.setText("Incorrect Pin");
                                    }else{
                                        mErrMsg.setText("");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        };
        //Run the created runnable on a new thread
        new Thread(pinCheck).start();
    }

}