package com.example.northlandbankmobile;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Database {
    //Single member variables. User data retrieved from the currentUserDatabase.
    private static String currentUser;
    private static String userBalance;

    //Getters for user database
    @NonNull
    public static File accessUsersTable() {
        File mainDB = new File("/data/user/0/com.example.northlandbankmobile/files/usersTable");
        return mainDB;
    }
    public static File getCurrentlyLoggedInUserFile(){
        File currentlyLoggedInUser = new File("/data/user/0/com.example.northlandbankmobile/files/currentLoggedUser");
        return currentlyLoggedInUser;
    }
    public static File getCurrentUserDataFile() {
        File currentUserData = new File("/data/user/0/com.example.northlandbankmobile/files/currentUserData");
        return currentUserData;
    }
    //Getters for receipt database
    public static File accessTransactionsTable() {
        File transactionsTable = new File("/data/user/0/com.example.northlandbankmobile/files/mainReceiptDb");
        return transactionsTable;
    }
    public static File accessUserTransactions() {
        File userTransactions = new File("/data/user/0/com.example.northlandbankmobile/files/receiptsOf"+currentUser);
        return userTransactions;
    }
    //Getters for loans table
    public static File getLoansTable() {
        File loansTable = new File("/data/user/0/com.example.northlandbankmobile/files/loansTable");
        return loansTable;
    }
    public static File getUserLoans() {
        File userLoans = new File("/data/user/0/com.example.northlandbankmobile/files/userLoans");
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


    //Constructors. Tho, not really needed because Database class is not treated as an object.
    public Database(){

    }

    //Database functions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initDatabase() throws IOException {
        //Initialize main table
        accessUsersTable().createNewFile();
        getLoansTable().createNewFile();
        accessTransactionsTable().createNewFile();



    }

    public static void prepareCurrentUserData(){
        //This is for writing to the currentUserData file
        String accountDetails[];
        try {
            //If the currentUserData file has lines, delete the file to maintain file consistency.
            /*If not deleted, fos.append will keep writing lines to the file without replacing existing lines
            resulting in an inconsistent/corrupted userData
             */
            if(Database.getCurrentUserDataFile()!=null){
                Database.getCurrentUserDataFile().delete();
            }

            //Retrieves the user data line from the database and stores the user data in its own database file
            //This is done to avoid accessing the main database just to get the current user data
            FileOutputStream createUserData = new FileOutputStream(Database.getCurrentUserDataFile(), true);
            Scanner scan = new Scanner(Database.accessUsersTable());
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
            Log.d("checkNameFile1", accessUserTransactions().getAbsolutePath());
            Log.d("checkNameFile2", accessUserTransactions().getAbsolutePath());
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
