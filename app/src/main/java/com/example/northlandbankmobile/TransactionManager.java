package com.example.northlandbankmobile;

import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

//This class is responsible for handling all Transactions of the user (Send Money, Loan, etc.)
public class TransactionManager {
    private DateManager dateManager;

    //Member variables
    private boolean transactionSuccess;
    private boolean noUnpaidLoans;
    private User user;

    //Responsible for Generating receipt and storing them to the database
    private TransactionReceipt receipt;
    private UserTransactions userTransactions;

    public TransactionManager(Activity activity){
        dateManager = new DateManager();
    }
    public TransactionManager(){
        dateManager = new DateManager();
    }

    public boolean isTransactionSuccess() {
        return transactionSuccess;
    }
    public boolean hasNoUnpaidLoans() {
        return noUnpaidLoans;
    }

    public void sendMoney(String receiver, String amount){
        String sender;
        double amount_ = Double.parseDouble(amount);
        try {
            //Retrieves the username of the sender
            Scanner getSender = new Scanner(Database.getCurrentUserData());
            sender = getSender.nextLine();
            String[] senderData = sender.split(",");
            sender = senderData[3];
            getSender.close();

            boolean enoughBalance = Double.parseDouble(senderData[6]) >= amount_;

            /*Sends money to the recipient and deducts the amount from the sender
            This is done by searching the whole database for both the receiver and the sender's username
            The Database is then updated with both the user's new respective balance in their account
             */
            if(enoughBalance){
                File temp = new File("/data/user/0/com.example.northlandbankmobile/files/tempMainDB");
                Scanner scan = new Scanner(Database.getMainDB());
                FileOutputStream fos = new FileOutputStream(temp, true);

                String[] userData;
                while(scan.hasNextLine()){

                    String line = scan.nextLine();
                    userData = line.split(",");
                    String updatedUserData="";

                    //Neither user nor sender. Simply copy to the new file
                    if(!userData[3].equals(receiver) && !userData[3].equals(sender)){
                        fos.write(line.getBytes());
                    }
                    //Receiver found in database. Updates receiver data in the temp file
                    else if (userData[3].equals(receiver)){
                        Double newBalance;
                        double balance = Double.parseDouble(userData[6]);
                        newBalance = balance+amount_;
                        userData[6]=newBalance.toString();
                        for(int i=0; i<userData.length; i++){
                            updatedUserData=updatedUserData.concat(userData[i]+",");
                        }
                        fos.write(updatedUserData.getBytes());
                    }
                    //Sender found in database. Updates the sender data in the temp file
                    else if(userData[3].equals(sender)){
                        Double newBalance;
                        double balance = Double.parseDouble(userData[6]);
                        newBalance = balance - amount_;
                        userData[6]=newBalance.toString();
                        for(int i=0; i<userData.length; i++){
                            updatedUserData=updatedUserData.concat(userData[i]+",");
                        }
                        fos.write(updatedUserData.getBytes());
                    }
                    fos.write("\n".getBytes());
                }
                //Replace mainDb file with the TEMP one
                Database.getMainDB().delete();
                temp.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/MAIN_DB"));

                //Generate Receipt to mainReceiptTable and userReceiptTable
                generateReceipt(generateRefNum(), sender, receiver, amount, "SEND MONEY", dateManager.getCurrentDate().toString());

                //Close resources
                scan.close();
                fos.close();

                transactionSuccess =true;
            }else{
                transactionSuccess=false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("errorFNF", "Temp File or Main DB file not found.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("errorIOE", "Error writing Temp File / Error reading MainDB file");
        }


    }
    public void loanCredits(String userInputAmount){
        Integer amountLoan = Integer.parseInt(userInputAmount);
        String username = Database.getCurrentUser();
        String refNum;
        user = new User();

        noUnpaidLoans = noUnpaidLoans();


        String dateLoaned = dateManager.getCurrentDate().toString();
        String dateDue="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateDue = dateManager.getCurrentDate().plusDays(14).toString();
        }

        try {
            File temp = new File("/data/user/0/com.example.northlandbankmobile/files/tempMainDB");
            Scanner usersDataTable = new Scanner(Database.getMainDB());
            FileOutputStream fos = new FileOutputStream(temp, true);

            if(amountLoan<=10000 && noUnpaidLoans){
                String scannedLine;
                String[] userDataIndexOf;
                String updatedUserData;


                while(usersDataTable.hasNextLine()){
                    //Re-initializes each variable per scanned line
                    scannedLine = usersDataTable.nextLine();
                    userDataIndexOf = scannedLine.split(",");
                    updatedUserData="";

                    if(!userDataIndexOf[3].equals(username)){
                        fos.write(scannedLine.getBytes());
                    }else{
                        Double updatedBalance;
                        double balance = Double.parseDouble(userDataIndexOf[6]);
                        updatedBalance = balance+amountLoan;
                        userDataIndexOf[6]=updatedBalance.toString();
                        for(int i=0; i<userDataIndexOf.length; i++){
                            updatedUserData=updatedUserData.concat(userDataIndexOf[i]+",");
                        }
                        fos.write(updatedUserData.getBytes());
                    }
                    fos.write("\n".getBytes());
                }
                refNum = generateRefNum();
                /*
                Deletes the usersTable
                Replace it with the updated "Temp" Table by renaming TEMP table to usersData.csv
                 */
                transactionSuccess=true;
                writeToLoansTable(username, amountLoan.toString(), dateLoaned, dateDue, refNum);
                generateReceipt(refNum, "SYSTEM", username, amountLoan.toString(), "LOAN", dateLoaned);
                Database.getMainDB().delete();
                temp.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/MAIN_DB"));
                usersDataTable.close();
                printFileDataMulti("loans",Database.getLoansTable());
            }else{
                transactionSuccess=false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void payExistingLoan(){
        File updatedLoansTable = new File("/data/user/0/com.example.northlandbankmobile/files/tempLoansTable");
        String user = Database.getCurrentUser();
        Integer amount = null;
        try {
            Scanner getLoansTable = new Scanner(Database.getLoansTable());
            FileOutputStream loansTableUpdater = new FileOutputStream(updatedLoansTable, true);
            String scannedLine;
            String[] loans;
            String updatedData="";
            //Update the loansTable. Change "unpaid" to "paid."
            while (getLoansTable.hasNextLine()){
                scannedLine = getLoansTable.nextLine();
                loans = scannedLine.split(",");
                updatedData="";

                if(loans[0].equals(user) && loans[2].equals("unpaid")){
                    loans[2] = "paid";
                    for (int i=0; i<loans.length; i++){
                        updatedData=updatedData.concat(loans[i]);
                        updatedData=updatedData.concat(",");
                        amount = Integer.parseInt(loans[1]);
                    }
                    loansTableUpdater.write(updatedData.getBytes());
                }else{
                    loansTableUpdater.write(scannedLine.getBytes());
                }
                loansTableUpdater.write("\n".getBytes());
            }
            //Generate Receipts
            generateReceipt(generateRefNum(), user, "SYSTEM", amount.toString(), "PAY LOAN",
                    dateManager.getCurrentDate().toString());

            //Close scanner resource. Nulls value of variables to prepare for re-use.
            Database.getLoansTable().delete();
            updatedLoansTable.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/loansTable"));
            getLoansTable.close();
            updatedData = "";

            //Updates the accountsTable. Deducts the paid amount to user balance.
            Scanner getAccountsTable = new Scanner(Database.getMainDB());
            File updatedAccountsTable = new File("/data/user/0/com.example.northlandbankmobile/files/accountsUpdating");
            FileOutputStream accountsTableUpdater = new FileOutputStream(updatedAccountsTable, true);
            while(getAccountsTable.hasNextLine()){
                scannedLine = getAccountsTable.nextLine();
                String[] userData = scannedLine.split(",");
                updatedData="";
                if(userData[3].equals(user)){
                    Double newBalance;
                    double balance = Double.parseDouble(userData[6]);
                    newBalance = balance - amount;
                    userData[6]=newBalance.toString();
                    for(int i=0; i< userData.length; i++){
                        updatedData=updatedData.concat(userData[i]);
                        updatedData=updatedData.concat(",");
                    }
                    accountsTableUpdater.write(updatedData.getBytes());
                }else{
                    accountsTableUpdater.write(scannedLine.getBytes());
                }
                accountsTableUpdater.write("\n".getBytes());
            }
            //generateReceipt();
            Database.getMainDB().delete();
            updatedAccountsTable.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/MAIN_DB"));
            accountsTableUpdater.close();
            transactionSuccess=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void generateReceipt(String refNum, String sender, String receiver, String amount, String transactType, String transactDate){
        receipt = new TransactionReceipt(refNum, sender, receiver, amount, transactType, transactDate);
        receipt.storeToDatabase();
        receipt = null;

        userTransactions = new UserTransactions(refNum, sender, receiver, amount, transactType, transactDate);
        userTransactions.storeToDatabase();
        userTransactions = null;


   }

    //Loans methods
    private void writeToLoansTable(String user, String amount, String dateLoaned, String dateDue, String refNum){

        try {
            FileOutputStream fos = new FileOutputStream(Database.getLoansTable(), true);
            fos.write(user.getBytes());
            fos.write(",".getBytes());
            fos.write(amount.getBytes());
            fos.write(",".getBytes());
            fos.write("unpaid".getBytes());
            fos.write(",".getBytes());
            fos.write(dateLoaned.getBytes());
            fos.write(",".getBytes());
            fos.write(dateDue.getBytes());
            fos.write(",".getBytes());
            fos.write(refNum.getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public boolean noUnpaidLoans(){
        String user = Database.getCurrentUser();
        String scannedLine;
        String[] loansData;
        noUnpaidLoans=true;
        try {
            Scanner scan = new Scanner(Database.getLoansTable());
            while(scan.hasNextLine()){
                scannedLine = scan.nextLine();
                loansData = scannedLine.split(",");
                if(loansData[0].equals(user)){
                    if(loansData[2].equals("unpaid")){
                        noUnpaidLoans = false;
                        break;
                    }else{
                        noUnpaidLoans = true;
                    }

                }else {
                    noUnpaidLoans = true;
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return noUnpaidLoans;
    }

    private String generateRefNum(){
        //Generates a random number to be used for ReferenceNumber
        Random rnd = new Random();
        Integer rand = 1000000000 + rnd.nextInt(900000000);
        String refNum = rand.toString();


        //Makes sure that the reference number is unique within the database
        try {
            Scanner transactionTable = new Scanner(Database.getTransactionsTable());
            String scannedLine;
            String[] transaction;
            //The generated refNum is already unique if it's the first-ever record in the transactionTable
            if(!transactionTable.hasNextLine()){
                return refNum;
            }
            //Generates a new refNum if the refNum is already existing in the transactionTable
            while(transactionTable.hasNextLine()){
                scannedLine = transactionTable.nextLine();
                transaction = scannedLine.split(",");
                if(transaction[0].equals(refNum)){
                    rand = 1000000000 + rnd.nextInt(900000000);
                }else{
                    refNum = rand.toString();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return refNum;
    }



    //FOR TESTING/DEBUGGING PURPOSES METHODS
    public void printFileDataMulti(String tag, File file){
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                Log.d(tag, line);
            }
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printFileDataSingle(String tag, File file){
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] array = line.split(",");
                for(int i=0; i<array.length; i++){
                    Log.d(tag, array[i]);
                }
            }
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}