package com.se370group1.banking_system.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.se370group1.banking_system.dto.TransactionDTO;

@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String transactionID;
    
    private String connectedBankAccountID;
    private String sourceEntity;
    private String details;
    private double amountDollars;
    private Date transactionDate;

    public Transaction(String transactionID, String connectedBankAccountID, String sourceEntity, String details, double amount, Date transactionDate) {
        this.transactionID = transactionID;
        this.connectedBankAccountID = connectedBankAccountID;
        this.sourceEntity = sourceEntity;
        this.details = details;
        this.amountDollars = amount;
        this.transactionDate = transactionDate;
    }

    public Transaction(TransactionDTO t_dto) {
        this.transactionID = t_dto.getTransactionID();
        this.connectedBankAccountID = t_dto.getConnectedBankAccount();
        this.sourceEntity = t_dto.getSourceEntity();
        this.details = t_dto.getDetails();
        this.amountDollars = t_dto.getAmountDollars();
        this.transactionDate = t_dto.getTransactionDate();
    }
    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String transactionID) { this.transactionID = transactionID; }

    public String getConnectedBankAccountID() { return connectedBankAccountID; }
    public void setConnectedBankAccountID(String connectedBankAccountID) { this.connectedBankAccountID = connectedBankAccountID; }

    public String getSourceEntity() { return sourceEntity; }
    public void setSourceEntity(String sourceEntity) { this.sourceEntity = sourceEntity; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public double getAmountDollars() { return amountDollars; }
    public void setAmountDollars(double amountDollars) { this.amountDollars = amountDollars; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate;
    }
}
