package com.example.northlandbankmobile;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Database {
    private static final String TAG = "Database";
    //Initializes all database files needed throughout the application
    //(Users Table Indexes) 0. firstName 1. lastName 2. email 3. username 4. password 5. Account Number 6. Account Balance 7. Pin

    //Created on the first user's registration. Deleted/Updated everytime a transaction is made
    private static File mainDB = new File("/data/user/0/com.example.northlandbankmobile/files/MAIN_DB");
    //Created when a user logs in. (ActivityLogin Login Button Click). Deleted every logout event. (Activity Home Logout Button)
    private static File currentlyLoggedInUser = new File("/data/user/0/com.example.northlandbankmobile/files/currentLoggedUser");
    private static File currentUserData = new File("/data/user/0/com.example.northlandbankmobile/files/currentUserData");
    //Receipts Table
    private static File transactionsTable = new File("/data/user/0/com.example.northlandbankmobile/files/mainReceiptDb");


    //Loans Table
    private static File loansTable = new File("/data/user/0/com.example.northlandbankmobile/files/loansTable");
    private static File userLoans = new File("/data/user/0/com.example.northlandbankmobile/files/userLoans");

    //Single member variables. User data retrieved from the currentUserDatabase.
    private static String currentUser;
    private static String userBalance;

    //Getters for user database
    public static File getMainDB() {
        return mainDB;
    }
    public static File getCurrentlyLoggedInUserFile(){
        return currentlyLoggedInUser;
    }
    public static File getCurrentUserData() {
        return currentUserData;
    }

    //Getters for receipt database
    public static File getTransactionsTable() {
        return transactionsTable;
    }
    public static File getUserTransactions() {
        File userTransactions = new File("/data/user/0/com.example.northlandbankmobile/files/receiptsOf"+currentUser);
        return userTransactions;
    }

    //Getters for loans table
    public static File getLoansTable() {
        return loansTable;
    }
    public static File getUserLoans() {
        return userLoans;
    }

    //Getters for single variables
    public static String getCurrentUser() {
        String currentUser_="";
        try {
            Scanner getUser = new Scanner(Database.getCurrentlyLoggedInUserFile());
            currentUser_ = getUser.nextLine();
            setCurrentUser(currentUser_);
            getUser.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return currentUser;
    }
    public static String getUserBalance() {
        return userBalance;
    }
    // Setters for single variables
    public static void setCurrentUser(String s){
        currentUser=s;
    }
    public static void setUserBalance(String userBalance) {
        Database.userBalance = userBalance;
    }

    private static SharedPreferences sharedPreferences;

    //Constructors. Tho, not really needed because Database class is not treated as an object.
    public Database(){

    }

    //Database functions
    public static void initDatabase() {
        //Initialize main table
        if(!mainDB.exists()){
            try {
                Log.d(TAG, "initDatabase: DB Initialized");
                FileOutputStream fos = new FileOutputStream(mainDB);
                fos.write(" ".getBytes());
                fos.close();
            } catch (Exception e) {
                Log.d(TAG, "initDatabase: not initialized Main DB");
                e.printStackTrace();
            }
        }



        //Initialize loans table
        if(!loansTable.exists()){
            Log.d("checkCreate", "loansTable created.");
            try {
                FileOutputStream fos = new FileOutputStream(loansTable);
                fos.write(" ".getBytes());
                fos.close();
            } catch (Exception e) {
                Log.d(TAG, "initDatabase: Db not initialized");
                e.printStackTrace();
            }
        }

        //Initialize transactions table
        if(!transactionsTable.exists()){
            Log.d("checkCreate", "loansTable created.");
            try {
                FileOutputStream fos = new FileOutputStream(transactionsTable);
                fos.write(" ".getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Initialize userTransactions table and makes sure each user's transactions gets stored in their own file




    }

    public static void prepareCurrentUserData(){
        //This is for writing to the currentUserData file
        String accountDetails[];
        try {
            //If the currentUserData file has lines, delete the file to maintain file consistency.
            /*If not deleted, fos.append will keep writing lines to the file without replacing existing lines
            resulting in an inconsistent/corrupted userData
             */
            if(Database.getCurrentUserData()!=null){
                Database.getCurrentUserData().delete();
            }

            //Retrieves the user data line from the database and stores the user data in its own database file
            //This is done to avoid accessing the main database just to get the current user data
            FileOutputStream createUserData = new FileOutputStream(Database.getCurrentUserData(), true);
            Scanner scan = new Scanner(Database.getMainDB());
            while (scan.hasNextLine()){
                String currentLoopedUser = scan.nextLine();
                accountDetails = currentLoopedUser.split(",");

                if(getCurrentUser().equals(accountDetails[3])){
                    for(int i=0; i<accountDetails.length; i++){
                        createUserData.write(accountDetails[i].getBytes());
                        createUserData.write(",".getBytes());
                    }
                    setUserBalance(accountDetails[6]);

                }

            }
            Log.d("checkNameFile1", getUserTransactions().getAbsolutePath());
            Log.d("checkNameFile2", getUserTransactions().getAbsolutePath());
            scan.close();
            createUserData.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File commonDocumentDirPath(String name){
        File dir = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+name);
        }else{
            dir = new File(Environment.getExternalStorageDirectory()+"/"+name);
        }

        if(!dir.exists()){
            boolean success = dir.mkdirs();
            if(!success){
                dir = null;
            }
        }
        return dir;
    }
}
