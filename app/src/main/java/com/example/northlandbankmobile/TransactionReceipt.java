package com.example.northlandbankmobile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
    private TransactionReceipt (){
        //Hidden Constructor
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
            FileOutputStream fos = new FileOutputStream(Database.accessTransactionsTable(), true);
            for (String s : Arrays.asList(
                    refNum, ",",
                    sender, ",",
                    receiver, ",",
                    amount, ",",
                    transactType, ",",
                    transactDate, "\n")) {
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
