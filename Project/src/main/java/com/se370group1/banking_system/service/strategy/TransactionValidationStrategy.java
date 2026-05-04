package com.se370group1.banking_system.service.strategy;

public interface TransactionValidationStrategy {
    boolean isValid(double amount);
}