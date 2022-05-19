package com.example.northlandbankmobile;

import android.annotation.SuppressLint;
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
    //Tables
    public static File accessUsersTable() {
        //This is where the details of all users are stored
        File usersTable = new File("/data/user/0/com.example.northlandbankmobile/files/usersTable");
        return usersTable.getAbsoluteFile();
    }
    public static File accessTransactionsTable() {
        //This is where all transactions of all users in the app are stored
        File transactionsTable = new File("/data/user/0/com.example.northlandbankmobile/files/mainReceiptDb");
        return transactionsTable;
    }
    public static File accessUserTransactions() {
        //Creates a unique userTransactionsTable for each user.
        File userTransactions = new File("/data/user/0/com.example.northlandbankmobile/files/receiptsOf"
                                                                        +currentUser);
        return userTransactions;
    }
    public static File accessLoansTable() {
        //This is where all the loans of all users are stored (Paid or unpaid)
        File loansTable = new File("/data/user/0/com.example.northlandbankmobile/files/loansTable");
        return loansTable;
    }

    //Single member variables. User data retrieved from the currentUserDatabase.
    private static String currentUser;

    //Caches
    public static File accessCurrentlyLoggedInUserFile(){
        //This is for persisting user-login. If this file exists, it lets the app know that there's a currently logged in user.
        //This gets created every successful login and deleted every successful logout.
        File currentlyLoggedInUser = new File("/data/user/0/com.example.northlandbankmobile/files/currentLoggedUser");
        return currentlyLoggedInUser;
    }
    public static File accessCurrentUserDataFile() {
        //This is for caching the user data in its own file instead of having to constantly access the main usersTable file
        File currentUserData = new File("/data/user/0/com.example.northlandbankmobile/files/currentUserData");
        return currentUserData;
    }

    //Getters
    public static String getCurrentUser() {
        String currentUser_="";
        try {
            Scanner getUser = new Scanner(Database.accessCurrentlyLoggedInUserFile());
            currentUser_ = getUser.nextLine();
            currentUser = currentUser_;
            getUser.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return currentUser;
    }

    //Database functions
    public static void initDatabase() throws IOException {
        //Initialize main table
        accessUsersTable().createNewFile();
        accessLoansTable().createNewFile();
        accessTransactionsTable().createNewFile();

    }

    public static void checkDatabase(){
        if(!accessUsersTable().getAbsoluteFile().exists()){
            try {
                accessUsersTable().createNewFile();
                FileOutputStream fos = new FileOutputStream(accessUsersTable());
                fos.write("".getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){

            }
        }
    }


    public static void prepareCurrentUserData(){
        //This is for writing to the currentUserData file
        String[] accountDetails;
        try {
            //Delete to ensure file consistency
            /*If not deleted, fos.append will keep writing lines to the file without replacing existing lines
            resulting in an inconsistent/corrupted userData
             */
            Database.accessCurrentUserDataFile().delete();


            //Retrieves the user data line from the database and stores the user data in its own database file
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
                }
            }
            scan.close();
            createUserData.close();
        } catch (FileNotFoundException e) {
            Log.d("errorDatabase", "prepareCurrentUserData: FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("errorDatabase", "prepareCurrentUserData: IOException");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
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
