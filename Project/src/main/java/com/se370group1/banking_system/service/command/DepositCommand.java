package com.se370group1.banking_system.service.command;

import com.se370group1.banking_system.service.BankAccountService;

/**
 * Command Pattern - Concrete Command for Deposit
 *
 * Encapsulates a deposit operation as an object.
 * The BankAccountService is the "receiver" — it contains the actual deposit logic.
 * Wrapping it here lets the CommandInvoker trigger it without knowing the details.
 */
public class DepositCommand implements Command {

    private final BankAccountService bankAccountService;
    private final String accountId;
    private final double amount;

    public DepositCommand(BankAccountService bankAccountService, String accountId, double amount) {
        this.bankAccountService = bankAccountService;
        this.accountId = accountId;
        this.amount = amount;
    }

    @Override
    public boolean execute() {
        return bankAccountService.depositFunds(accountId, amount);
    }
}
