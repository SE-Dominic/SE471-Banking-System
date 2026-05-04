package com.se370group1.banking_system.service.strategy;

public class WithdrawValidationStrategy implements TransactionValidationStrategy {
    private final double accountBalance;

    public WithdrawValidationStrategy(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public boolean isValid(double amount) {
        return amount > 0 && amount <= accountBalance;
    }
}