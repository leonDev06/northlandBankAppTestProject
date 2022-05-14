package com.example.northlandbankmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReceiptViewAdapter extends RecyclerView.Adapter<ReceiptViewAdapter.MyViewHolder>  {
    //The data that will be used to display in each recycler view. (Contains each transaction detail.)
    private ArrayList<UserTransactions> receipts= new ArrayList<UserTransactions>();

    //Parameters for Constructor
    private Context context;

    //Constructor
    public ReceiptViewAdapter(Context context, ArrayList<UserTransactions> receipts){
        this.context = context; this.receipts = receipts;
    }

    @NonNull
    @Override
    public ReceiptViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_receipts_view, parent, false);
        return new ReceiptViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewAdapter.MyViewHolder holder, int position) {
        //Responsible for displaying respective data in each item
        holder.refNum.setText(receipts.get(receipts.size() -position-1).getRefNum());
        holder.sender.setText(receipts.get(receipts.size() -position-1).getSender());
        holder.receiver.setText(receipts.get(receipts.size() -position-1).getReceiver());
        holder.amount.setText(receipts.get(receipts.size() -position-1).getAmount());
        holder.transactDate.setText(receipts.get(receipts.size() -position-1).getTransactDate());
        holder.transactType.setText(receipts.get(receipts.size() -position-1).getTransactType());
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView refNum, sender, receiver, amount, transactDate, transactType;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            refNum = itemView.findViewById(R.id.rRefNum);
            sender = itemView.findViewById(R.id.rSender);
            receiver = itemView.findViewById(R.id.rReceiver);
            amount = itemView.findViewById(R.id.rAmount);
            transactDate = itemView.findViewById(R.id.rDate);
            transactType = itemView.findViewById(R.id.rType);
        }
    }
}
