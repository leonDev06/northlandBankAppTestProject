package com.example.northlandbankmobile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class UserTransactions extends TransactionReceipt {

    public UserTransactions(String refNum, String sender, String receiver, String amount, String transactType, String transactDate){
        super(refNum, sender, receiver, amount, transactType, transactDate);
        this.refNum = refNum; this.sender=sender; this.receiver=receiver; this.amount=amount;
        this.transactType=transactType; this.transactDate=transactDate;
    }

    @Override
    public void storeToDatabase() {
        try {
            FileOutputStream fos = new FileOutputStream(Database.accessUserTransactions(), true);
            for (String s : Arrays.asList(refNum, ",",
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
