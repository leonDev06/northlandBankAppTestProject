package com.example.northlandbankmobile;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String accountNumber;
    private String accountBalance;

    //Loan Variables (Couldn't decide whether to create a Loan Class or embed these here.)
    private Boolean hasActiveLoans;
    private String activeLoanAmount;
    private String activeLoanDateLoaned;
    private String activeLoanDateDue;
    private String activeLoanRefNum;

    //Getters and Setters for user details
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public String getAccountBalance() {
        return accountBalance;
    }

    //Getters and Setters for User's active loans
    public String getActiveLoanAmount() {
        return activeLoanAmount;
    }
    public String getActiveLoanDateLoaned() {
        return activeLoanDateLoaned;
    }
    public String getActiveLoanDateDue() {
        return activeLoanDateDue;
    }
    public String getActiveLoanRefNum() {
        return activeLoanRefNum;
    }

    //Constructors
    public User(){
        syncUserDetailsData();
        syncUserActiveLoanData();
    }

    //Refreshes this class' variables directly from the database
    public void syncUserDetailsData(){
        Database.prepareCurrentUserData();
        try {
            Scanner getUserData = new Scanner(Database.accessCurrentUserDataFile());
            String scannedLine;
            String[] userData;
            while (getUserData.hasNextLine()){
                scannedLine = getUserData.nextLine();
                userData = scannedLine.split(",");
                firstName = userData[0];
                lastName = userData[1];
                email = userData[2];
                userName = userData[3];
                accountNumber = userData[5];
                accountBalance = userData [6];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Get user's active loans data from the database
    private void syncUserActiveLoanData(){
        String user = Database.getCurrentUser();
        try {
            Scanner getLoansTable = new Scanner(Database.accessLoansTable());
            String scannedLine;
            String[] loans;

            while (getLoansTable.hasNextLine()){
                scannedLine = getLoansTable.nextLine();
                loans = scannedLine.split(",");
                if(loans[0].equals(user) && loans[2].equals("unpaid")){
                    activeLoanAmount = loans[1];
                    activeLoanDateLoaned = loans[3];
                    activeLoanDateDue = loans[4];
                    activeLoanRefNum = loans[5];
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Checks whether the user has active(unpaid) loans or not from the database.
    //Also serves as a getter for hasActiveLoans variable
    public boolean hasActiveLoans(){
        String user = Database.getCurrentUser();
        String scannedLine;
        String[] loansData;
        hasActiveLoans=false;
        try {
            Scanner scan = new Scanner(Database.accessLoansTable());
            while(scan.hasNextLine()){
                scannedLine = scan.nextLine();
                loansData = scannedLine.split(",");
                if(loansData[0].equals(user)){
                    if(loansData[2].equals("unpaid")){
                        hasActiveLoans = true;
                        break;
                    }else{
                        hasActiveLoans = false;
                    }
                }else {
                    hasActiveLoans = false;
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return hasActiveLoans;
    }

}
