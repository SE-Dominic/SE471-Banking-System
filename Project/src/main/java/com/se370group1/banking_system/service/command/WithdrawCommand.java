package com.se370group1.banking_system.service.command;

import com.se370group1.banking_system.service.BankAccountService;

public class WithdrawCommand implements Command {

    private final BankAccountService service;
    private final String accountId;
    private final double amount;

    public WithdrawCommand(BankAccountService service, String accountId, double amount) {
        this.service = service;
        this.accountId = accountId;
        this.amount = amount;
    }

    @Override
    public boolean execute() {
        return service.withdrawFunds(accountId, amount);
    }
}