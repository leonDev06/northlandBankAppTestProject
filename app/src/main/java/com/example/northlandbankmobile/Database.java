package com.example.northlandbankmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

//This will be the mock database of the whole application.
public final class Database {
    //Get the Context of the application that this Database will be working with
    private static Context context;

    //Single member variables. User data retrieved from the currentUserDatabase.
    private static String currentUser;
    private static String userBalance;

    //Getters for user database
    @NonNull
    public static File accessUsersTable() {
        File usersTable = new File("/data/user/0/com.example.northlandbankmobile/files","usersTable");
        return usersTable;
    }
    public static File accessUsersTable(Context context) {
        Database.context = context;
        File usersTable = new File(context.getFilesDir().getAbsolutePath(), "usersTable");
        return usersTable;
    }
    public static File accessCurrentlyLoggedInUserFile(){
        return new File(context.getFilesDir().getAbsolutePath(), "currentLoggedUser");
    }
    public static File accessCurrentUserDataFile() {
        return new File(context.getFilesDir().getAbsolutePath(),"currentUserData");
    }

    //Getters for receipt database
    public static File accessTransactionsTable() {
        return new File(context.getFilesDir().getAbsolutePath(),"mainReceiptDb");
    }
    public static File accessUserTransactions() {
        return new File("/data/user/0/com.example.northlandbankmobile/files/receiptsOf"+currentUser);
    }

    //Getters for loans table
    public static File accessLoansTable() {
        return new File(context.getFilesDir().getAbsolutePath(),"loansTable");
    }

    //Getters for single variables
    public static String getCurrentUser() {
        String currentUser_="";
        try {
            Scanner getUser = new Scanner(Database.accessCurrentlyLoggedInUserFile());
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

    //Hides the constructor. This class is not to be instantiated.
    private Database(){

    }

    //Database functions
    public static void onDatabaseFirstCreate(Context context) {
        //Initialize main table
        accessUsersTable().getParentFile().mkdirs();
        if(!accessUsersTable(context).exists()){
            try{
                accessUsersTable(context).createNewFile();
                accessTransactionsTable().createNewFile();
                accessLoansTable().createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void prepareCurrentUserData(){
        //This is for writing to the currentUserData file
        String[] accountDetails;
        try {
            //If the currentUserData file has lines, delete the file to maintain file consistency.
            /*If not deleted, fos.append will keep writing lines to the file without replacing existing lines
            resulting in an inconsistent/corrupted userData
             */
            if(Database.accessCurrentUserDataFile()!=null){
                Database.accessCurrentUserDataFile().delete();
            }

            //Retrieves the user data line from the database and stores the user data in its own database file
            //This is done to avoid accessing the main database just to get the current user data
            FileOutputStream createUserData = new FileOutputStream(Database.accessCurrentUserDataFile(), true);
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
            scan.close();
            createUserData.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File commonDocumentDirPath(String name){
        File dir;
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
