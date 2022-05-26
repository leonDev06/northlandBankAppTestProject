package com.example.northlandbankmobile;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class LoginManager {
    private Activity activity;

    //Widgets for both ActivityLogin and ActivityRegistration are initialized here
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

    //booleans
    private boolean isLoginSuccess;
    private boolean isRegistrationSuccess;

    //Getters
    public EditText getPasswordLogin() {
        return passwordLogin;
    }
    public boolean isLoginSuccess() {
        return isLoginSuccess;
    }
    public boolean isRegistrationSuccess() {
        return isRegistrationSuccess;
    }

    //LoginManager FUNCTIONS
    //This initializes the Widgets of the login/registration activity.
    // Widgets Initialized will depend on where this method is called
    public void initializeWidgets(){
        if(activity.getClass().equals(ActivityLogin.class)){
            userNameLogin = activity.findViewById(R.id.userName);
            passwordLogin = activity.findViewById(R.id.password);
            invalidLoginMessage = activity.findViewById(R.id.invalidLoginMessage);
        }else if(activity.getClass().equals(ActivityRegistration.class)){
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
    //Verify the attempted registration of the user.
    public void attemptRegistration(){
        //Initialize the conditions that the registration attempt needs to pass
        boolean passwordsMatch= isValidPassword();
        boolean pinsMatch = isValidPin();
        boolean validEmail= iValidEmail();
        boolean noNullFields=noNullFields();
        boolean uniqueAccount=makeAccountUnique();
        boolean noInvalidCharacters=noInvalidCharacters();

        //Generate a random account number. Made unique with makeAccountUnique function
        generateAccountNumber();
        //If all conditions are met, registration is valid. Register user account to Database.
        if(passwordsMatch && noNullFields && validEmail && uniqueAccount && noInvalidCharacters && pinsMatch){
            registerToDatabase();
            clearText();
            isRegistrationSuccess=true;
        }
    }

    //Verify the attempted login
    public void isValidLogin(){
        boolean userFound=false;
        boolean loginVerified=false;


        //Start searching for the entered username in the database
        String line;
        String[] accountsDetails;
        try {
            Scanner scan = new Scanner(Database.accessUsersTable());
            while (scan.hasNextLine()){
                line = scan.nextLine();
                accountsDetails=line.split(",");
                //The user is found in the database. Set userFound to true. Important for setting correct error message
                if(accountsDetails[3].equals(userNameLogin.getText().toString())){
                    userFound=true;
                }
                //Conditions are met. Entered username matches with its corresponding password. Valid Login
                if(accountsDetails[3].equals(userNameLogin.getText().toString()) && accountsDetails[4].equals(passwordLogin.getText().toString())){
                    persistLogIn();
                    isLoginSuccess=true;
                    loginVerified=true;
                    break;
                }
            }
            //If there are errors, display corresponding error messages to user.
            if(!userFound){
                invalidLoginMessage.setText("User not found.");
            }else if(!loginVerified){
                invalidLoginMessage.setText("Wrong Password");
            }else{
                invalidLoginMessage.setText("");
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Used to remove the login data of the current user.
    public void logout(){
        Database.accessCurrentlyLoggedInUserFile().delete();
        Database.accessCurrentUserDataFile().getAbsoluteFile().delete();
    }
    //END OF LoginManager FUNCTIONS

    
    //PRIVATE FUNCTIONS

    //Called if the attempted registration is valid. Used to Register new account to database.
    private void registerToDatabase(){
        //As this is a mock app, users start with 5,000Php in their pcokets.
        accountBalance = 5000.0;
        try {
            FileOutputStream writer = new FileOutputStream(Database.accessUsersTable(), true);
            for (String s : Arrays.asList(
                    firstNameRegister.getText().toString(), ",",
                    lastNameRegister.getText().toString(),",",
                    emailRegister.getText().toString(), ",",
                    userNameRegister.getText().toString(), ",",
                    passwordRegister.getText().toString(), ",",
                    accountNumber, ",",
                    accountBalance.toString(), ",",
                    pinRegister.getText().toString(), "\n" )){
                writer.write(s.getBytes());
            }
            writer.close();
            Toast.makeText(activity.getApplicationContext(), "Registered Account", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(), "Account wasn't registered", Toast.LENGTH_SHORT).show();
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
    //Check to see if the password is Valid.
    private boolean isValidPassword(){
        //Not empty. Not less than 8 characters
        if(passwordRegister.getText().toString().length()<8){
            invalidRegistrationMessage.setText("Password must contain at least 8 characters.");
            return false;
        }
        //Password and Password Confirm match
        if (passwordRegister.getText().toString().equals(confirmPassRegister.getText().toString())) {
            return true;
        } else {
            invalidRegistrationMessage.setText("Passwords don't match.");
            return false;
        }
    }
    //Check if pin is valid
    private boolean isValidPin(){
        //Pins must be 4 in length and Pin and Pin confirm must match
        if (pinRegister.getText().toString().length()==4
                && pinRegister.getText().toString().equals(pinRegisterConfirm.getText().toString())) {
            return true;
        } else {
            //Check error to display correct message
            if(pinRegister.getText().toString().length()!=4){
                invalidRegistrationMessage.setText("User Pin must be composed of 4 numbers");
                return false;
            }
            if(!pinRegister.getText().toString().equals(pinRegisterConfirm.getText().toString())){
                invalidRegistrationMessage.setText("Pins don't match");
                return false;
            }

            return false;
        }
    }
    //No Null fields
    private boolean noNullFields(){
        if (firstNameRegister.getText().toString().isEmpty()) {
            invalidRegistrationMessage.setText("First Name can't be empty");
            return false;
        }else if(lastNameRegister.getText().toString().isEmpty()){
            invalidRegistrationMessage.setText("Last Name can't be empty");
            return false;
        }else if (emailRegister.getText().toString().isEmpty()){
            invalidRegistrationMessage.setText("Email can't be empty");
            return false;
        }else if(userNameRegister.getText().toString().isEmpty()){
            invalidRegistrationMessage.setText("Username can't be empty");
            return false;
        }
        //If filters passed, there are no null fields
        return true;
    }
    //No invalid characters (',') Will mess up the indexing if allowed
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
    //Own Regex. Emails must contain these characters
    private boolean iValidEmail(){
        if(emailRegister.getText().toString().contains("edu.ph")){
            return true;
        }
        //Emails must contain @ and .com
        if(!emailRegister.getText().toString().contains("@") || !emailRegister.getText().toString().contains(".com")){
            invalidRegistrationMessage.setText("Invalid Email");
            return false;
        }
        return true;
    }

    //Makes sure that an email only belongs to one account. Same goes for username and account number
    private boolean makeAccountUnique(){
        String line;
        String [] accountDetails; //0. firstName 1. lastName 2. email 3. username 4. password 5. Account Number
        try {
            Scanner scan = new Scanner(Database.accessUsersTable().getAbsoluteFile());
            while(scan.hasNextLine()){
                line = scan.nextLine();
                accountDetails = line.split(",");
                //Make sure email is unique
                if(accountDetails[2].equals(emailRegister.getText().toString())){
                    invalidRegistrationMessage.setText("Email belongs to another account.");
                    return false;
                }
                //Make sure username is unique
                if(accountDetails[3].equals(userNameRegister.getText().toString())){
                    invalidRegistrationMessage.setText("The username is already in use.");
                    return false;
                }
                //Make sure account number is unique
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
    //Generate a random account number of length 10
    private void generateAccountNumber(){
        Random rnd = new Random();
        Integer rand = 1000000000 + rnd.nextInt(900000000);
        String accountNumber = rand.toString();
        this.accountNumber=accountNumber;
    }
    //Persist login. Create a token/file that the app reads to check if there's a logged-in user.
    private void persistLogIn(){
        try {
            FileOutputStream fos = new FileOutputStream(Database.accessCurrentlyLoggedInUserFile());
            fos.write(userNameLogin.getText().toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //Clear text of all fields
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
