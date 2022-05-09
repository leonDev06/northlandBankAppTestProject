package com.example.northlandbankmobile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserTransactions extends TransactionReceipt {

    public UserTransactions(String refNum, String sender, String receiver, String amount, String transactType, String transactDate){
        this.refNum = refNum; this.sender=sender; this.receiver=receiver; this.amount=amount;
        this.transactType=transactType; this.transactDate=transactDate;
    }

    @Override
    public void storeToDatabase() {
        try {
            FileOutputStream fos = new FileOutputStream(Database.getUserTransactions(), true);
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
