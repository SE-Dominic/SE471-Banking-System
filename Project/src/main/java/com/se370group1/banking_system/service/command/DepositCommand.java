package com.se370group1.banking_system.service.command;

import com.se370group1.banking_system.service.BankAccountService;

public class DepositCommand implements Command {
    private final BankAccountService bankAccountService;
    private final String targetAccountID;
    private final double amount;

    public DepositCommand(BankAccountService bankAccountService, String targetAccountID, double amount) {
        this.bankAccountService = bankAccountService;
        this.targetAccountID = targetAccountID;
        this.amount = amount;
    }

    @Override
    public boolean execute() {
        return bankAccountService.depositFunds(targetAccountID, amount);
    }
}
