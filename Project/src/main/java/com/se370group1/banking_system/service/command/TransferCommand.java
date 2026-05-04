package com.se370group1.banking_system.service.command;

import com.se370group1.banking_system.service.BankAccountService;

public class TransferCommand implements Command {
    private final BankAccountService bankAccountService;
    private final String sourceAccountID;
    private final String targetAccountID;
    private final double amount;

    public TransferCommand(BankAccountService bankAccountService,
                           String sourceAccountID,
                           String targetAccountID,
                           double amount) {
        this.bankAccountService = bankAccountService;
        this.sourceAccountID = sourceAccountID;
        this.targetAccountID = targetAccountID;
        this.amount = amount;
    }

    @Override
    public Boolean execute() {
        return bankAccountService.transferFunds(sourceAccountID, targetAccountID, amount);
    }
}