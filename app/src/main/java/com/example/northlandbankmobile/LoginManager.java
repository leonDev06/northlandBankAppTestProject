package com.example.northlandbankmobile;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.Scanner;

public class LoginManager {
    private Activity activity;


    private EditText firstNameRegister, lastNameRegister, emailRegister, userNameRegister, passwordRegister, confirmPassRegister;
    private EditText pinRegister, pinRegisterConfirm;
    private EditText userNameLogin, passwordLogin;
    private TextView invalidRegistrationMessage, invalidLoginMessage;
    private String accountNumber;
    private Double accountBalance;

    //Constructors
    public LoginManager(Activity act){
        this.activity=act;
        activity.getApplicationContext();
    }

    //Getters and Setters
    //Getters for EditText
    public EditText getFirstNameRegister() {
        return firstNameRegister;
    }
    public EditText getLastNameRegister() {
        return lastNameRegister;
    }
    public EditText getEmailRegister() {
        return emailRegister;
    }
    public EditText getUserNameRegister() {
        return userNameRegister;
    }
    public EditText getPasswordRegister() {
        return passwordRegister;
    }
    public EditText getConfirmPassRegister() {
        return confirmPassRegister;
    }
    public EditText getUserNameLogin() {
        return userNameLogin;
    }
    public EditText getPasswordLogin() {
        return passwordLogin;
    }
    //Getters for Strings
    public String getAccountNumber() {
        return accountNumber;
    }
    //Getters for Files/Database

    public File getCurrentUserData() {
        return Database.getCurrentUserData();
    }

    //Methods
    public void initializeWidgets(){
        if(activity.getClass()== ActivityLogin.class){
            userNameLogin = activity.findViewById(R.id.userName);
            passwordLogin = activity.findViewById(R.id.password);
            invalidLoginMessage = activity.findViewById(R.id.invalidLoginMessage);
        }else if(activity.getClass()== ActivityRegistration.class){
            firstNameRegister = activity.findViewById(R.id.firstNameRegister);
            lastNameRegister = activity.findViewById(R.id.lastNameRegister);
            emailRegister = activity.findViewById(R.id.emailRegister);
            userNameRegister = activity.findViewById(R.id.userNameRegister);
            passwordRegister = activity.findViewById(R.id.passwordRegister);
            confirmPassRegister = activity.findViewById(R.id.confirmPassRegister);
            pinRegister = activity.findViewById(R.id.pinRegister);
            pinRegisterConfirm = activity.findViewById(R.id.confirmPinRegister);
            invalidRegistrationMessage = activity.findViewById(R.id.message_status_reg);
        }

    }
    public boolean verifyRegistration(){
        boolean passwordsMatch= passwordsMatch();
        boolean pinsMatch = pinsMatch();
        boolean validEmail=validEmail();
        boolean noNullFields=noNullFields();
        boolean uniqueAccount=makeAccountUnique();
        boolean noInvalidCharacters=noInvalidCharacters();

        generateAccountNumber();
        if(passwordsMatch && noNullFields && validEmail && uniqueAccount && noInvalidCharacters && pinsMatch){
            registerToDatabase();
            clearText();
            return true;
        }
        return false;
    }
    public boolean verifyLogin(){

        boolean userFound=false;
        boolean loginVerified=false;
        
        String line;
        String[] accountsDetails;


        try {
            Scanner scan = new Scanner(Database.getMainDB().getAbsoluteFile());
            while (scan.hasNextLine()){
                line = scan.nextLine();
                accountsDetails=line.split(",");
                if(accountsDetails[3].equals(userNameLogin.getText().toString())){
                    userFound=true;
                    Log.d("UserFound", "Found");
                }
                if(accountsDetails[3].equals(userNameLogin.getText().toString()) && accountsDetails[4].equals(passwordLogin.getText().toString())){
                    persistLogIn();
                    loginVerified=true;
                    break;
                }
            }
            if(!userFound){
                invalidLoginMessage.setText("User not found.");
            }else if(!loginVerified){
                invalidLoginMessage.setText("Wrong Password");
            }else{
                invalidLoginMessage.setText("");
            }
            scan.close();
        } catch (FileNotFoundException e) {
            Log.d("LoginTag", "DBProblem");
            e.printStackTrace();
        }
        return loginVerified;
    }
    public void prepareUserData(){


    }
    public void logout(){
        Database.getCurrentlyLoggedInUserFile().delete();
        Database.getCurrentUserData().getAbsoluteFile().delete();
    }

    
    //PRIVATE FUNCTIONS
    //For Readability/Code Shortening functions
    private void registerToDatabase(){
        accountBalance = 5000.0;
        try {
            FileOutputStream writer = new FileOutputStream(Database.getMainDB().getAbsolutePath(), true);
            writer.write(firstNameRegister.getText().toString().getBytes());
            writer.write(",".getBytes());
            writer.write(lastNameRegister.getText().toString().getBytes());
            writer.write(",".getBytes());
            writer.write(emailRegister.getText().toString().getBytes());
            writer.write(",".getBytes());
            writer.write(userNameRegister.getText().toString().getBytes());
            writer.write(",".getBytes());
            writer.write(passwordRegister.getText().toString().getBytes());
            writer.write(",".getBytes());
            writer.write(accountNumber.getBytes());
            writer.write(",".getBytes());
            writer.write(accountBalance.toString().getBytes());
            writer.write(",".getBytes());
            writer.write(pinRegister.getText().toString().getBytes());
            writer.write("\n".getBytes());
            writer.close();
            Toast.makeText(activity.getApplicationContext(), "Registered Account", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(), "Account wasn't registered", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean passwordsMatch(){
            if(passwordRegister.getText().toString().length()<8){
                invalidRegistrationMessage.setText("Password must contain at least 8 characters.");
                return false;
            }
            if (passwordRegister.getText().toString().equals(confirmPassRegister.getText().toString())) {
                return true;
            } else {
                invalidRegistrationMessage.setText("Passwords don't match.");
                return false;
            }
    }
    private boolean pinsMatch(){
        if (pinRegister.getText().toString().equals(pinRegisterConfirm.getText().toString())) {
            return true;
        } else {
            //Check error to display correct message
            invalidRegistrationMessage.setText("Pins don't match");
            return false;
        }
    }
    private boolean noNullFields(){
        if (firstNameRegister.getText().toString().isEmpty()) {
            invalidRegistrationMessage.setText("First Name can't be empty");
            return false;
        }
        if(lastNameRegister.getText().toString().isEmpty()){
            invalidRegistrationMessage.setText("Last Name can't be empty");
            return false;
        }
        if (emailRegister.getText().toString().isEmpty()){
            invalidRegistrationMessage.setText("Email can't be empty");
            return false;
        }
        if(userNameRegister.getText().toString().isEmpty()){
            invalidRegistrationMessage.setText("Username can't be empty");
            return false;
        }
        return true;
    }
    private boolean noInvalidCharacters(){
        if(emailRegister.getText().toString().contains(",")){
            invalidRegistrationMessage.setText("Text fields can't contain ','");
            return false;
        }
        if(userNameRegister.getText().toString().contains(",")){
            invalidRegistrationMessage.setText("Text fields can't contain ','");
            return false;
        }
        if(passwordRegister.getText().toString().contains(",")){
            invalidRegistrationMessage.setText("Text Fields can't contain ','");
            return false;
        }
        return true;
    }
    private boolean validEmail(){
        if(emailRegister.getText().toString().contains("@neu.edu.ph")){
            return true;
        }
        if(!emailRegister.getText().toString().contains("@") || !emailRegister.getText().toString().contains(".com")){
            invalidRegistrationMessage.setText("Invalid Email");
            return false;
        }
        return true;
    }
    private boolean makeAccountUnique(){
        String line;
        String [] accountDetails; //0. firstName 1. lastName 2. email 3. username 4. password 5. Account Number
        try {
            Scanner scan = new Scanner(Database.getMainDB().getAbsoluteFile());
            while(scan.hasNextLine()){
                line = scan.nextLine();
                accountDetails = line.split(",");
                if(accountDetails[2].equals(emailRegister.getText().toString())){
                    invalidRegistrationMessage.setText("Email belongs to another account.");
                    return false;
                }
                if(accountDetails[3].equals(userNameRegister.getText().toString())){
                    invalidRegistrationMessage.setText("The username is already in use.");
                    return false;
                }
                if(accountDetails[5].equals(accountNumber)){
                    while(accountDetails[5].equals(accountNumber)){
                        generateAccountNumber();
                    }
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
    private void generateAccountNumber(){
        Random rnd = new Random();
        Integer rand = 1000000000 + rnd.nextInt(900000000);
        String accountNumber = rand.toString();
        this.accountNumber=accountNumber;
    }
    private void persistLogIn(){
        try {
            FileOutputStream fos = new FileOutputStream(Database.getCurrentlyLoggedInUserFile());
            fos.write(userNameLogin.getText().toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void clearText(){
        firstNameRegister.setText("");
        lastNameRegister.setText("");
        emailRegister.setText("");
        userNameRegister.setText("");
        passwordRegister.setText("");
        confirmPassRegister.setText("");
        invalidRegistrationMessage.setText("");
    }
}
