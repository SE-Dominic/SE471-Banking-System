package com.se370group1.banking_system.service.command;

import com.se370group1.banking_system.service.BankAccountService;

public class WithdrawCommand implements Command {
    private final BankAccountService bankAccountService;
    private final String sourceAccountID;
    private final double amount;

    public WithdrawCommand(BankAccountService bankAccountService, String sourceAccountID, double amount) {
        this.bankAccountService = bankAccountService;
        this.sourceAccountID = sourceAccountID;
        this.amount = amount;
    }

    @Override
    public boolean execute() {
        return bankAccountService.withdrawFunds(sourceAccountID, amount);
    }
}