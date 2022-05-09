package com.example.northlandbankmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class ActivityEnterPin extends AppCompatActivity {
    //Widgets
    private Button ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, ZERO, CLEAR, ENTER_PIN, LOGOUT;
    private EditText digitOne, digitTwo, digitThree, digitFour;
    private TextView currentUser;

    private Navigator navigator;
    private LoginManager loginManager;

    private Class ActivityReturnActivity;
    private java.lang.String returnActivityName;

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
        ONE = findViewById(R.id.buttonPin1);
        TWO = findViewById(R.id.buttonPin2);
        THREE = findViewById(R.id.buttonPin3);
        FOUR = findViewById(R.id.buttonPin4);
        FIVE = findViewById(R.id.buttonPin5);
        SIX = findViewById(R.id.buttonPin6);
        SEVEN = findViewById(R.id.buttonPin7);
        EIGHT = findViewById(R.id.buttonPin8);
        NINE = findViewById(R.id.buttonPin9);
        ZERO = findViewById(R.id.buttonPin0);
        CLEAR = findViewById(R.id.buttonPinClear);
        ENTER_PIN = findViewById(R.id.buttonPinConfirm);
        LOGOUT = findViewById(R.id.buttonPinLogout);

        //Link EditText
        digitOne = findViewById(R.id.digit1);
        digitTwo = findViewById(R.id.digit2);
        digitThree = findViewById(R.id.digit3);
        digitFour = findViewById(R.id.digit4);
        //Link TextView
        currentUser = findViewById(R.id.enterPinCurrentUser);

        //Display the current user on the screen
        currentUser.setText(Database.getCurrentUser());



        //Clickable Buttons
        ONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("1");
            }
        });
        TWO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("2");
            }
        });
        THREE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("3");
            }
        });
        FOUR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("4");
            }
        });
        FIVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("5");
            }
        });
        SIX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("6");
            }
        });
        SEVEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("7");
            }
        });
        EIGHT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("8");
            }
        });
        NINE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("9");
            }
        });
        ZERO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeUserInput("0");
            }
        });
        ENTER_PIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        CLEAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInput();
            }
        });
        LOGOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logout();
            }
        });

    }
    //Override Methods
    @Override
    public void onBackPressed(){
        this.finishAffinity();
    }

    //Private Helper Methods

    //Determines whether or not a digit slot is empty
    //If the slot is empty, type userInput into that slot (LTR)
    private void typeUserInput(String input){
        if(digitOne.getText().toString().isEmpty()){
            digitOne.setText(input);
        }else if(digitTwo.getText().toString().isEmpty()){
            digitTwo.setText(input);
        }else if(digitThree.getText().toString().isEmpty()){
            digitThree.setText(input);
        }else if(digitFour.getText().toString().isEmpty()){
            digitFour.setText(input);
        }else{

        }
    }

    //Deletes one digit input per call from RTL
    private void deleteInput(){
        if(!digitFour.getText().toString().isEmpty()){
            digitFour.setText("");
        }else if(!digitThree.getText().toString().isEmpty()){
            digitThree.setText("");;
        }else if(!digitTwo.getText().toString().isEmpty()){
            digitTwo.setText("");
        }else if(!digitOne.getText().toString().isEmpty()){
            digitOne.setText("");
        }
    }

    //Clears all 4 digits
    private void clearComposingText(){
        digitOne.setText("");
        digitTwo.setText("");
        digitThree.setText("");
        digitFour.setText("");
    }

    //Checks whether the enteredPin matches with the correctUserPin
    private boolean isCorrectPin(){
        String enteredPin;
        String correctUserPin="";
        enteredPin = digitOne.getText().toString() + "" +digitTwo.getText().toString() + "" + digitThree.getText().toString() + ""
                + digitFour.getText().toString();

        //Retrieve the correct user pin from the database
        try {
            Scanner scan = new Scanner(Database.getCurrentUserData());
            String readUserData = scan.nextLine();
            String userData[] = readUserData.split(",");
            correctUserPin = userData[7];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Check if pins match
        if(enteredPin.equals(correctUserPin)){
            return true;
        }
        return false;
    }

    private void determineReturnActivity(){
        returnActivityName = getIntent().getStringExtra(KEY_FOR_ENTER_PIN);
        try {
            ActivityReturnActivity = Class.forName(returnActivityName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}