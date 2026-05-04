package com.se370group1.banking_system.service.strategy;

public class DepositValidationStrategy implements TransactionValidationStrategy {
    private static final double MAX_DEPOSIT_LIMIT = 10000.00;

    @Override
    public boolean isValid(double amount) {
        return amount > 0 && amount <= MAX_DEPOSIT_LIMIT;
    }
}