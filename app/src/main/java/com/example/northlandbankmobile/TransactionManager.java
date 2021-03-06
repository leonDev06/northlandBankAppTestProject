package com.example.northlandbankmobile;

import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

//This class is responsible for handling all Transactions of the user (Send Money, Loan, etc.)
public class TransactionManager {
    //Member variables
    private boolean transactionSuccess;
    private boolean noUnpaidLoans;
    private User user;
    private String referenceNumber;

    //Helper Classes
    private DateManager dateManager;
    //Responsible for Generating receipt and storing them to the database
    private TransactionReceipt receipt;
    private UserTransactions userTransactions;

    //Constructors
    public TransactionManager(){
        dateManager = new DateManager();
        user = new User();
    }

    //Getters
    public boolean isTransactionSuccess() {
        return transactionSuccess;
    }
    public String getReferenceNumber() {
        //Commonly used to pass the reference number of a transaction from the transaction screen to the verify and comfirmation screen
        return referenceNumber;
    }

    //Transaction Methods
    //Handles Money Transactions of the user

    //Allows the user to send money to another person, whether said person is a user of this app or not. (Found in database or not.)
    public void sendMoney(String receiver, String amount){
        String sender;
        double amount_ = Double.parseDouble(amount);
        try {
            //Retrieves the username of the sender
            sender = user.getUserName();

            boolean enoughBalance = Double.parseDouble(user.getAccountBalance()) >= amount_;

            /*Sends money to the recipient and deducts the amount from the sender
            This is done by searching the whole database for both the receiver and the sender's username
            The Database is then updated with both the user's new respective balance in their account
             */
            if(enoughBalance){
                File temp = new File("/data/user/0/com.example.northlandbankmobile/files/tempMainDB");
                Scanner usersTable = new Scanner(Database.accessUsersTable());
                FileOutputStream fos = new FileOutputStream(temp, true);

                String[] userData;
                while(usersTable.hasNextLine()){

                    String line = usersTable.nextLine();
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
                //Replace usersTable file with the TEMP one
                Database.accessUsersTable().delete();
                temp.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/usersTable"));

                //Generate referenceNumber for this transaction
                referenceNumber = generateRefNum();
                //Generate Receipt to mainReceiptTable and userReceiptTable
                generateReceipt(referenceNumber, sender, receiver, amount, "SEND MONEY", dateManager.getCurrentDate().toString());

                //Close resources
                usersTable.close();
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

    //Allows the user to make a loan from the system.
    // Amount not to exceed 10kPhp, has 14 days due date, only one active loan per user.
    //The user will first have to pay their existing loan if they want to make another loan.
    public void loanCredits(String userInputAmount){
        //Data vital for making the loan
        Integer amountLoan = Integer.parseInt(userInputAmount);
        String username = user.getUserName();
        String refNum;
        user = new User();

        //Check if the user has unpaid loans
        noUnpaidLoans = noUnpaidLoans();

        //Gets date and the would be due date of the loan.
        String dateLoaned = dateManager.getCurrentDate().toString();
        String dateDue="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateDue = dateManager.getCurrentDate().plusDays(14).toString();
        }

        //Creates temp file where the updated usersTable will be stored
        //Retrieves and reads the users table from the Database
        try {
            File temp = new File("/data/user/0/com.example.northlandbankmobile/files/tempMainDB");
            Scanner usersDataTable = new Scanner(Database.accessUsersTable());
            FileOutputStream fos = new FileOutputStream(temp, true);

            //Check if the loan is valid. (Amount less than 10kPhp and user has no unpaid loans
            if(amountLoan<=10000 && noUnpaidLoans){
                String scannedLine;
                String[] userDataIndexOf;
                String updatedUserData;

                //Updates the usersTable with the new available balance of the user
                while(usersDataTable.hasNextLine()){
                    //Re-initializes each variable per scanned line
                    scannedLine = usersDataTable.nextLine();
                    userDataIndexOf = scannedLine.split(",");
                    updatedUserData="";

                    //Not user. Simply copy to the temp file without modifying data
                    if(!userDataIndexOf[3].equals(username)){
                        fos.write(scannedLine.getBytes());
                    }else{
                        //The user is found in the database. Update user's data (update account balance)
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
                //Delete outdated users table and replace it with the updated (TEMP) one.
                Database.accessUsersTable().delete();
                temp.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/usersTable"));
                usersDataTable.close();
                fos.close();
            }else{
                transactionSuccess=false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Used to pay any active loans the user may have. Called automatically if the user's active loan is past due date.
    //Updates 2 tables. The usersTable to deduct balance and the loansTable to change from unpaid to paid.
    public void payUnpaidLoan(){
        File updatedLoansTable = new File("/data/user/0/com.example.northlandbankmobile/files/tempLoansTable");
        String user = Database.getCurrentUser();
        Integer amount = null;
        //Loans Table
        try {
            Scanner loansTable = new Scanner(Database.accessLoansTable());
            FileOutputStream loansTableUpdater = new FileOutputStream(updatedLoansTable, true);
            String scannedLine;
            String[] loans;
            String updatedData="";
            //Update the loansTable. Change "unpaid" to "paid."
            while (loansTable.hasNextLine()){
                scannedLine = loansTable.nextLine();
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
            //Generate Receipt for the payment transaction
            generateReceipt(generateRefNum(), user, "SYSTEM", amount.toString(), "PAY LOAN",
                    dateManager.getCurrentDate().toString());

            //Close scanner resource. Nulls value of variables to prepare for re-use.
            //Delete outdated loansTable and replace it with the updated (TEMP) one.
            Database.accessLoansTable().delete();
            updatedLoansTable.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/loansTable"));
            loansTable.close();
            updatedData = "";

            //End of Loans Table

            //usersTable
            //Updates the usersTable. Deducts the paid amount to user balance.
            Scanner usersTable = new Scanner(Database.accessUsersTable());
            File updatedUsersTable = new File("/data/user/0/com.example.northlandbankmobile/files/accountsUpdating");
            FileOutputStream accountsTableUpdater = new FileOutputStream(updatedUsersTable, true);
            while(usersTable.hasNextLine()){
                scannedLine = usersTable.nextLine();
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
            //Deletes outdated usersTable and update it with the updated one
            Database.accessUsersTable().delete();
            updatedUsersTable.renameTo(new File("/data/user/0/com.example.northlandbankmobile/files/usersTable"));
            accountsTableUpdater.close();
            transactionSuccess=true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Checks whether the user has unpaid loans.
    public boolean noUnpaidLoans(){
        String user = Database.getCurrentUser();
        String scannedLine;
        String[] loansData;
        noUnpaidLoans=true;
        try {
            Scanner scan = new Scanner(Database.accessLoansTable());
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

    //Check if entered amount digits are valid
    //Pass through a series of filters/test. Immediately fail if even just one test is failed.
    public boolean isValidAmount(String amount){
        int decimalPointCount=0;
        int decimalPointPosition=0;

        //Amount can't be empty (Mainly used to avoid NullPointerException
        //This is returning true just so that the lines below will be unreachable if amount TextField is empty (NullPointerException).
        //It's not returning false because it will cause problems in showing the correct error message to the user.
        if(amount.isEmpty()){
            return true;
        }
        //Loop through each character in the amount TextView
        for(int i=0; i<amount.length(); i++){
            //Only allow digits or a decimal points.
            if(!Character.isDigit(amount.charAt(i)) && amount.charAt(i)!='.'){
                return  false;
            }
            //Check if the number is a decimal. Permits only 1 decimal point aka a dot.
            if(i>=1 && amount.charAt(i)=='.'){
                decimalPointCount++;
                if (decimalPointCount>1){
                    return false;
                }
                //Only allow whole numbers in tens value to have a decimal
            }else if(i<2 && amount.charAt(i)=='.'){
                return false;
            }//Prevents '.' at the end of the amount
            else if(amount.charAt(amount.length()-1)=='.'){
                return false;
            }
            //Makes sure that, if there are decimals, that there would only be 1 to 2 decimal places
            if(amount.charAt(i)=='.'){
                decimalPointPosition=i;
                if(amount.length()-(decimalPointPosition+1) > 2){
                    return false;
                }
            }
        }
        //First digit must be a whole number. Fails if '.'
        if(amount.charAt(0)=='0' || amount.charAt(0)=='.'){
            return false;
        }
        //If the amount passed all the filters, the amount is valid. Return true.
        return true;
    }

    //PRIVATE METHODS/HELPER METHODS

    //Generate Reference Number for the Transaction made.
    //All transaction numbers are randomly generated, but is made sure to be unique for each transaction.
    private String generateRefNum(){
        //Generates a random number to be used for ReferenceNumber
        Random rnd = new Random();
        Integer rand = 1000000000 + rnd.nextInt(900000000);
        String refNum = rand.toString();

        //Makes sure that the reference number is unique within the database
        try {
            Scanner transactionTable = new Scanner(Database.accessTransactionsTable());
            String scannedLine;
            String[] transaction;
            //The generated refNum is already unique if it's the first-ever record in the transactionTable
            if(!transactionTable.hasNextLine()){
                return refNum;
            }
            //Generates a new refNum if the refNum is already existing in the transactionTable (not unique)
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

    //Used to generate the receipt of the Transaction
    //Stores it in 2 Tables. User-exclusive Transaction table and All transactions made by all users
   private void generateReceipt(String refNum, String sender, String receiver,
                                String amount, String transactType, String transactDate){
        //Store to MainReceiptTable
        receipt = new TransactionReceipt(refNum, sender, receiver, amount, transactType, transactDate);
        receipt.storeToDatabase();
        receipt = null;
        //Store to user transaction table. Used to display user's past transactions (Transaction History)
        userTransactions = new UserTransactions(refNum, sender, receiver, amount, transactType, transactDate);
        userTransactions.storeToDatabase();
        userTransactions = null;
   }

    //Called to save the loan transaction to the database.
    private void writeToLoansTable(String user, String amount, String dateLoaned, String dateDue, String refNum){
        try {
            FileOutputStream fos = new FileOutputStream(Database.accessLoansTable(), true);
            for (String s : Arrays.asList(
                    user, ",",
                    amount, ",",
                    "unpaid", ",",
                    dateLoaned, ",",
                    dateDue, ",",
                    refNum, "\n")) {
                fos.write(s.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}