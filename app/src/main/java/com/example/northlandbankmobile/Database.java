package com.example.northlandbankmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

//This will be the mock database system of the whole application.
public final class Database {
    //The application context
    @SuppressLint("StaticFieldLeak") //Not a memory leak since this context will be the whole application context, not an activity context.
    private static Context context;

    //Who's currently accessing the database
    private static String currentUser;

    //DATABASE TABLES
    @NonNull
    public static File accessUsersTable() {
        //This is where the details of all users are stored
        return new File("/data/user/0/com.example.northlandbankmobile/files","usersTable");
    }
    public static File accessTransactionsTable() {
        //This is where all transactions of all users in the app are stored
        return new File(context.getFilesDir().getAbsolutePath(),"receiptTable");
    }
    public static File accessUserTransactions() {
        //Creates a unique userTransactionsTable for each user.
        return new File(context.getFilesDir().getAbsolutePath(), ("transactionsOf"+currentUser));
    }
    public static File accessLoansTable() {
        //This is where all the loans of all users are stored (Paid or unpaid)
        return new File(context.getFilesDir().getAbsolutePath(),"loansTable");
    }

    //DIRECTORIES
    public static File commonDocumentDirPath(String name){
        //This directory is used in printing PDFs
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

    //CACHES
    public static File accessCurrentlyLoggedInUserFile(){
        //This is for persisting user-login. If this file exists, it lets the app know that there's a currently logged in user.
        //This gets created every successful login and deleted every successful logout.
        return new File(context.getFilesDir().getAbsolutePath(), "currentLoggedUser");
    }
    public static File accessCurrentUserDataFile() {
        //This is for caching the user data in its own file instead of having to constantly access the main usersTable file
        return new File(context.getFilesDir().getAbsolutePath(),"currentUserData");
    }


    //METHODS
    public static File connectToDatabase(Context context) {
        /*Connect database class to the application context.
        The context that should be passed here should always ONLY be the application context to avoid memory leak.
        */
        Database.context = context;
        return new File(context.getFilesDir().getAbsolutePath(), "usersTable");
    }

    //Getters for single variables
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

    //Hides the constructor. This class is not to be instantiated.
    private Database(){
        //Hidden Constructor
    }

    //Database functions
    public static void onDatabaseFirstCreate(Context context) {
        //Initialize main table
        accessUsersTable().getParentFile().mkdirs();
        if(!connectToDatabase(context).exists()){
            try{
                connectToDatabase(context).createNewFile();
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
            Database.accessCurrentUserDataFile().delete();

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
}
