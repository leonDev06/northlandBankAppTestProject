package com.example.northlandbankmobile;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class TransactionReceipt {

    //Member variables
    private String id;
    protected String sender;
    protected String receiver;
    protected String amount;
    protected String transactType;
    protected String transactDate;
    protected String refNum;



    //Constructors
    public TransactionReceipt(
            String refNum, String sender, String receiver, String amount, String transactType, String transactDate){
        this.refNum = refNum; this.sender=sender; this.receiver=receiver; this.amount=amount;
        this.transactType=transactType; this.transactDate=transactDate;
    }
    public TransactionReceipt() {

    }

    //Getters
    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactType() {
        return transactType;
    }

    public String getTransactDate() {
        return transactDate;
    }

    public String getRefNum() {
        return refNum;
    }

    //Receipt functions
    public void storeToDatabase(){
        try {
            FileOutputStream fos = new FileOutputStream(Database.getTransactionsTable(), true);
            fos.write(refNum.getBytes());
            fos.write(",".getBytes());
            fos.write(sender.getBytes());
            fos.write(",".getBytes());
            fos.write(receiver.getBytes());
            fos.write(",".getBytes());
            fos.write(amount.getBytes());
            fos.write(",".getBytes());
            fos.write(transactType.getBytes());
            fos.write(",".getBytes());
            fos.write(transactDate.getBytes());
            fos.write("\n".getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
