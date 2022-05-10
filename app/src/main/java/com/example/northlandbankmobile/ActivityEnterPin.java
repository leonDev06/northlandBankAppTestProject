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
    private Button buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive, buttonSix, buttonSeven,
            buttonEight, buttonNine, buttonZero, buttonClear, buttonEnter, buttonLogout;
    private EditText mDigitOne, mDigitTwo, mDigitThree, mDigitFour;
    private TextView mCurrentUser;

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
        buttonOne = findViewById(R.id.buttonPin1);
        buttonTwo = findViewById(R.id.buttonPin2);
        buttonThree = findViewById(R.id.buttonPin3);
        buttonFour = findViewById(R.id.buttonPin4);
        buttonFive = findViewById(R.id.buttonPin5);
        buttonSix = findViewById(R.id.buttonPin6);
        buttonSeven = findViewById(R.id.buttonPin7);
        buttonEight = findViewById(R.id.buttonPin8);
        buttonNine = findViewById(R.id.buttonPin9);
        buttonZero = findViewById(R.id.buttonPin0);
        buttonClear = findViewById(R.id.buttonPinClear);
        buttonEnter = findViewById(R.id.buttonPinConfirm);
        buttonLogout = findViewById(R.id.buttonPinLogout);

        //Link EditText
        mDigitOne = findViewById(R.id.digit1);
        mDigitTwo = findViewById(R.id.digit2);
        mDigitThree = findViewById(R.id.digit3);
        mDigitFour = findViewById(R.id.digit4);
        //Link TextView
        mCurrentUser = findViewById(R.id.enterPinCurrentUser);

        //Display the current user on the screen
        mCurrentUser.setText(Database.getCurrentUser());

        //Clickable Buttons
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("1");
            }
        });
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("2");
            }
        });
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("3");
            }
        });
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("4");
            }
        });
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("5");
            }
        });
        buttonSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("6");
            }
        });
        buttonSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("7");
            }
        });
        buttonEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("8");
            }
        });
        buttonNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("9");
            }
        });
        buttonZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("0");
            }
        });
        buttonEnter.setOnClickListener(new View.OnClickListener() {
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
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInput();
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logout();
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
            Scanner scanGetCorrectPin = new Scanner(Database.getCurrentUserData());
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
    This is determined by the data passed to this class by the activity that the user was before they triggered this EnterPin Activity
     */
    private void determineReturnActivity(){
        returnActivityName = getIntent().getStringExtra(KEY_FOR_ENTER_PIN);
        try {
            ActivityReturnActivity = Class.forName(returnActivityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}